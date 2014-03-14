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
package org.onehippo.forge.exdocpicker.impl.field;

import java.util.Iterator;

import javax.jcr.Node;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.hippoecm.frontend.dialog.AbstractDialog;
import org.hippoecm.frontend.dialog.DialogAction;
import org.hippoecm.frontend.dialog.IDialogFactory;
import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.model.event.IObservable;
import org.hippoecm.frontend.model.event.IObserver;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.service.IEditor;
import org.hippoecm.frontend.service.render.RenderPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalDocumentFieldSelectorPlugin extends RenderPlugin<Node> implements IObserver {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(ExternalDocumentFieldSelectorPlugin.class);

    private JcrNodeModel documentModel;

    public ExternalDocumentFieldSelectorPlugin(final IPluginContext context, IPluginConfig config) {
        super(context, config);

        documentModel = (JcrNodeModel) getModel();

        if (isEditMode() && documentModel != null) {
            context.registerService(new IObserver() {
                public IObservable getObservable() {
                    return documentModel;
                }
                public void onEvent(Iterator events) {
                    redraw();
                }
            }, IObserver.class.getName());
        }

        IDialogFactory dialogFactory = createDialogFactory();
        final DialogAction action = new DialogAction(dialogFactory, getDialogService());

        AjaxLink<String> dialogLink = new AjaxLink<String>("browser-select") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                action.execute();
            }
        };

        dialogLink.add(new Label("link-text", new StringResourceModel("picker.browse", this, null)));
        dialogLink.setVisible(isEditMode());
        add(dialogLink);
    }

    protected boolean isEditMode() {
        return IEditor.Mode.EDIT.equals(IEditor.Mode.fromString(getPluginConfig().getString("mode", "view")));
    }

    protected boolean isCompareMode() {
        return IEditor.Mode.COMPARE.equals(IEditor.Mode.fromString(getPluginConfig().getString("mode", "view")));
    }

    protected IDialogFactory createDialogFactory() {
        return new IDialogFactory() {
            private static final long serialVersionUID = 1L;

            public AbstractDialog<ExternalDocumentFieldListModel> createDialog() {
                return new ExternalDocumentFieldBrowserDialog(getPluginContext(), getPluginConfig(), new Model(new ExternalDocumentFieldListModel(documentModel)));
            }
        };
    }

}

