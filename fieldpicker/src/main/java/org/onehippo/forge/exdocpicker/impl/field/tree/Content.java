package org.onehippo.forge.exdocpicker.impl.field.tree;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;

public abstract class Content implements IDetachable
{

    /**
     * Create new content.
     */
    public abstract Component newContentComponent(String id, AbstractTree<Serializable> tree,
        IModel<Serializable> model);

    @Override
    public void detach()
    {
    }
}