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
package org.onehippo.forge.exdocpicker.impl.field;

import java.util.Collection;
import java.util.Iterator;

import net.sf.json.JSONObject;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.hippoecm.frontend.model.JcrNodeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalDocumentFieldListModel implements IDataProvider<JSONObject>, IDetachable {

    private static final long serialVersionUID = 1L;

    static final Logger log = LoggerFactory.getLogger(ExternalDocumentFieldListModel.class);

    private JcrNodeModel contextModel;

    public ExternalDocumentFieldListModel() {
    }

    public ExternalDocumentFieldListModel(JcrNodeModel contextModel) {
        if (contextModel == null) {
            return;
        }

        this.contextModel = contextModel;
    }

    public void add(JSONObject doc) {
        
    }

    public void addAll(Collection<JSONObject> docs) {
    }

    public void remove(JSONObject doc) {
    }

    @Override
    public Iterator<? extends JSONObject> iterator(long first, long count) {
        return null;
    }

    @Override
    public IModel<JSONObject> model(JSONObject doc) {
        return new Model<JSONObject>(doc);
    }

    @Override
    public void detach() {
    }

    @Override
    public long size() {
        return 0;
    }

}
