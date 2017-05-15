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

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SubjectCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String title;

    private SubjectCategory parent;
    private List<SubjectCategory> children;

    public SubjectCategory() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SubjectCategory getParent() {
        return parent;
    }

    public List<SubjectCategory> getChildren() {
        return (children != null) ? Collections.unmodifiableList(children) : Collections.emptyList();
    }

    public void addChild(SubjectCategory child) {
        if (children == null) {
            children = new LinkedList<>();
        }

        children.add(child);
        child.parent = this;
    }
}
