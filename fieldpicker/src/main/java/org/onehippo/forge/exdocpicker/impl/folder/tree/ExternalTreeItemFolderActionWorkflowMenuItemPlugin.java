/**
 * Copyright 2014-2024 Bloomreach B.V. (<a href="https://www.bloomreach.com">https://www.bloomreach.com</a>)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         <a href="http://www.apache.org/licenses/LICENSE-2.0">http://www.apache.org/licenses/LICENSE-2.0</a>
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onehippo.forge.exdocpicker.impl.folder.tree;

import java.io.Serializable;

import org.apache.wicket.model.Model;
import org.hippoecm.frontend.dialog.AbstractDialog;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentCollection;
import org.onehippo.forge.exdocpicker.impl.field.tree.ExternalTreeItemFieldBrowserDialog;
import org.onehippo.forge.exdocpicker.impl.folder.ExternalDocumentFolderActionWorkflowMenuItemPlugin;

public class ExternalTreeItemFolderActionWorkflowMenuItemPlugin extends ExternalDocumentFolderActionWorkflowMenuItemPlugin {

    private static final long serialVersionUID = 1L;

    public ExternalTreeItemFolderActionWorkflowMenuItemPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);
    }

    @Override
    protected AbstractDialog<ExternalDocumentCollection<Serializable>> createDialogInstance() {
        return new ExternalTreeItemFieldBrowserDialog(
                getDialogTitleModel(),
                getExternalDocumentServiceContext(),
                getExternalDocumentServiceFacade(),
                new Model<>(getCurrentExternalDocumentCollection()));
    }

}
