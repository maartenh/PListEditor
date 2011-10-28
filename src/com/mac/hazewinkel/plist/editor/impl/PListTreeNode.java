/*
 * Copyright (c) 2011-2011. Maarten Hazewinkel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mac.hazewinkel.plist.editor.impl;

import com.mac.hazewinkel.plist.datamodel.*;

import javax.swing.tree.TreeNode;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

/**
 * @author Maarten Hazewinkel
 */
public class PListTreeNode implements TreeNode {
    private PList plist;
    private PListTreeNode parent;
    private String name;

    public PListTreeNode(PList plist) {
        this.plist = plist;
        this.name = plist.getClass().getName();
    }

    public PListTreeNode(PListEntry entry) {
        this.plist = entry.getValue();
        this.name = entry.getKey();
    }

    private void setParent(PListTreeNode pListTreeNode) {
        parent = pListTreeNode;
    }

    public PListTreeNode getChildAt(int childIndex) {
        if (plist instanceof PListAggregate) {
            PListEntry entry = ((PListAggregate) plist).elements().get(childIndex);
            PListTreeNode child = new PListTreeNode(entry);
            child.setParent(this);
            return child;
        } else {
            return null;
        }
    }

    public int getChildCount() {
        if (plist instanceof PListAggregate) {
            return ((PListAggregate) plist).elements().size();
        } else {
            return 0;
        }
    }

    public PListTreeNode getParent() {
        return parent;
    }

    public int getIndex(TreeNode node) {
        for (int i = 0; i < getChildCount(); i++) {
            if (node.equals(getChildAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public boolean getAllowsChildren() {
        return plist instanceof PListAggregate;
    }

    public boolean isLeaf() {
        return ! getAllowsChildren();
    }

    public Enumeration children() {
        if (plist instanceof PListAggregate) {
            final Enumeration<PListEntry> plistEnumeration = Collections.enumeration(((PListAggregate) plist).elements());
            return new Enumeration<PListTreeNode>() {
                public boolean hasMoreElements() {
                    return plistEnumeration.hasMoreElements();
                }

                public PListTreeNode nextElement() {
                    PListEntry entry = plistEnumeration.nextElement();
                    PListTreeNode child = new PListTreeNode(entry);
                    child.setParent(PListTreeNode.this);
                    return child;
                }
            };
        } else {
            return new Vector<PListTreeNode>().elements();
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getTypeName() {
        return plist.getType().name();
    }

    public String getAsString() {
        return plist.getAsString();
    }

    public boolean setAsString(String value) {
        if (plist instanceof PListPrimitive) {
            if (value != null && !value.equals(plist.getAsString())) {
                try {
                    ((PListPrimitive) plist).setAsString(value);
                    return true;
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        return false;
    }

    public boolean isNameEditable() {
        return parent != null && parent.plist instanceof PListDictionary;
    }

    public boolean setNameAsString(String value) {
        System.out.println("PListTreeNode.setNameAsString(" + value + ")");
        if (isNameEditable() && value != null && !value.equals(name)) {
            return ((PListDictionary) parent.plist).renameEntry(name, value);
        }
        return false;
    }
}
