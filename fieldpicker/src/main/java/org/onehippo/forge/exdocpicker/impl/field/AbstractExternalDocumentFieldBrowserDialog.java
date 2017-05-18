/**
 * Copyright 2014-2017 Hippo B.V. (http://www.onehippo.com)
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

/**
 * Abstract external document(s) picker dialog class.
 */
public abstract class AbstractExternalDocumentFieldBrowserDialog
        extends AbstractDialog<ExternalDocumentCollection<Serializable>> {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(AbstractExternalDocumentFieldBrowserDialog.class);

    /**
     * Dialog title model.
     */
    private final IModel<String> titleModel;

    /**
     * {@link ExternalDocumentServiceFacade} instance.
     */
    private final ExternalDocumentServiceFacade<Serializable> exdocService;

    /**
     * Currently picked external documents in the list view UI by end user.
     */
    private final Set<Serializable> pickedExtDocsInUI = new LinkedHashSet<Serializable>();

    /**
     * Currently selected external documents in the document variant node data.
     */
    private ExternalDocumentCollection<Serializable> selectedExtDocsInVariantNode;

    /**
     * Searched external documents to show in the list view UI.
     */
    private ExternalDocumentCollection<Serializable> searchedExtDocs = new SimpleExternalDocumentCollection<Serializable>();

    /**
     * Page size.
     */
    private int pageSize;

    /**
     * Dialog size.
     */
    private final IValueMap dialogSize;

    /**
     * {@link ExternalDocumentServiceContext} instance.
     */
    private final ExternalDocumentServiceContext extDocServiceContext;

    /**
     * Constructs external document(s) picker dialog.
     * @param titleModel title model
     * @param extDocServiceContext {@link ExternalDocumentServiceContext} instance
     * @param exdocService {@link ExternalDocumentServiceFacade} instance
     * @param model the model containing the currently selected external documents in the document node data
     */
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

        selectedExtDocsInVariantNode = getModelObject();

        pickedExtDocsInUI.clear();

        for (Iterator<? extends Serializable> it = selectedExtDocsInVariantNode.iterator(); it.hasNext();) {
            pickedExtDocsInUI.add(it.next());
        }

        if (getModel().getObject() == null) {
            setOkVisible(false);
            setOkEnabled(false);
        }

        initializeSearchedExternalDocuments();
        initializeDataListView();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onOk() {
        if (pickedExtDocsInUI != null) {
            if (isSingleSelectionMode()) {
                selectedExtDocsInVariantNode.clear();
                Serializable curDoc = null;
                // when single selection mode, let's add the last added item only.
                for (Iterator<Serializable> it = pickedExtDocsInUI.iterator(); it.hasNext();) {
                    curDoc = it.next();
                }
                if (curDoc != null) {
                    selectedExtDocsInVariantNode.add(curDoc);
                }
                exdocService.setFieldExternalDocuments(extDocServiceContext, selectedExtDocsInVariantNode);
            } else {
                boolean added = false;

                for (Serializable doc : pickedExtDocsInUI) {
                    if (!selectedExtDocsInVariantNode.contains(doc)) {
                        selectedExtDocsInVariantNode.add(doc);
                        added = true;
                    }
                }

                if (added) {
                    exdocService.setFieldExternalDocuments(extDocServiceContext, selectedExtDocsInVariantNode);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IModel<String> getTitle() {
        return titleModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IValueMap getProperties() {
        return dialogSize;
    }

    /**
     * Returns the plugin config.
     * @return the plugin config
     */
    protected IPluginConfig getPluginConfig() {
        return extDocServiceContext.getPluginConfig();
    }

    /**
     * Returns the plugin context.
     * @return the plugin context
     */
    protected IPluginContext getPluginContext() {
        return extDocServiceContext.getPluginContext();
    }

    /**
     * Returns {@link ExternalDocumentServiceFacade} instance.
     * @return {@link ExternalDocumentServiceFacade} instance
     */
    protected ExternalDocumentServiceFacade<Serializable> getExternalDocumentServiceFacade() {
        return exdocService;
    }

    /**
     * Returns {@link ExternalDocumentServiceContext} instance.
     * @return {@link ExternalDocumentServiceContext} instance
     */
    protected ExternalDocumentServiceContext getExternalDocumentServiceContext() {
        return extDocServiceContext;
    }

    /**
     * Returns currently picked external documents in the list view UI.
     * @return currently picked external documents in the list view UI
     */
    protected Set<Serializable> getPickedExternalDocuments() {
        return pickedExtDocsInUI;
    }

    /**
     * Returns currently selected external documents in the document variant data node.
     * @return currently selected external documents in the document variant data node
     */
    protected ExternalDocumentCollection<Serializable> getSelectedExternalDocuments() {
        return selectedExtDocsInVariantNode;
    }

    /**
     * Returns searched external documents to show in the list view UI.
     * @return searched external documents to show in the list view UI
     */
    protected ExternalDocumentCollection<Serializable> getSearchedExternalDocuments() {
        return searchedExtDocs;
    }

    /**
     * Returns true if the selection mode in UI is 'single'.
     * @return true if the selection mode in UI is 'single'
     */
    protected boolean isSingleSelectionMode() {
        return StringUtils.equalsIgnoreCase(PluginConstants.SELECTION_MODE_SINGLE, getPluginConfig()
                .getString(PluginConstants.PARAM_SELECTION_MODE, PluginConstants.SELECTION_MODE_MULTIPLE));
    }

    /**
     * Returns the page size.
     * @return the page size
     */
    protected int getPageSize() {
        return pageSize;
    }

    /**
     * Returns the dialog size.
     * @return the dialog size
     */
    protected IValueMap getDialogSize() {
        return dialogSize;
    }

    /**
     * Initializes the {@link #searchedExtDocs} on construction.
     * Invoked during the construction of this dialog instance.
     */
    abstract protected void initializeSearchedExternalDocuments();

    /**
     * Initializes the data list view UI.
     * Invoked during the construction of this dialog instance.
     */
    abstract protected void initializeDataListView();

}
