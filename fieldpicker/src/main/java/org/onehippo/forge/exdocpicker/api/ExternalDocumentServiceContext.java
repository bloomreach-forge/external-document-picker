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
package org.onehippo.forge.exdocpicker.api;

import org.apache.wicket.util.io.IClusterable;
import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;

/**
 * The service invocation context which is supposed to be used by the service implementations.
 */
public interface ExternalDocumentServiceContext extends IClusterable {

    /**
     * Returns the plugin config instance.
     * @return
     */
    public IPluginConfig getPluginConfig();

    /**
     * Returns the plugin context instance.
     * @return
     */
    public IPluginContext getPluginContext();

    /**
     * Returns the context document object which is being used by the user when the invocation is made.
     * @return
     */
    public JcrNodeModel getContextModel();

}
