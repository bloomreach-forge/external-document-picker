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
package org.onehippo.forge.exdocpicker.impl;

import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceContext;

/**
 * 
 */
public class SimpleExternalDocumentServiceContext implements ExternalDocumentServiceContext {

    private static final long serialVersionUID = 1L;

    private final IPluginConfig config;
    private final IPluginContext context;
    private final JcrNodeModel contextModel;

    public SimpleExternalDocumentServiceContext(final IPluginConfig config, final IPluginContext context, final JcrNodeModel contextModel) {
        this.config = config;
        this.context = context;
        this.contextModel = contextModel;
    }

    @Override
    public IPluginConfig getPluginConfig() {
        return config;
    }

    @Override
    public IPluginContext getPluginContext() {
        return context;
    }

    @Override
    public JcrNodeModel getContextModel() {
        return contextModel;
    }

}
