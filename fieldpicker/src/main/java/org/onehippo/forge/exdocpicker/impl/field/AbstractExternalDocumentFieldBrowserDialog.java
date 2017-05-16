/**
 * Copyright 2014 Hippo B.V. (http://www.onehippo.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onehippo.forge.exdocpicker.impl.field;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.value.IValueMap;
import org.apache.wicket.util.value.ValueMap;
import org.hippoecm.frontend.dialog.AbstractDialog;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentCollection;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceContext;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceFacade;
import org.onehippo.forge.exdocpicker.api.PluginConstants;
import org.onehippo.forge.exdocpicker.impl.SimpleExternalDocumentCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractExternalDocumentFieldBrowserDialog
        extends AbstractDialog<ExternalDocumentCollection<Serializable>> {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(AbstractExternalDocumentFieldBrowserDialog.class);

    private final IModel<String> titleModel;
    protected final ExternalDocumentServiceFacade<Serializable> exdocService;

    protected final Set<Serializable> selectedExtDocs = new LinkedHashSet<Serializable>();

    protected ExternalDocumentCollection<Serializable> currentDocSelection;
    protected ExternalDocumentCollection<Serializable> searchedDocCollection = new SimpleExternalDocumentCollection<Serializable>();

    protected int pageSize;

    private final IValueMap dialogSize;

    protected final ExternalDocumentServiceContext extDocServiceContext;

    public AbstractExternalDocumentFieldBrowserDialog(IModel<String> titleModel,
            final ExternalDocumentServiceContext extDocServiceContext,
            final ExternalDocumentServiceFacade<Serializable> exdocService,
            IModel<ExternalDocumentCollection<Serializable>> model) {
        super(model);
        setOutputMarkupId(true);

        this.titleModel = titleModel;
        this.extDocServiceContext = extDocServiceContext;
        this.exdocService = exdocService;

        final String dialogSizeParam = getPluginConfig().getString(PluginConstants.PARAM_DIALOG_SIZE,
                PluginConstants.DEFAULT_DIALOG_SIZE);
        dialogSize = new ValueMap(dialogSizeParam).makeImmutable();

        pageSize = getPluginConfig().getInt(PluginConstants.PARAM_PAGE_SIZE, PluginConstants.DEFAULT_PAGE_SIZE);

        currentDocSelection = getModelObject();

        selectedExtDocs.clear();

        for (Iterator<? extends Serializable> it = currentDocSelection.iterator(); it.hasNext();) {
            selectedExtDocs.add(it.next());
        }

        if (getModel().getObject() == null) {
            setOkVisible(false);
            setOkEnabled(false);
        }

        doInitialSearchOnExternalDocuments();
        initDataListViewUI();
    }

    @Override
    protected void onOk() {
        if (selectedExtDocs != null) {
            if (isSingleSelectionMode()) {
                currentDocSelection.clear();
                Serializable curDoc = null;
                // when single selection mode, let's add the last added item only.
                for (Iterator<Serializable> it = selectedExtDocs.iterator(); it.hasNext();) {
                    curDoc = it.next();
                }
                if (curDoc != null) {
                    currentDocSelection.add(curDoc);
                }
                exdocService.setFieldExternalDocuments(extDocServiceContext, currentDocSelection);
            } else {
                boolean added = false;

                for (Serializable doc : selectedExtDocs) {
                    if (!currentDocSelection.contains(doc)) {
                        currentDocSelection.add(doc);
                        added = true;
                    }
                }

                if (added) {
                    exdocService.setFieldExternalDocuments(extDocServiceContext, currentDocSelection);
                }
            }
        }
    }

    @Override
    public IModel<String> getTitle() {
        return titleModel;
    }

    @Override
    public IValueMap getProperties() {
        return dialogSize;
    }

    protected IPluginConfig getPluginConfig() {
        return extDocServiceContext.getPluginConfig();
    }

    protected IPluginContext getPluginContext() {
        return extDocServiceContext.getPluginContext();
    }

    protected boolean isSingleSelectionMode() {
        return StringUtils.equalsIgnoreCase(PluginConstants.SELECTION_MODE_SINGLE, getPluginConfig()
                .getString(PluginConstants.PARAM_SELECTION_MODE, PluginConstants.SELECTION_MODE_MULTIPLE));
    }

    abstract protected void doInitialSearchOnExternalDocuments();

    abstract protected void initDataListViewUI();

}
