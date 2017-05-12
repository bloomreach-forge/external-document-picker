package org.onehippo.forge.exdocpicker.impl.field.tree;

import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.CheckedFolder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class CheckedSelectableFolderContent extends SelectableFolderContent
{
    private static final long serialVersionUID = 1L;

    public CheckedSelectableFolderContent(ITreeProvider<Foo> provider)
    {
        super(provider);
    }

    @Override
    public Component newContentComponent(String id, final AbstractTree<Foo> tree, IModel<Foo> model)
    {
        return new CheckedFolder<Foo>(id, tree, model)
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected IModel<Boolean> newCheckBoxModel(final IModel<Foo> model)
            {
                return new PropertyModel<>(model, "quux");
            }

            @Override
            protected void onUpdate(AjaxRequestTarget target)
            {
                Foo foo = getModelObject();

                // search first ancestor with quux not set
                while (!foo.getQuux() && foo.getParent() != null)
                {
                    foo = foo.getParent();
                }

                tree.updateBranch(foo, target);
            }

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
                CheckedSelectableFolderContent.this.select(getModelObject(), tree, target);
            }

            @Override
            protected boolean isSelected()
            {
                return CheckedSelectableFolderContent.this.isSelected(getModelObject());
            }
        };
    }
}