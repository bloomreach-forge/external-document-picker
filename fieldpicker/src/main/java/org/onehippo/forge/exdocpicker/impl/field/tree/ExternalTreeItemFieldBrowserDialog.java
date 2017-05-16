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
package org.onehippo.forge.exdocpicker.impl.field.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.TableTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.CheckedFolder;
import org.apache.wicket.extensions.markup.html.repeater.tree.table.TreeColumn;
import org.apache.wicket.extensions.markup.html.repeater.tree.theme.HumanTheme;
import org.apache.wicket.extensions.markup.html.repeater.tree.theme.WindowsTheme;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentCollection;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceContext;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceFacade;
import org.onehippo.forge.exdocpicker.api.PluginConstants;
import org.onehippo.forge.exdocpicker.impl.field.AbstractExternalDocumentFieldBrowserDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalTreeItemFieldBrowserDialog extends AbstractExternalDocumentFieldBrowserDialog {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(ExternalTreeItemFieldBrowserDialog.class);

    private ExternalTreeItemDataProvider treeDataProvider;

    private Behavior theme;

    public ExternalTreeItemFieldBrowserDialog(IModel<String> titleModel,
            final ExternalDocumentServiceContext extDocServiceContext,
            final ExternalDocumentServiceFacade<Serializable> exdocService,
            IModel<ExternalDocumentCollection<Serializable>> model) {
        super(titleModel, extDocServiceContext, exdocService, model);

        final String themeName = extDocServiceContext.getPluginConfig().getString(
                PluginConstants.PARAM_EXTERNAL_TREE_VIEW_THEME, PluginConstants.DEFAULT_EXTERNAL_TREE_VIEW_THEME);

        if (StringUtils.startsWithIgnoreCase(themeName, "windows")) {
            theme = new WindowsTheme();
        } else {
            theme = new HumanTheme();
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(
                CssHeaderItem.forReference(new PackageResourceReference(ExternalTreeItemFieldBrowserDialog.class,
                        ExternalTreeItemFieldBrowserDialog.class.getSimpleName() + ".css")));
    }

    @Override
    protected void doInitialSearchOnExternalDocuments() {
        searchExternalTreeItems();
    }

    @Override
    protected void initDataListViewUI() {
        TreeItemExpansion expansion = new TreeItemExpansion();
        expansion.expandAll();

        treeDataProvider = new ExternalTreeItemDataProvider(searchedDocCollection, exdocService);
        AbstractTree<Serializable> treeDataView = createTree(new Model(expansion));
        treeDataView.setOutputMarkupId(true);

        treeDataView.add(new Behavior() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onComponentTag(Component component, ComponentTag tag) {
                theme.onComponentTag(component, tag);
            }

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                theme.renderHead(component, response);
            }
        });

        add(treeDataView);
    }

    protected void searchExternalTreeItems() {
        try {
            ExternalDocumentCollection<? extends Serializable> searchedDocs = exdocService
                    .searchExternalDocuments(extDocServiceContext, "");

            searchedDocCollection.clear();

            if (searchedDocs != null && searchedDocs.getSize() > 0) {
                for (Iterator<? extends Serializable> it = searchedDocs.iterator(); it.hasNext();) {
                    searchedDocCollection.add(it.next());
                }
            }
        } catch (Exception e) {
            log.error("Failed to execute search external tree items.", e);
        }
    }

    protected AbstractTree<Serializable> createTree(IModel<Set<Serializable>> state) {
        List<IColumn<Serializable, String>> columns = createColumns();

        final TableTree<Serializable, String> tree = new TableTree<Serializable, String>("tree", columns,
                treeDataProvider, Integer.MAX_VALUE, state) {
            private static final long serialVersionUID = 1L;

            @Override
            protected Component newContentComponent(String id, IModel<Serializable> model) {
                CheckableTreeNode treeNode = new CheckableTreeNode(id, this, model);
                treeNode.add(new AttributeModifier("title", exdocService.getDocumentDescription(extDocServiceContext,
                        model.getObject(), getRequest().getLocale())));
                return treeNode;
            }

            @Override
            protected Item<Serializable> newRowItem(String id, int index, IModel<Serializable> model) {
                return new OddEvenItem<>(id, index, model);
            }
        };

        tree.getTable().addBottomToolbar(new NoRecordsToolbar(tree.getTable()));

        return tree;
    }

    private List<IColumn<Serializable, String>> createColumns() {
        List<IColumn<Serializable, String>> columns = new ArrayList<>();
        columns.add(new TreeColumn<>(Model.of("Tree")));
        return columns;
    }

    private class CheckableTreeNode extends CheckedFolder<Serializable> {

        private static final long serialVersionUID = 1L;

        private AbstractTree<Serializable> tree;

        private boolean checked;

        public CheckableTreeNode(String id, AbstractTree<Serializable> tree, IModel<Serializable> model) {
            super(id, tree, model);
            this.tree = tree;
        }

        @Override
        protected IModel<?> newLabelModel(IModel<Serializable> model) {
            return Model.of(
                    exdocService.getDocumentTitle(extDocServiceContext, model.getObject(), getRequest().getLocale()));
        }

        @Override
        protected IModel<Boolean> newCheckBoxModel(final IModel<Serializable> model) {
            return new IModel<Boolean>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Boolean getObject() {
                    return selectedExtDocs.contains(model.getObject());
                }

                @Override
                public void setObject(Boolean object) {
                    if (Boolean.TRUE.equals(object)) {
                        selectedExtDocs.add(model.getObject());
                    } else {
                        selectedExtDocs.remove(model.getObject());
                    }
                }

                @Override
                public void detach() {
                }
            };
        }

        @Override
        protected void onUpdate(AjaxRequestTarget target) {
            Serializable foo = getModelObject();
            Serializable parent = treeDataProvider.getParent(foo);

            while (parent != null) {
                foo = parent;
                parent = treeDataProvider.getParent(parent);
            }

            tree.updateBranch(foo, target);
        }
    }

    private class TreeItemExpansion implements Set<Serializable>, Serializable {

        private static final long serialVersionUID = 1L;

        private Set<Serializable> items = new HashSet<>();
        private boolean inverse;

        public void expandAll() {
            items.clear();
            inverse = true;
        }

        public void collapseAll() {
            items.clear();
            inverse = false;
        }

        @Override
        public boolean add(Serializable item) {
            if (inverse) {
                return items.remove(item);
            } else {
                return items.add(item);
            }
        }

        @Override
        public boolean remove(Object o) {
            if (inverse) {
                return items.add((Serializable) o);
            } else {
                return items.remove(o);
            }
        }

        @Override
        public boolean contains(Object o) {
            if (inverse) {
                return !items.contains(o);
            } else {
                return items.contains(o);
            }
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <A> A[] toArray(A[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<Serializable> iterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object[] toArray() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends Serializable> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }
    }
}
