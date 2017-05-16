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
package org.onehippo.forge.exdocpicker.demo.field.tree;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentCollection;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceContext;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceFacade;
import org.onehippo.forge.exdocpicker.impl.SimpleExternalDocumentCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ExampleExternalSubjectCategoryServiceFacade implements ExternalDocumentServiceFacade<SubjectCategory> {

    /**
     * Plugin parameter name for physical categories field name (JCR property name).
     */
    public static final String PARAM_EXTERNAL_CATS_FIELD_NAME = "example.external.cats.field.name";

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(ExampleExternalSubjectCategoryServiceFacade.class);

    private List<SubjectCategory> rootSubjectCategories;
    private Map<String, SubjectCategory> subjectCategoriesMap;

    public ExampleExternalSubjectCategoryServiceFacade() {
        InputStream input = null;

        try {
            rootSubjectCategories = new LinkedList<>();
            subjectCategoriesMap = new HashMap<>();

            input = ExampleExternalSubjectCategoryServiceFacade.class.getResourceAsStream("msc.xml");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(input));
            Element root = document.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            int length = nodeList.getLength();
            org.w3c.dom.Node node;
            SubjectCategory subjectCategory;

            for (int i = 0; i < length; i++) {
                node = nodeList.item(i);
                if (isSubjectNode(node)) {
                    subjectCategory = convertSubjectElementToSubjectCategory((Element) node);
                    rootSubjectCategories.add(subjectCategory);
                }
            }
        } catch (Exception e) {
            log.error("Failed to load JSON data.", e);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    @Override
    public ExternalDocumentCollection<SubjectCategory> searchExternalDocuments(ExternalDocumentServiceContext context,
            String queryString) {
        ExternalDocumentCollection<SubjectCategory> catCollection = new SimpleExternalDocumentCollection<SubjectCategory>();
        int size = rootSubjectCategories.size();

        for (int i = 0; i < size; i++) {
            catCollection.add(rootSubjectCategories.get(i));
        }

        return catCollection;
    }

    @Override
    public ExternalDocumentCollection<SubjectCategory> getFieldExternalDocuments(
            ExternalDocumentServiceContext context) {
        final String fieldName = context.getPluginConfig().getString(PARAM_EXTERNAL_CATS_FIELD_NAME);

        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException("Invalid plugin configuration parameter for '"
                    + PARAM_EXTERNAL_CATS_FIELD_NAME + "': " + fieldName);
        }

        ExternalDocumentCollection<SubjectCategory> catCollection = new SimpleExternalDocumentCollection<SubjectCategory>();

        try {
            final Node contextNode = context.getContextModel().getNode();

            if (contextNode.hasProperty(fieldName)) {
                Value[] values = contextNode.getProperty(fieldName).getValues();

                for (Value value : values) {
                    String id = value.getString();
                    SubjectCategory cat = subjectCategoriesMap.get(id);

                    if (cat != null) {
                        catCollection.add(cat);
                    }
                }
            }
        } catch (RepositoryException e) {
            log.error("Failed to retrieve related exdoc array field.", e);
        }

        return catCollection;
    }

    @Override
    public void setFieldExternalDocuments(ExternalDocumentServiceContext context,
            ExternalDocumentCollection<SubjectCategory> excats) {
        final String fieldName = context.getPluginConfig().getString(PARAM_EXTERNAL_CATS_FIELD_NAME);

        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException("Invalid plugin configuration parameter for '"
                    + PARAM_EXTERNAL_CATS_FIELD_NAME + "': " + fieldName);
        }

        try {
            final Node contextNode = context.getContextModel().getNode();
            final List<String> catIds = new ArrayList<String>();

            for (Iterator<? extends SubjectCategory> it = excats.iterator(); it.hasNext();) {
                SubjectCategory cat = it.next();
                catIds.add(cat.getId());
            }

            contextNode.setProperty(fieldName, catIds.toArray(new String[catIds.size()]));
        } catch (RepositoryException e) {
            log.error("Failed to set related excat array field.", e);
        }
    }

    @Override
    public String getDocumentTitle(ExternalDocumentServiceContext context, SubjectCategory cat,
            Locale preferredLocale) {
        return cat.getTitle();
    }

    @Override
    public String getDocumentDescription(ExternalDocumentServiceContext context, SubjectCategory cat,
            Locale preferredLocale) {
        return cat.getPath();
    }

    @Override
    public String getDocumentIconLink(ExternalDocumentServiceContext context, SubjectCategory cat,
            Locale preferredLocale) {
        return null;
    }

    @Override
    public boolean hasChildren(SubjectCategory cat) {
        return cat.hasChildren();
    }

    @Override
    public Iterator<SubjectCategory> getChildren(SubjectCategory cat) {
        return cat.getChildren().iterator();
    }

    @Override
    public SubjectCategory getParent(SubjectCategory cat) {
        return cat.getParent();
    }

    private SubjectCategory convertSubjectElementToSubjectCategory(Element elem) {
        SubjectCategory category = new SubjectCategory();
        category.setId(elem.getAttribute("id"));
        category.setTitle(elem.getAttribute("name"));

        subjectCategoriesMap.put(category.getId(), category);

        NodeList nodeList = elem.getChildNodes();
        int length = nodeList.getLength();
        org.w3c.dom.Node node;

        for (int i = 0; i < length; i++) {
            node = nodeList.item(i);
            if (isSubjectNode(node)) {
                category.addChild(convertSubjectElementToSubjectCategory((Element) node));
            }
        }

        return category;
    }

    private boolean isSubjectNode(org.w3c.dom.Node node) {
        return (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE && "subject".equals(node.getNodeName()));
    }
}
