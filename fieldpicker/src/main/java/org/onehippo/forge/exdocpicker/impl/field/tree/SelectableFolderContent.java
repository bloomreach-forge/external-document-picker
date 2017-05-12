package org.onehippo.forge.exdocpicker.impl.field.tree;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.model.IModel;

public class SelectableFolderContent extends Content
{

    private static final long serialVersionUID = 1L;

    private ITreeProvider<Foo> provider;

    private IModel<Foo> selected;

    public SelectableFolderContent(ITreeProvider<Foo> provider)
    {
        this.provider = provider;
    }

    @Override
    public void detach()
    {
        if (selected != null)
        {
            selected.detach();
        }
    }

    protected boolean isSelected(Foo foo)
    {
        IModel<Foo> model = provider.model(foo);

        try
        {
            return selected != null && selected.equals(model);
        }
        finally
        {
            model.detach();
        }
    }

    protected void select(Foo foo, final AbstractTree<Foo> tree, final AjaxRequestTarget target)
    {
        if (selected != null)
        {
            if (target != null) {
                tree.updateNode(selected.getObject(), target);
            }

            selected.detach();
            selected = null;
        }

        selected = provider.model(foo);

        if (target != null) {
            tree.updateNode(foo, target);
        }
    }

    @Override
    public Component newContentComponent(String id, final AbstractTree<Foo> tree, IModel<Foo> model)
    {
        return new Folder<Foo>(id, tree, model)
        {
            private static final long serialVersionUID = 1L;

            /**
             * Always clickable.
             */
            @Override
            protected boolean isClickable()
            {
                return true;
            }

            @Override
            protected void onClick(AjaxRequestTarget target)
            {
                SelectableFolderContent.this.select(getModelObject(), tree, target);
            }

            @Override
            protected boolean isSelected()
            {
                return SelectableFolderContent.this.isSelected(getModelObject());
            }
        };
    }
}