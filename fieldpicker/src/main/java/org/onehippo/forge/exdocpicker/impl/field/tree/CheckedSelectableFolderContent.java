package org.onehippo.forge.exdocpicker.impl.field.tree;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.CheckedFolder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class CheckedSelectableFolderContent extends SelectableFolderContent {
    private static final long serialVersionUID = 1L;

    public CheckedSelectableFolderContent(ExternalTreeItemDataProvider provider) {
        super(provider);
    }

    @Override
    public Component newContentComponent(String id, final AbstractTree<Serializable> tree, IModel<Serializable> model) {
        return new CheckedFolder<Serializable>(id, tree, model) {
            private static final long serialVersionUID = 1L;

            @Override
            protected IModel<Boolean> newCheckBoxModel(final IModel<Serializable> model) {
                // FIXME
                return Model.of(Boolean.FALSE);
            }

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                Serializable foo = getModelObject();
                Serializable parent = provider.getParent(foo);
                while (parent != null) {
                    foo = parent;
                    parent = provider.getParent(parent);
                }
                tree.updateBranch(foo, target);
            }

            /**
             * Always clickable.
             */
            @Override
            protected boolean isClickable() {
                return true;
            }

            @Override
            protected void onClick(AjaxRequestTarget target) {
                CheckedSelectableFolderContent.this.select(getModelObject(), tree, target);
            }

            @Override
            protected boolean isSelected() {
                return CheckedSelectableFolderContent.this.isSelected(getModelObject());
            }
        };
    }
}