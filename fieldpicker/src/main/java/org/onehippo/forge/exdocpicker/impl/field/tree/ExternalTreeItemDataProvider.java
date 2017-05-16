package org.onehippo.forge.exdocpicker.impl.field.tree;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentCollection;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentTreeService;

public class ExternalTreeItemDataProvider<T extends Serializable> implements ITreeProvider<T> {

    private static final long serialVersionUID = 1L;

    private final ExternalDocumentCollection<T> rootTreeItems;
    private final ExternalDocumentTreeService<T> treeService;

    public ExternalTreeItemDataProvider(final ExternalDocumentCollection<T> rootTreeItems, ExternalDocumentTreeService<T> treeService) {
        this.rootTreeItems = rootTreeItems;
        this.treeService = treeService;
    }

    @Override
    public Iterator<? extends T> getRoots() {
        return rootTreeItems.iterator();
    }

    @Override
    public boolean hasChildren(T item) {
        return treeService.hasChildren(item);
    }

    @Override
    public Iterator<T> getChildren(T item) {
        return treeService.getChildren(item);
    }

    @Override
    public IModel<T> model(Serializable item) {
        return new Model(item);
    }

    @Override
    public void detach() {
    }

    public T getParent(T item) {
        return treeService.getParent(item);
    }
}
