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
 * 
 */
public class PluginConstants {

    public static final String PARAM_EXTERNAL_DOCUMENT_SERVICE_FACADE = "external.document.service.facade";

    public static final String PARAM_SELECTION_MODE = "selection.mode";

    public static final String PARAM_DIALOG_SIZE = "dialog.size";

    public static final String PARAM_INITIAL_SEARCH_ENABLED = "initial.search.enabled";

    public static final String PARAM_INITIAL_SEARCH_QUERY = "initial.search.query";

    public static final String PARAM_PAGE_SIZE = "page.size";

    public static final String SELECTION_MODE_MULTIPLE = "multiple";

    public static final String SELECTION_MODE_SINGLE = "single";

    public static final String DEFAULT_FIELD_CAPTION = "Related external documents";

    public static final String DEFAULT_SELECTION_MODE = SELECTION_MODE_MULTIPLE;

    public static final boolean DEFAULT_INITIAL_SEARCH_ENABLED = false;

    public static final String DEFAULT_INITIAL_SEARCH_QUERY = "";

    public static final String DEFAULT_DIALOG_SIZE = "width=835,height=650";

    public static final int DEFAULT_PAGE_SIZE = 5;

    private PluginConstants() {
    }

}
