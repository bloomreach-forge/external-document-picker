/**
 * Copyright 2017-2024 Bloomreach B.V. (<a href="https://www.bloomreach.com">https://www.bloomreach.com</a>)
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
package org.onehippo.forge.exdocpicker.demo.field;

import java.util.Calendar;

import javax.jcr.RepositoryException;

import org.apache.wicket.MarkupContainer;
import org.hippoecm.frontend.editor.editor.EditorForm;
import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.onehippo.forge.exdocpicker.impl.field.AbstractHiddenExternalDocumentFieldPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example hidden date field updater plugin.
 */
public class DateUpdatingHiddenFieldPlugin extends AbstractHiddenExternalDocumentFieldPlugin {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(DateUpdatingHiddenFieldPlugin.class);

    public DateUpdatingHiddenFieldPlugin(final IPluginContext context, IPluginConfig config) {
        super(context, config);
    }

    @Override
    protected void init() {
        if (isEditMode()) {
            try {
                JcrNodeModel documentModel = getDocumentModel();
                Calendar now = Calendar.getInstance();
                documentModel.getNode().setProperty("exdocpickerbasedemo:date", now);
                refreshDocumentEditor();
            } catch (RepositoryException e) {
                log.error("Failed to set date.", e);
            }
        }
    }

    private void refreshDocumentEditor() {
        MarkupContainer container = getParent();

        for ( ; container != null; container = container.getParent()) {
            if (container instanceof EditorForm) {
                ((EditorForm) container).onModelChanged();
                break;
            }
        }
    }

}
