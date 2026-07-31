/**
 * Copyright 2014-2026 Bloomreach B.V. (<a href="https://www.bloomreach.com">https://www.bloomreach.com</a>)
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Serializable;

import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.plugin.IPlugin;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SimpleExternalDocumentServiceContextTest {

    @Mock
    private IPlugin plugin;

    @Mock
    private IPluginConfig pluginConfig;

    @Mock
    private IPluginContext pluginContext;

    @Mock
    private JcrNodeModel contextModel;

    private SimpleExternalDocumentServiceContext context;

    @BeforeEach
    public void before() {
        context = new SimpleExternalDocumentServiceContext(plugin, pluginConfig, pluginContext, contextModel);
    }

    @Test
    public void getPlugin_returnsConstructorArgument() {
        assertSame(plugin, context.getPlugin());
    }

    @Test
    public void getPluginConfig_returnsConstructorArgument() {
        assertSame(pluginConfig, context.getPluginConfig());
    }

    @Test
    public void getPluginContext_returnsConstructorArgument() {
        assertSame(pluginContext, context.getPluginContext());
    }

    @Test
    public void getContextModel_returnsConstructorArgument() {
        assertSame(contextModel, context.getContextModel());
    }

    @Test
    public void setAttribute_thenGetAttribute_returnsStoredValue() {
        context.setAttribute("key", "value");

        assertEquals("value", context.getAttribute("key"));
    }

    @Test
    public void setAttribute_nullName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> context.setAttribute(null, "value"));
    }

    @Test
    public void getAttribute_unknownName_returnsNull() {
        assertNull(context.getAttribute("missing"));
    }

    @Test
    public void removeAttribute_presentName_removesIt() {
        context.setAttribute("key", "value");

        context.removeAttribute("key");

        assertNull(context.getAttribute("key"));
        assertFalse(context.getAttributeNames().contains("key"));
    }

    @Test
    public void getAttributeNames_reflectsStoredAttributes() {
        context.setAttribute("key1", "value1");
        context.setAttribute("key2", "value2");

        assertEquals(2, context.getAttributeNames().size());
        assertTrue(context.getAttributeNames().contains("key1"));
        assertTrue(context.getAttributeNames().contains("key2"));
    }

    /**
     * Documents existing (buggy) behavior: setAttribute(name, null) calls removeAttribute(name)
     * but then unconditionally falls through to attributes.put(name, null), re-adding the key
     * with a null value instead of leaving it removed. See SimpleExternalDocumentServiceContext
     * lines 70-80 — flagged for triage, not fixed here per task scope.
     */
    @Test
    public void setAttribute_withNullValue_reAddsKeyWithNullValueInsteadOfRemovingIt() {
        context.setAttribute("key", "value");

        context.setAttribute("key", (Serializable) null);

        assertTrue(context.getAttributeNames().contains("key"));
        assertNull(context.getAttribute("key"));
    }
}
