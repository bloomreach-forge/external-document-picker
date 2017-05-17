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

/**
 * Constants used in the External Document Picker related plugins.
 */
public class PluginConstants {

    /**
     * Plugin parameter for the {@link ExternalDocumentServiceFacade} implementation class name.
     * Plugin UI implementation reads this parameter to instantiate the external document service facade.
     */
    public static final String PARAM_EXTERNAL_DOCUMENT_SERVICE_FACADE = "external.document.service.facade";

    /**
     * Plugin parameter for whether the picker is single item selection only or multiple items selectable.
     */
    public static final String PARAM_SELECTION_MODE = "selection.mode";

    /**
     * Plugin parameter for the picker dialog size.
     */
    public static final String PARAM_DIALOG_SIZE = "dialog.size";

    /**
     * Plugin parameter for whether or not the initial search should be done when the picker dialog opens up.
     */
    public static final String PARAM_INITIAL_SEARCH_ENABLED = "initial.search.enabled";

    /**
     * Plugin parameter for the initial search query string to be used if {@link PluginConstants#PARAM_INITIAL_SEARCH_ENABLED} is turned on.
     */
    public static final String PARAM_INITIAL_SEARCH_QUERY = "initial.search.query";

    /**
     * Plugin parameter for page size (item count in a page) displayed in the picker dialog.
     */
    public static final String PARAM_PAGE_SIZE = "page.size";

    /**
     * Plugin parameter for whether or not the selected external documents should be visible or not.
     * <p>
     * Note: In some use cases, it is expected to not display the selected document data in the plugin view itself.
     *       For example, the facade implementation creates other compound or link fields in the same document.
     * </p>
     */
    public static final String PARAM_EXTERNAL_DOCUMENTS_CONTAINER_VISIBLE = "external.documents.container.visible";

    /**
     * Plugin parameter value for multiple items selection mode in the picker dialog.
     */
    public static final String SELECTION_MODE_MULTIPLE = "multiple";

    /**
     * Plugin parameter value for single item selection only mode in the picker dialog.
     */
    public static final String SELECTION_MODE_SINGLE = "single";

    /**
     * Default field caption string which can be used instead when 'caption' plugin parameter is not defined.
     */
    public static final String DEFAULT_FIELD_CAPTION = "Related external documents";

    /**
     * Default item selection mode parameter value. The default value is multiple item selectino mode.
     */
    public static final String DEFAULT_SELECTION_MODE = SELECTION_MODE_MULTIPLE;

    /**
     * Default initial search enabled parameter value. The default value is false.
     */
    public static final boolean DEFAULT_INITIAL_SEARCH_ENABLED = false;

    /**
     * Default initial search query parameter value.
     */
    public static final String DEFAULT_INITIAL_SEARCH_QUERY = "";

    /**
     * Default dialog size parameter value.
     */
    public static final String DEFAULT_DIALOG_SIZE = "width=835,height=650";

    /**
     * Default page size (item count in a page) parameter value.
     */
    public static final int DEFAULT_PAGE_SIZE = 5;

    /**
     * Plugin parameter for theme name to be used when rendering a tree list view. e.g, 'human' or 'windows'.
     */
    public static final String PARAM_EXTERNAL_TREE_VIEW_THEME = "external.tree.view.theme";

    /**
     * Default theme name to be used when rendering a tree list view.
     */
    public static final String DEFAULT_EXTERNAL_TREE_VIEW_THEME = "human";

    private PluginConstants() {
    }

}
