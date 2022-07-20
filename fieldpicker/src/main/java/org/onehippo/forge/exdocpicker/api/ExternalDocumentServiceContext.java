/**
 * Copyright 2014-2022 Bloomreach B.V. (<a href="https://www.bloomreach.com">https://www.bloomreach.com</a>)
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
package org.onehippo.forge.exdocpicker.api;

import java.io.Serializable;
import java.util.Set;

import org.apache.wicket.util.io.IClusterable;
import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.plugin.IPlugin;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;

/**
 * The service invocation context which is supposed to be used by the service implementations.
 */
public interface ExternalDocumentServiceContext extends IClusterable {

    /**
     * Returns the plugin instance.
     * @return IPlugin instance
     */
    IPlugin getPlugin();

    /**
     * Returns the plugin config instance.
     * @return IPluginConfig instance
     */
    IPluginConfig getPluginConfig();

    /**
     * Returns the plugin context instance.
     * @return IPluginContext instance
     */
    IPluginContext getPluginContext();

    /**
     * Returns the context document object which is being used by the user when the invocation is made.
     * @return context model
     */
    JcrNodeModel getContextModel();

    /**
     * Set an attribute.
     * 
     * @param name attribute name
     * @param value serializable attribute value
     */
    void setAttribute(String name, Serializable value);

    /**
     * Retrieve the attribute value by the attribute name.
     * @param name attribute name
     * @return attribute value
     */
    Serializable getAttribute(String name);

    /**
     * Removes the attribute by the attribute name.
     * @param name attribute name
     */
    void removeAttribute(String name);

    /**
     * Enumerates the attribute names
     * @return attribute names set
     */
    Set<String> getAttributeNames();

}
