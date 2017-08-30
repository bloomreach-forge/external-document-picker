/**
 * Copyright 2017 Hippo B.V. (http://www.onehippo.com)
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
package org.onehippo.forge.exdocpicker.impl.folder;

import java.io.Serializable;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.hippoecm.addon.workflow.StdWorkflow;
import org.hippoecm.addon.workflow.WorkflowDescriptorModel;
import org.hippoecm.frontend.dialog.AbstractDialog;
import org.hippoecm.frontend.dialog.IDialogFactory;
import org.hippoecm.frontend.dialog.IDialogService;
import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.service.render.RenderPlugin;
import org.hippoecm.repository.api.WorkflowDescriptor;
import org.hippoecm.repository.standardworkflow.FolderWorkflow;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentCollection;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceContext;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceFacade;
import org.onehippo.forge.exdocpicker.api.PluginConstants;
import org.onehippo.forge.exdocpicker.impl.SimpleExternalDocumentServiceContext;
import org.onehippo.forge.exdocpicker.impl.field.TextSearchExternalDocumentFieldBrowserDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalDocumentFolderActionWorkflowMenuItemPlugin extends RenderPlugin<WorkflowDescriptor> {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(ExternalDocumentFolderActionWorkflowMenuItemPlugin.class);

    private WorkflowDescriptorModel folderWorkflowModel;
    private JcrNodeModel folderNodeModel;

    private ExternalDocumentServiceFacade<Serializable> exdocService;
    private ExternalDocumentCollection<Serializable> curDocCollection;
    private ExternalDocumentServiceContext extDocServiceContext;

    public ExternalDocumentFolderActionWorkflowMenuItemPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);

        folderWorkflowModel = (WorkflowDescriptorModel) getModel();

        try {
            folderNodeModel = new JcrNodeModel(folderWorkflowModel.getNode());
        } catch (RepositoryException e) {
            throw new IllegalStateException("Folder node is not resolvable.", e);
        }

        setExternalDocumentServiceFacade((ExternalDocumentServiceFacade<Serializable>) createExternalDocumentService());
        setExternalDocumentServiceContext(
                new SimpleExternalDocumentServiceContext(this, config, context, folderNodeModel));

        add(new StdWorkflow<FolderWorkflow>("menuItem", getMenuItemLabelModel(), folderWorkflowModel) {

            private static final long serialVersionUID = 1L;

            @Override
            protected ResourceReference getIcon() {
                return getMenuItemIconResourceReference();
            }

            @Override
            protected String execute(FolderWorkflow workflow) throws Exception {
                final IDialogService dialogService = getDialogService();

                if (!dialogService.isShowingDialog()) {
                    final IDialogFactory dialogFactory = createDialogFactory();
                    dialogService.show(dialogFactory.createDialog());
                }

                return null;
            }
        });
    }

    protected IModel<String> getMenuItemLabelModel() {
        String menuItemLabel = getPluginConfig().getString("exdocfield.menu.label", PluginConstants.DEFAULT_FIELD_CAPTION);
        String menuItemLabelKey = menuItemLabel;
        return new StringResourceModel(menuItemLabelKey, this, null, menuItemLabel);
    }

    protected ResourceReference getMenuItemIconResourceReference() {
        // TODO
        return new PackageResourceReference(getClass(), "copy-folder-16.png");
    }

    protected IModel<String> getDialogTitleModel() {
        String dialogTitle = getPluginConfig().getString("exdocfield.dialog.title");

        if (StringUtils.isBlank(dialogTitle)) {
            return getMenuItemLabelModel();
        } else {
            String dialogTitleKey = dialogTitle;
            return new StringResourceModel(dialogTitleKey, this, null, dialogTitle);
        }
    }

    /**
     * Creates a dialog factory to create a picker dialog.
     * @return a dialog factory to create a picker dialog
     */
    protected IDialogFactory createDialogFactory() {
        return new IDialogFactory() {
            private static final long serialVersionUID = 1L;

            public AbstractDialog<ExternalDocumentCollection<Serializable>> createDialog() {
                return createDialogInstance();
            }
        };
    }

    /**
     * Creates a picker dialog with which user can select external documents.
     * @return a picker dialog with which user can select external documents
     */
    protected AbstractDialog<ExternalDocumentCollection<Serializable>> createDialogInstance() {
        return new TextSearchExternalDocumentFieldBrowserDialog(getDialogTitleModel(), getExternalDocumentServiceContext(),
                getExternalDocumentServiceFacade(),
                new Model<ExternalDocumentCollection<Serializable>>(getCurrentExternalDocumentCollection()));
    }

    /**
     * Creates a new {@link ExternalDocumentServiceFacade} instance.
     * By default, this method reads the plugin configuration by the name, {@link PluginConstants#PARAM_EXTERNAL_DOCUMENT_SERVICE_FACADE},
     * and instantiated an object by the FQCN configuration parameter.
     * @return a new {@link ExternalDocumentServiceFacade} instance
     */
    protected ExternalDocumentServiceFacade<? extends Serializable> createExternalDocumentService() {
        ExternalDocumentServiceFacade<? extends Serializable> service = null;
        String serviceFacadeClassName = null;

        try {
            serviceFacadeClassName = getPluginConfig()
                    .getString(PluginConstants.PARAM_EXTERNAL_DOCUMENT_SERVICE_FACADE);
            Class<? extends ExternalDocumentServiceFacade> serviceClass = (Class<? extends ExternalDocumentServiceFacade>) Class
                    .forName(serviceFacadeClassName);
            service = serviceClass.newInstance();
        } catch (Exception e) {
            log.error("Failed to create external document service facade from class name, '{}'.",
                    serviceFacadeClassName, e);
        }

        return service;
    }

    /**
     * Returns the {@link ExternalDocumentServiceFacade} instance used by this plugin instance.
     * @return the {@link ExternalDocumentServiceFacade} instance used by this plugin instance
     */
    protected ExternalDocumentServiceFacade<Serializable> getExternalDocumentServiceFacade() {
        return exdocService;
    }

    /**
     * Sets the {@link ExternalDocumentServiceFacade} instance used by this plugin instance.
     * @param exdocService the {@link ExternalDocumentServiceFacade} instance used by this plugin instance
     */
    protected void setExternalDocumentServiceFacade(ExternalDocumentServiceFacade<Serializable> exdocService) {
        this.exdocService = exdocService;
    }

    /**
     * Returns the collection of the currently-selected external documents in the document.
     * @return the collection of the currently-selected external documents in the document
     */
    protected ExternalDocumentCollection<Serializable> getCurrentExternalDocumentCollection() {
        if (curDocCollection == null) {
            curDocCollection = getExternalDocumentServiceFacade()
                    .getFieldExternalDocuments(getExternalDocumentServiceContext());
        }

        return curDocCollection;
    }

    /**
     * Sets the collection of the currently-selected external documents in the document.
     * @param curDocCollection the collection of the currently-selected external documents in the document
     */
    protected void setCurrentExternalDocumentCollection(ExternalDocumentCollection<Serializable> curDocCollection) {
        this.curDocCollection = curDocCollection;
    }

    /**
     * Returns the {@link ExternalDocumentServiceContext} instance.
     * @return the {@link ExternalDocumentServiceContext} instance
     */
    protected ExternalDocumentServiceContext getExternalDocumentServiceContext() {
        return extDocServiceContext;
    }

    /**
     * Sets the {@link ExternalDocumentServiceContext} instance.
     * @param extDocServiceContext the {@link ExternalDocumentServiceContext} instance
     */
    protected void setExternalDocumentServiceContext(ExternalDocumentServiceContext extDocServiceContext) {
        this.extDocServiceContext = extDocServiceContext;
    }
}
