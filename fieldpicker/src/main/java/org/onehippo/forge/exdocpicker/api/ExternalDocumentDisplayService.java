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
package org.onehippo.forge.exdocpicker.api;

import java.io.Serializable;
import java.util.Locale;

/**
 * The implementation of this interface is responsible for reading title, description and icon link from the domain specific external document object.
 *
 * @param <T> Domain specific external document POJO type which must be serializable.
 */
public interface ExternalDocumentDisplayService<T extends Serializable> {

    /**
     * Returns true if the domain specific external document object is selectable for a field by end user.
     * @param context ExternalDocumentServiceContext instance
     * @param doc document
     * @return true if the domain specific external document object is selectable for a field by end user
     */
    default boolean isDocumentSelectable(ExternalDocumentServiceContext context, T doc) {
        return true;
    }

    /**
     * Reads the title from the domain specific external document object by the <code>preferredLocale</code>.
     *
     * @param context ExternalDocumentServiceContext instance
     * @param doc document
     * @param preferredLocale preferrered locale
     * @return document title
     */
    String getDocumentTitle(ExternalDocumentServiceContext context, T doc, final Locale preferredLocale);

    /**
     * Reads the description from the domain specific external document object by the <code>preferredLocale</code>.
     *
     * @param context ExternalDocumentServiceContext instance
     * @param doc document
     * @param preferredLocale preferred locale
     * @return document description
     */
    String getDocumentDescription(ExternalDocumentServiceContext context, T doc, final Locale preferredLocale);

    /**
     * Reads the icon link URL from the domain specific external document object by the <code>preferredLocale</code>.
     *
     * @param context ExternalDocumentServiceContext instance
     * @param doc document
     * @param preferredLocale preferred locale
     * @return document icon link URL
     */
    String getDocumentIconLink(ExternalDocumentServiceContext context, T doc, final Locale preferredLocale);

}
