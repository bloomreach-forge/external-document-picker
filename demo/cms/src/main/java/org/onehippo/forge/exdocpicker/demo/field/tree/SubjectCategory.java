/**
 * Copyright 2022 Bloomreach B.V. (https://www.bloomreach.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         <a href="http://www.apache.org/licenses/LICENSE-2.0">http://www.apache.org/licenses/LICENSE-2.0</a>
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

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Subject category representation for a Mathematics Subject Classification.
 * <P>
 * This POJO bean is supposed to capture basic information of a Mathematics Subject Classification published
 * by American Mathematical Society. For full reference and examples,
 * see <a href="http://www.ams.org/mathscinet/msc/msc2010.html">http://www.ams.org/mathscinet/msc/msc2010.html</a>.
 * </p>
 */
public class SubjectCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String title;

    private SubjectCategory parent;
    private List<SubjectCategory> children;

    private boolean selectable = true;

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

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public String getPath() {
        String parentPath = (parent != null) ? parent.getPath() : null;

        if (parentPath != null) {
            return parentPath + "/" + getTitle();
        } else {
            return getTitle();
        }
    }

    public SubjectCategory getParent() {
        return parent;
    }

    public boolean hasChildren() {
        return !getChildren().isEmpty();
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SubjectCategory)) {
            return false;
        }

        SubjectCategory that = (SubjectCategory) o;
        return (id != null && id.equals(that.id));
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }

        return 31;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("title", title).toString();
    }
}
