/**
 * Copyright 2014-2022 Bloomreach B.V. (<a href="http://www.bloomreach.com">http://www.bloomreach.com</a>)
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
package org.onehippo.forge.exdocpicker.impl.field.tree;

import java.io.Serializable;

import org.apache.wicket.model.Model;
import org.hippoecm.frontend.dialog.AbstractDialog;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentCollection;
import org.onehippo.forge.exdocpicker.impl.field.ExternalDocumentFieldSelectorPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * External document(s) selector field plugin for tree item data from external data backend.
 * This class simply exstends {@link ExternalDocumentFieldSelectorPlugin} to show a different implementation of
 * the dialog when user clicks on the browse button.
 */
public class ExternalTreeItemFieldSelectorPlugin extends ExternalDocumentFieldSelectorPlugin {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(ExternalTreeItemFieldSelectorPlugin.class);

    /**
     * Constructs tree-viewable external document(s) selector field plugin.
     * @param context plugin context
     * @param config plugin config
     */
    public ExternalTreeItemFieldSelectorPlugin(final IPluginContext context, IPluginConfig config) {
        super(context, config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractDialog<ExternalDocumentCollection<Serializable>> createDialogInstance() {
        return new ExternalTreeItemFieldBrowserDialog(
                getCaptionModel(),
                getExternalDocumentServiceContext(),
                getExternalDocumentServiceFacade(),
                new Model<>(getCurrentExternalDocumentCollection()));
    }

}
