/**
 * Copyright 2017 Hippo B.V. (http://www.onehippo.com)
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
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
import org.apache.wicket.markup.html.form.Form;
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

/**
 * External document field picker dialog implementation with the default tree list view UI, providing a basic tree
 * navigation capability and expand-all / collapse-all buttons.
 */
public class ExternalTreeItemFieldBrowserDialog extends AbstractExternalDocumentFieldBrowserDialog {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(ExternalTreeItemFieldBrowserDialog.class);

    /**
     * {@link ExternalTreeItemDataProvider} instance.
     */
    private ExternalTreeItemDataProvider<Serializable> treeDataProvider;

    /**
     * {@link TreeItemExpansionSet} instance.
     */
    private TreeItemExpansionSet treeExpansionSet;

    /**
     * Tree list view UI theme.
     */
    private Behavior theme;

    private final AjaxButton expandSelectedButton;
    private final AjaxButton expandAllButton;
    private final AjaxButton collapseAllButton;

    /**
     * Constructs external document(s) picker dialog.
     * @param titleModel title model
     * @param extDocServiceContext {@link ExternalDocumentServiceContext} instance
     * @param exdocService {@link ExternalDocumentServiceFacade} instance
     * @param model the model containing the currently selected external documents in the document node data
     */
    public ExternalTreeItemFieldBrowserDialog(IModel<String> titleModel,
            final ExternalDocumentServiceContext extDocServiceContext,
            final ExternalDocumentServiceFacade<Serializable> exdocService,
            IModel<ExternalDocumentCollection<Serializable>> model) {
        super(titleModel, extDocServiceContext, exdocService, model);

        final String themeName = extDocServiceContext.getPluginConfig().getString(
                PluginConstants.PARAM_EXTERNAL_TREE_VIEW_THEME, PluginConstants.DEFAULT_EXTERNAL_TREE_VIEW_THEME);

        if (StringUtils.startsWithIgnoreCase(themeName, "window")) {
            theme = new WindowsTheme();
        } else {
            theme = new HumanTheme();
        }

        expandSelectedButton = new AjaxButton("expand-selected-button") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                expandPickedExternalTreeItemNodes(getExternalDocumentServiceFacade());
                target.add(ExternalTreeItemFieldBrowserDialog.this);
            }
        };
        add(expandSelectedButton);

        expandAllButton = new AjaxButton("expand-all-button") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                treeExpansionSet.expandAll();
                target.add(ExternalTreeItemFieldBrowserDialog.this);
            }
        };
        add(expandAllButton);

        collapseAllButton = new AjaxButton("collapse-all-button") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                treeExpansionSet.collapseAll();
                target.add(ExternalTreeItemFieldBrowserDialog.this);
            }
        };
        add(collapseAllButton);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(
                CssHeaderItem.forReference(new PackageResourceReference(ExternalTreeItemFieldBrowserDialog.class,
                        ExternalTreeItemFieldBrowserDialog.class.getSimpleName() + ".css")));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeSearchedExternalDocuments() {
        searchExternalTreeNodeItems();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeDataListView() {
        treeExpansionSet = new TreeItemExpansionSet();

        final int initialExpandDepth = getExternalDocumentServiceContext().getPluginConfig()
                .getAsInteger(PluginConstants.PARAM_INITIAL_TREE_EXPAND_DEPTH, 0);

        // Expand tree nodes to the initial depth level if configured.
        if (initialExpandDepth > 0) {
            for (Iterator<? extends Serializable> itemIt = getSearchedExternalDocuments().iterator(); itemIt
                    .hasNext();) {
                expandExternalTreeItemNode(getExternalDocumentServiceFacade(), itemIt.next(), 0, initialExpandDepth);
            }
        }

        // Expand parent tree nodes of currently selected items.
        expandPickedExternalTreeItemNodes(getExternalDocumentServiceFacade());

        treeDataProvider = new ExternalTreeItemDataProvider<>(getSearchedExternalDocuments(),
                getExternalDocumentServiceFacade());
        AbstractTree<Serializable> treeDataView = createTree(new Model(treeExpansionSet));
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

    /**
     * Search tree node items.
     */
    protected void searchExternalTreeNodeItems() {
        try {
            ExternalDocumentCollection<? extends Serializable> searchedDocs = getExternalDocumentServiceFacade()
                    .searchExternalDocuments(getExternalDocumentServiceContext(), "");

            getSearchedExternalDocuments().clear();

            if (searchedDocs != null && searchedDocs.getSize() > 0) {
                for (Iterator<? extends Serializable> it = searchedDocs.iterator(); it.hasNext();) {
                    getSearchedExternalDocuments().add(it.next());
                }
            }
        } catch (Exception e) {
            log.error("Failed to execute search external tree items.", e);
        }
    }

    /**
     * Create a tree list view UI component.
     * @param state expansion state
     * @return a tree list view UI component
     */
    protected AbstractTree<Serializable> createTree(IModel<Set<Serializable>> state) {
        List<IColumn<Serializable, String>> columns = createColumns();

        final TableTree<Serializable, String> tree = new TableTree<Serializable, String>("tree", columns,
                treeDataProvider, Integer.MAX_VALUE, state) {
            private static final long serialVersionUID = 1L;

            @Override
            protected Component newContentComponent(String id, IModel<Serializable> model) {
                CheckableTreeNode treeNode = new CheckableTreeNode(id, this, model);
                treeNode.add(new AttributeModifier("title", getExternalDocumentServiceFacade().getDocumentDescription(
                        getExternalDocumentServiceContext(), model.getObject(), getRequest().getLocale())));
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

    private void expandExternalTreeItemNode(ExternalDocumentServiceFacade<Serializable> extDocService,
            Serializable item, int curDepth, int maxDepth) {
        if (curDepth < maxDepth) {
            treeExpansionSet.add(item);

            if (extDocService.hasChildren(item)) {
                for (Iterator<Serializable> childIt = extDocService.getChildren(item); childIt.hasNext();) {
                    expandExternalTreeItemNode(extDocService, childIt.next(), curDepth + 1, maxDepth);
                }
            }
        }
    }

    private void expandPickedExternalTreeItemNodes(ExternalDocumentServiceFacade<Serializable> extDocService) {
        for (Serializable item : getPickedExternalDocuments()) {
            Serializable parent = extDocService.getParent(item);
            while (parent != null) {
                treeExpansionSet.add(parent);
                parent = getExternalDocumentServiceFacade().getParent(parent);
            }
        }
    }

    private class CheckableTreeNode extends CheckedFolder<Serializable> {

        private static final long serialVersionUID = 1L;

        private AbstractTree<Serializable> tree;

        public CheckableTreeNode(String id, AbstractTree<Serializable> tree, IModel<Serializable> model) {
            super(id, tree, model);
            this.tree = tree;
        }

        @Override
        protected IModel<?> newLabelModel(IModel<Serializable> model) {
            return Model.of(getExternalDocumentServiceFacade().getDocumentTitle(getExternalDocumentServiceContext(),
                    model.getObject(), getRequest().getLocale()));
        }

        @Override
        protected Component newCheckBox(String id, IModel<Serializable> model) {
            Component checkbox = super.newCheckBox(id, model);
            checkbox.setEnabled(getExternalDocumentServiceFacade()
                    .isDocumentSelectable(getExternalDocumentServiceContext(), model.getObject()));
            return checkbox;
        }

        @Override
        protected IModel<Boolean> newCheckBoxModel(final IModel<Serializable> model) {
            return new IModel<Boolean>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Boolean getObject() {
                    return getPickedExternalDocuments().contains(model.getObject());
                }

                @Override
                public void setObject(Boolean object) {
                    if (Boolean.TRUE.equals(object)) {
                        getPickedExternalDocuments().add(model.getObject());

                        if (isSingleSelectionMode()) {
                            ExternalTreeItemFieldBrowserDialog.this.handleSubmit();
                        }
                    } else {
                        getPickedExternalDocuments().remove(model.getObject());
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
}
