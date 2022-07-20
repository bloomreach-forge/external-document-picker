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
package org.onehippo.forge.exdocpicker.impl;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.plugin.IPlugin;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceContext;

/**
 * Simple {@link ExternalDocumentServiceContext} impl.
 */
public class SimpleExternalDocumentServiceContext implements ExternalDocumentServiceContext {

    private static final long serialVersionUID = 1L;

    private final IPlugin plugin;
    private final IPluginConfig config;
    private final IPluginContext context;
    private final JcrNodeModel contextModel;
    protected Map<String, Serializable> attributes = new LinkedHashMap<>();

    public SimpleExternalDocumentServiceContext(final IPlugin plugin, final IPluginConfig config, final IPluginContext context, final JcrNodeModel contextModel) {
        this.plugin = plugin;
        this.config = config;
        this.context = context;
        this.contextModel = contextModel;
    }

    @Override
    public IPlugin getPlugin() {
        return plugin;
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

    @Override
    public void setAttribute(String name, Serializable value) {
        if (name == null) {
            throw new IllegalArgumentException("attribute name cannot be null.");
        }

        if (value == null) {
            removeAttribute(name);
        }

        attributes.put(name, value);
    }

    @Override
    public Serializable getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public Set<String> getAttributeNames() {
        return attributes.keySet();
    }

}
