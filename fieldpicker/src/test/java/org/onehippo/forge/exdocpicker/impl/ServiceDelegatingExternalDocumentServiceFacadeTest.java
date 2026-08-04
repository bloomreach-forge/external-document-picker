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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.Locale;

import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentCollection;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceContext;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceFacade;

@ExtendWith(MockitoExtension.class)
public class ServiceDelegatingExternalDocumentServiceFacadeTest {

    private static final String SERVICE_ID = "delegateServiceId";

    @Mock
    private ExternalDocumentServiceContext context;

    @Mock
    private IPluginConfig pluginConfig;

    @Mock
    private IPluginContext pluginContext;

    @Mock
    private ExternalDocumentServiceFacade<Serializable> delegate;

    private ServiceDelegatingExternalDocumentServiceFacade facade;

    @BeforeEach
    public void before() {
        facade = new ServiceDelegatingExternalDocumentServiceFacade();
    }

    private void stubDelegateLookup() {
        when(context.getPluginConfig()).thenReturn(pluginConfig);
        when(pluginConfig.getString(ServiceDelegatingExternalDocumentServiceFacade.DELEGATE_EXTERNAL_DOCUMENT_SERVICE_ID))
                .thenReturn(SERVICE_ID);
        when(context.getPluginContext()).thenReturn(pluginContext);
        when(pluginContext.getService(eq(SERVICE_ID), eq(ExternalDocumentServiceFacade.class))).thenReturn(delegate);
    }

    @Test
    public void searchExternalDocuments_delegatesToLookedUpService() {
        stubDelegateLookup();
        @SuppressWarnings("unchecked")
        ExternalDocumentCollection<Serializable> expected = mock(ExternalDocumentCollection.class);
        when(delegate.searchExternalDocuments(context, "query")).thenReturn(expected);

        ExternalDocumentCollection<Serializable> result = facade.searchExternalDocuments(context, "query");

        assertSame(expected, result);
        verify(delegate).searchExternalDocuments(context, "query");
    }

    @Test
    public void getFieldExternalDocuments_delegatesToLookedUpService() {
        stubDelegateLookup();
        @SuppressWarnings("unchecked")
        ExternalDocumentCollection<Serializable> expected = mock(ExternalDocumentCollection.class);
        when(delegate.getFieldExternalDocuments(context)).thenReturn(expected);

        ExternalDocumentCollection<Serializable> result = facade.getFieldExternalDocuments(context);

        assertSame(expected, result);
    }

    @Test
    public void setFieldExternalDocuments_delegatesToLookedUpService() {
        stubDelegateLookup();
        @SuppressWarnings("unchecked")
        ExternalDocumentCollection<Serializable> exdocs = mock(ExternalDocumentCollection.class);

        facade.setFieldExternalDocuments(context, exdocs);

        verify(delegate).setFieldExternalDocuments(context, exdocs);
    }

    @Test
    public void getDocumentTitle_delegatesToLookedUpService() {
        stubDelegateLookup();
        Serializable doc = "doc";
        when(delegate.getDocumentTitle(context, doc, Locale.ENGLISH)).thenReturn("Title");

        String title = facade.getDocumentTitle(context, doc, Locale.ENGLISH);

        assertEquals("Title", title);
    }

    @Test
    public void getDocumentDescription_delegatesToLookedUpService() {
        stubDelegateLookup();
        Serializable doc = "doc";
        when(delegate.getDocumentDescription(context, doc, Locale.ENGLISH)).thenReturn("Description");

        String description = facade.getDocumentDescription(context, doc, Locale.ENGLISH);

        assertEquals("Description", description);
    }

    @Test
    public void getDocumentIconLink_delegatesToLookedUpService() {
        stubDelegateLookup();
        Serializable doc = "doc";
        when(delegate.getDocumentIconLink(context, doc, Locale.ENGLISH)).thenReturn("/icon.png");

        String iconLink = facade.getDocumentIconLink(context, doc, Locale.ENGLISH);

        assertEquals("/icon.png", iconLink);
    }

    @Test
    public void serviceIdLookup_isCachedAfterFirstResolution_configReadOnlyOnce() {
        stubDelegateLookup();
        when(delegate.getFieldExternalDocuments(any())).thenReturn(null);

        facade.getFieldExternalDocuments(context);
        facade.searchExternalDocuments(context, "query");

        verify(pluginConfig, times(1))
                .getString(ServiceDelegatingExternalDocumentServiceFacade.DELEGATE_EXTERNAL_DOCUMENT_SERVICE_ID);
    }

    @Test
    public void serviceIdLookup_nullConfigValue_throwsIllegalStateExceptionAndNeverLooksUpService() {
        when(context.getPluginConfig()).thenReturn(pluginConfig);
        when(pluginConfig.getString(ServiceDelegatingExternalDocumentServiceFacade.DELEGATE_EXTERNAL_DOCUMENT_SERVICE_ID))
                .thenReturn(null);

        assertThrows(IllegalStateException.class, () -> facade.getFieldExternalDocuments(context));
        verify(pluginContext, never()).getService(any(), any());
    }

    @Test
    public void serviceIdLookup_blankConfigValue_throwsIllegalStateException() {
        when(context.getPluginConfig()).thenReturn(pluginConfig);
        when(pluginConfig.getString(ServiceDelegatingExternalDocumentServiceFacade.DELEGATE_EXTERNAL_DOCUMENT_SERVICE_ID))
                .thenReturn("   ");

        assertThrows(IllegalStateException.class, () -> facade.getFieldExternalDocuments(context));
    }
}
