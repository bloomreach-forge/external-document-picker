/*
 * Copyright 2014-2024 Bloomreach B.V. (https://www.bloomreach.com)
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
package org.onehippo.forge.exdocpicker.demo.jpa.field;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentCollection;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceContext;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceFacade;
import org.onehippo.forge.exdocpicker.impl.SimpleExternalDocumentCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SimpleJPQLExternalDocumentServiceFacade.
 */
public class SimpleJPQLExternalDocumentServiceFacade implements ExternalDocumentServiceFacade<Serializable> {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(SimpleJPQLExternalDocumentServiceFacade.class);

    /**
     * Plugin parameter name for physical document field name (JCR property name).
     */
    public static final String PARAM_EXTERNAL_DOCS_FIELD_NAME = "jpql.external.docs.field.name";

    /**
     * JPA Data Persistence Unit name defined in persistence.xml.
     */
    private static final String JPQL_PERSISTENCE_UNIT = "jpql.persistence.unit";

    /**
     * JPQL Term Search Query which is used when user enters a text in input box and clicks on the search button.
     */
    private static final String JPQL_TERM_SEARCH_QUERY = "jpql.term.search.query";

    /**
     * JPQL Term Search Query parameter name.
     * For example, if the query is 'SELECT c FROM Consumer c WHERE c.type = :type', then
     * the parameter name should be 'type'.
     */
    private static final String JPQL_TERM_SEARCH_QUERY_PARAM = "jpql.term.search.query.param";

    /**
     * JPQL Item Search Query which is used when the plugin is loaded and the plugin tries to retrieve the
     * JPA data by the JCR Node property value.
     * For example, if this query is set to 'Select c From Consumer c WHERE c.id = :id', and
     * if the JCR Node has 'customerid' property with value, '123', then
     * the plugin may invoke this query with the parameter (see {@link #JPQL_ITEM_SEARCH_QUERY_PARAM} as well).
     */
    private static final String JPQL_ITEM_SEARCH_QUERY = "jpql.item.search.query";

    /**
     * JPQL Item Search Query parameter which is used when the plugin is loaded and the plugin tries to retrieve the
     * JPA data by the JCR Node property value.
     * For example, if the query is set to 'Select c From Consumer c WHERE c.id = :id', and
     * if the JCR Node has 'customerid' property with value, '123', then
     * the plugin may invoke the query with the parameter. (See {@link #JPQL_ITEM_SEARCH_QUERY} as well).
     */
    private static final String JPQL_ITEM_SEARCH_QUERY_PARAM = "jpql.item.search.query.param";

    /**
     * ID property name of the result list item bean.
     * This ID property is used to store in the JCR property.
     */
    private static final String JPQL_ITEM_ID_PROP = "jpql.item.id.prop";

    /**
     * The data type of ID property name of the result list item bean.
     * This data type of ID property is used to when executing the item search query to set the parameter.
     */
    private static final String JPQL_ITEM_ID_TYPE_PROP = "jpql.item.id.type.prop";

    /**
     * Title property name of the result list item bean.
     * This Title property is used to display an item in the search result in the picker dialog.
     */
    private static final String JPQL_ITEM_TITLE_PROP = "jpql.item.title.prop";

    /**
     * Description property name of the result list item bean.
     * This Description property is used to display an item in the search result in the picker dialog.
     */
    private static final String JPQL_ITEM_DESCRIPTION_PROP = "jpql.item.description.prop";

    /**
     * IconLink property name of the result list item bean.
     * This IconLink property is used to display the icon of an item in the search result in the picker dialog.
     */
    private static final String JPQL_ITEM_ICONLINK_PROP = "jpql.item.iconlink.prop";

    private EntityManagerFactory entityManagerfactory = null;

    @Override
    public ExternalDocumentCollection<Serializable> searchExternalDocuments(ExternalDocumentServiceContext context, String queryString) {
        ExternalDocumentCollection<Serializable> docCollection = new SimpleExternalDocumentCollection<>();

        EntityManager em = null;

        try {
            em = getEntityManager(context);

            Query query = createTermSearchQuery(context, em, queryString);

            if (query != null) {
                List<? extends Serializable> resultList = query.getResultList();

                for (Serializable doc : resultList) {
                    docCollection.add(doc);
                }
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return docCollection;
    }

    @Override
    public ExternalDocumentCollection<Serializable> getFieldExternalDocuments(ExternalDocumentServiceContext context) {
        ExternalDocumentCollection<Serializable> docCollection = new SimpleExternalDocumentCollection<Serializable>();

        final String fieldName = context.getPluginConfig().getString(PARAM_EXTERNAL_DOCS_FIELD_NAME);

        if (StringUtils.isBlank(fieldName)) {
            log.error("Invalid plugin configuration parameter for '{}': {}", PARAM_EXTERNAL_DOCS_FIELD_NAME, fieldName);
            return docCollection;
        }

        EntityManager em = null;

        try {
            final Node contextNode = context.getContextModel().getNode();

            if (contextNode.hasProperty(fieldName)) {
                em = getEntityManager(context);

                Value [] values = contextNode.getProperty(fieldName).getValues();
                Query query;

                for (Value value : values) {
                    query = createItemSearchQuery(context, em, value);

                    if (query != null) {
                        List<? extends Serializable> resultList = query.getResultList();

                        for (Serializable doc : resultList) {
                            docCollection.add(doc);
                        }
                    }
                }
            }
        } catch (RepositoryException e) {
            log.error("Failed to retrieve related exdoc array field.", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return docCollection;
    }

    @Override
    public void setFieldExternalDocuments(ExternalDocumentServiceContext context, ExternalDocumentCollection<Serializable> exdocs) {
        final String fieldName = context.getPluginConfig().getString(PARAM_EXTERNAL_DOCS_FIELD_NAME);

        if (StringUtils.isBlank(fieldName)) {
            log.error("Invalid plugin configuration parameter for '{}': {}", PARAM_EXTERNAL_DOCS_FIELD_NAME, fieldName);
            return;
        }

        String idProp = context.getPluginConfig().getString(JPQL_ITEM_ID_PROP, "id");

        if (StringUtils.isBlank(idProp)) {
            log.error("Invalid plugin configuration parameter for '{}': {}", JPQL_ITEM_ID_PROP, idProp);
            return;
        }

        try {
            final Node contextNode = context.getContextModel().getNode();
            final List<String> docIds = new ArrayList<String>();

            for (Iterator<? extends Serializable> it = exdocs.iterator(); it.hasNext(); ) {
                Serializable doc = it.next();
                docIds.add(BeanUtils.getProperty(doc, idProp));
            }

            contextNode.setProperty(fieldName, docIds.toArray(new String[docIds.size()]));
        } catch (Exception e) {
            log.error("Failed to set related exdoc array field.", e);
        }
    }

    @Override
    public String getDocumentTitle(ExternalDocumentServiceContext context, Serializable doc, Locale preferredLocale) {
        String title = "";
        String prop = context.getPluginConfig().getString(JPQL_ITEM_TITLE_PROP);

        if (StringUtils.isBlank(prop)) {
            log.error("Invalid plugin configuration parameter for '{}': {}", JPQL_ITEM_TITLE_PROP, prop);
            return title;
        }

        try {
            title = BeanUtils.getProperty(doc, prop);
        } catch (Exception e) {
            log.error("Failed to retrieve property, '{}', from {}.", prop, doc);
        }

        return title;
    }

    @Override
    public String getDocumentDescription(ExternalDocumentServiceContext context, Serializable doc, Locale preferredLocale) {
        String description = "";
        String prop = context.getPluginConfig().getString(JPQL_ITEM_DESCRIPTION_PROP);

        if (StringUtils.isBlank(prop)) {
            log.warn("Empty plugin configuration parameter for '{}': {}", JPQL_ITEM_DESCRIPTION_PROP, prop);
            return description;
        }

        try {
            description = BeanUtils.getProperty(doc, prop);
        } catch (Exception e) {
            log.error("Failed to retrieve property, '{}', from {}.", prop, doc);
        }

        return description;
    }

    @Override
    public String getDocumentIconLink(ExternalDocumentServiceContext context, Serializable doc, Locale preferredLocale) {
        String iconLink = "";
        String prop = context.getPluginConfig().getString(JPQL_ITEM_ICONLINK_PROP);

        if (StringUtils.isBlank(prop)) {
            log.warn("Emtpy plugin configuration parameter for '{}': {}", JPQL_ITEM_ICONLINK_PROP, prop);
            return iconLink;
        }

        try {
            iconLink = BeanUtils.getProperty(doc, prop);
        } catch (Exception e) {
            log.error("Failed to retrieve property, '{}', from {}.", prop, doc);
        }

        return iconLink;
    }

    protected EntityManager getEntityManager(ExternalDocumentServiceContext context) {
        if (entityManagerfactory == null) {
            String persistenceUnitName = context.getPluginConfig().getString(JPQL_PERSISTENCE_UNIT);

            if (StringUtils.isBlank(persistenceUnitName)) {
                log.error("Invalid plugin configuration parameter for '{}': {}", JPQL_PERSISTENCE_UNIT, persistenceUnitName);
                return null;
            }

            entityManagerfactory = Persistence.createEntityManagerFactory(persistenceUnitName);
        }

        return entityManagerfactory.createEntityManager();
    }

    protected Query createTermSearchQuery(ExternalDocumentServiceContext context, final EntityManager entityManager, final String searchTerm) {
        String qlString = context.getPluginConfig().getString(JPQL_TERM_SEARCH_QUERY);

        if (StringUtils.isBlank(qlString)) {
            log.error("Invalid plugin configuration parameter for '{}': {}", JPQL_TERM_SEARCH_QUERY, qlString);
            return null;
        }

        Query query = entityManager.createQuery(qlString);

        String paramName = context.getPluginConfig().getString(JPQL_TERM_SEARCH_QUERY_PARAM);

        if (StringUtils.isNotBlank(paramName)) {
            query.setParameter(paramName, searchTerm);
        }

        return query;
    }

    protected Query createItemSearchQuery(ExternalDocumentServiceContext context, final EntityManager entityManager, final Value value) throws RepositoryException {
        String qlString = context.getPluginConfig().getString(JPQL_ITEM_SEARCH_QUERY);
        Query query = entityManager.createQuery(qlString);

        if (StringUtils.isBlank(qlString)) {
            log.error("Invalid plugin configuration parameter for '{}': {}", JPQL_ITEM_SEARCH_QUERY, qlString);
            return null;
        }

        String paramName = context.getPluginConfig().getString(JPQL_ITEM_SEARCH_QUERY_PARAM);

        if (StringUtils.isNotBlank(paramName)) {
            String type = context.getPluginConfig().getString(JPQL_ITEM_ID_TYPE_PROP);

            if (StringUtils.equalsIgnoreCase("long", type)) {
                query.setParameter(paramName, NumberUtils.toLong(value.getString()));
            } else if (StringUtils.equalsIgnoreCase("int", type) || StringUtils.equalsIgnoreCase("integer", type)) {
                query.setParameter(paramName, NumberUtils.toInt(value.getString()));
            } else if (StringUtils.equalsIgnoreCase("float", type)) {
                query.setParameter(paramName, NumberUtils.toFloat(value.getString()));
            } else if (StringUtils.equalsIgnoreCase("double", type)) {
                query.setParameter(paramName, NumberUtils.toDouble(value.getString()));
            } else {
                query.setParameter(paramName, value.getString());
            }
        }

        return query;
    }
}
