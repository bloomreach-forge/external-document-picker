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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.TableTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.table.TreeColumn;
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
import org.onehippo.forge.exdocpicker.impl.field.AbstractExternalDocumentFieldBrowserDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalTreeItemFieldBrowserDialog extends AbstractExternalDocumentFieldBrowserDialog {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(ExternalTreeItemFieldBrowserDialog.class);

    public ExternalTreeItemFieldBrowserDialog(IModel<String> titleModel,
            final ExternalDocumentServiceContext extDocServiceContext,
            final ExternalDocumentServiceFacade<Serializable> exdocService,
            IModel<ExternalDocumentCollection<Serializable>> model) {
        super(titleModel, extDocServiceContext, exdocService, model);
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
        ExternalTreeItemDataProvider provider = new ExternalTreeItemDataProvider(searchedDocCollection, exdocService);
        ExternalTreeItemExpansion expansion = new ExternalTreeItemExpansion();
        AbstractTree<Serializable> treeDataView = createTree(provider, new Model(expansion));
        treeDataView.setOutputMarkupId(true);
        add(treeDataView);
    }

    protected void searchExternalTreeItems() {
        try {
            ExternalDocumentCollection<? extends Serializable> searchedDocs = exdocService.searchExternalDocuments(extDocServiceContext, "");

            searchedDocCollection.clear();

            if (searchedDocs != null && searchedDocs.getSize() > 0) {
                for (Iterator<? extends Serializable> it = searchedDocs.iterator(); it.hasNext(); ) {
                    searchedDocCollection.add(it.next());
                }
            }
        } catch (Exception e) {
            log.error("Failed to execute search external tree items.", e);
        }
    }

    protected AbstractTree<Serializable> createTree(ExternalTreeItemDataProvider provider, IModel<Set<Serializable>> state) {
        final CheckedSelectableFolderContent content = new CheckedSelectableFolderContent(provider);
        List<IColumn<Serializable, String>> columns = createColumns();

        final TableTree<Serializable, String> tree = new TableTree<Serializable, String>("tree", columns, provider, Integer.MAX_VALUE,
                state) {
            private static final long serialVersionUID = 1L;

            @Override
            protected Component newContentComponent(String id, IModel<Serializable> model) {
                return content.newContentComponent(id, this, model);
            }

            @Override
            protected Item<Serializable> newRowItem(String id, int index, IModel<Serializable> model) {
                return new OddEvenItem<>(id, index, model);
            }
        };

        tree.getTable().addTopToolbar(new HeadersToolbar<>(tree.getTable(), null));
        tree.getTable().addBottomToolbar(new NoRecordsToolbar(tree.getTable()));

        return tree;
    }

    private List<IColumn<Serializable, String>> createColumns() {
        List<IColumn<Serializable, String>> columns = new ArrayList<>();

        columns.add(new TreeColumn<>(Model.of("Tree")));
        columns.add(new PropertyColumn<>(Model.of("Title"), "title"));

        return columns;
    }
}
