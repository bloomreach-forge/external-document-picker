package org.onehippo.forge.exdocpicker.impl.field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.WicketTester;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugin.config.impl.JavaPluginConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentCollection;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceContext;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceFacade;
import org.onehippo.forge.exdocpicker.api.PluginConstants;
import org.onehippo.forge.exdocpicker.impl.SimpleExternalDocumentCollection;

public class AbstractExternalDocumentFieldBrowserDialogTest {

    private WicketTester tester;

    private ExternalDocumentServiceContext context;

    private ExternalDocumentServiceFacade<Serializable> facade;

    @AfterEach
    public void after() {
        if (tester != null) {
            tester.destroy();
        }
    }

    private AbstractExternalDocumentFieldBrowserDialog createDialog(IPluginConfig pluginConfig,
            List<Serializable> initialSelectedDocs) {
        tester = new WicketTester();

        context = mock(ExternalDocumentServiceContext.class);
        when(context.getPluginConfig()).thenReturn(pluginConfig);

        @SuppressWarnings("unchecked")
        final ExternalDocumentServiceFacade<Serializable> mockedFacade = mock(ExternalDocumentServiceFacade.class);
        facade = mockedFacade;

        final ExternalDocumentCollection<Serializable> selected = new SimpleExternalDocumentCollection<>(
                initialSelectedDocs);
        final IModel<ExternalDocumentCollection<Serializable>> model = Model.of(selected);

        return new AbstractExternalDocumentFieldBrowserDialog(Model.of("Title"), context, facade, model) {
            @Override
            protected void initializeSearchedExternalDocuments() {
            }

            @Override
            protected void initializeDataListView() {
            }
        };
    }

    @Test
    public void isSingleSelectionMode_singleConfigValue_returnsTrue() {
        final IPluginConfig pluginConfig = new JavaPluginConfig();
        pluginConfig.put(PluginConstants.PARAM_SELECTION_MODE, "single");

        final AbstractExternalDocumentFieldBrowserDialog dialog = createDialog(pluginConfig, List.of());

        assertTrue(dialog.isSingleSelectionMode());
    }

    @Test
    public void isSingleSelectionMode_singleConfigValueMixedCase_returnsTrue() {
        final IPluginConfig pluginConfig = new JavaPluginConfig();
        pluginConfig.put(PluginConstants.PARAM_SELECTION_MODE, "SiNgLe");

        final AbstractExternalDocumentFieldBrowserDialog dialog = createDialog(pluginConfig, List.of());

        assertTrue(dialog.isSingleSelectionMode());
    }

    @Test
    public void isSingleSelectionMode_multipleConfigValue_returnsFalse() {
        final IPluginConfig pluginConfig = new JavaPluginConfig();
        pluginConfig.put(PluginConstants.PARAM_SELECTION_MODE, PluginConstants.SELECTION_MODE_MULTIPLE);

        final AbstractExternalDocumentFieldBrowserDialog dialog = createDialog(pluginConfig, List.of());

        assertFalse(dialog.isSingleSelectionMode());
    }

    @Test
    public void isSingleSelectionMode_unsetConfig_defaultsToMultiple() {
        final IPluginConfig pluginConfig = new JavaPluginConfig();

        final AbstractExternalDocumentFieldBrowserDialog dialog = createDialog(pluginConfig, List.of());

        assertFalse(dialog.isSingleSelectionMode());
    }

    @Test
    public void onOk_singleSelectionMode_keepsOnlyLastPickedItem() {
        final IPluginConfig pluginConfig = new JavaPluginConfig();
        pluginConfig.put(PluginConstants.PARAM_SELECTION_MODE, PluginConstants.SELECTION_MODE_SINGLE);

        final AbstractExternalDocumentFieldBrowserDialog dialog = createDialog(pluginConfig, List.of("doc1"));

        dialog.getPickedExternalDocuments().clear();
        dialog.getPickedExternalDocuments().add("doc1");
        dialog.getPickedExternalDocuments().add("doc2");
        dialog.getPickedExternalDocuments().add("doc3");

        dialog.onOk();

        final ExternalDocumentCollection<Serializable> selected = dialog.getSelectedExternalDocuments();
        assertEquals(1, selected.getSize());
        assertTrue(selected.contains("doc3"));
        verify(facade).setFieldExternalDocuments(context, selected);
    }

    @Test
    public void onOk_singleSelectionMode_nothingPicked_clearsSelectionAndStillNotifiesFacade() {
        final IPluginConfig pluginConfig = new JavaPluginConfig();
        pluginConfig.put(PluginConstants.PARAM_SELECTION_MODE, PluginConstants.SELECTION_MODE_SINGLE);

        final AbstractExternalDocumentFieldBrowserDialog dialog = createDialog(pluginConfig, List.of("doc1"));

        dialog.getPickedExternalDocuments().clear();

        dialog.onOk();

        final ExternalDocumentCollection<Serializable> selected = dialog.getSelectedExternalDocuments();
        assertEquals(0, selected.getSize());
        verify(facade).setFieldExternalDocuments(context, selected);
    }

    @Test
    public void onOk_multiSelectionMode_addsOnlyNewPickedItems() {
        final IPluginConfig pluginConfig = new JavaPluginConfig();
        pluginConfig.put(PluginConstants.PARAM_SELECTION_MODE, PluginConstants.SELECTION_MODE_MULTIPLE);

        final AbstractExternalDocumentFieldBrowserDialog dialog = createDialog(pluginConfig, List.of("doc1"));

        dialog.getPickedExternalDocuments().add("doc2");

        dialog.onOk();

        final ExternalDocumentCollection<Serializable> selected = dialog.getSelectedExternalDocuments();
        assertEquals(2, selected.getSize());
        assertTrue(selected.contains("doc1"));
        assertTrue(selected.contains("doc2"));
        verify(facade).setFieldExternalDocuments(context, selected);
    }

    @Test
    public void onOk_multiSelectionMode_noNewItemsPicked_doesNotNotifyFacade() {
        final IPluginConfig pluginConfig = new JavaPluginConfig();
        pluginConfig.put(PluginConstants.PARAM_SELECTION_MODE, PluginConstants.SELECTION_MODE_MULTIPLE);

        final AbstractExternalDocumentFieldBrowserDialog dialog = createDialog(pluginConfig, List.of("doc1"));

        dialog.onOk();

        assertEquals(1, dialog.getSelectedExternalDocuments().getSize());
        verifyNoInteractions(facade);
    }
}
