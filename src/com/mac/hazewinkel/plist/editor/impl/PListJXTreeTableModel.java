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

import com.intellij.openapi.project.Project;
import com.mac.hazewinkel.plist.datamodel.PList;
import com.mac.hazewinkel.plist.datamodel.PListDataType;
import org.jdesktop.swingx.treetable.TreeTableModel;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;

/**
 * @author Maarten Hazewinkel
 */
public class PListJXTreeTableModel extends DefaultTreeModel implements TreeTableModel {

    private boolean modified;
    private PList plist;
    private Project project;
    private PListColumnInfo[] columns;

    public PListJXTreeTableModel(PList plist, Project project) {
        super(new PListJXTreeTableNode(plist), true);
        this.plist = plist;
        this.project = project;
        this.columns = getPListColumns(project);
    }

    private PListColumnInfo[] getPListColumns(Project project) {
        PListColumnInfo[] columns = new PListColumnInfo[3];
        columns[0] = new PListNameColumnInfo("Key", project, this);
        columns[1] = new PListTypeColumnInfo("Type", project, this);
        columns[2] = new PlistValueColumnInfo("Value", project, this);
        return columns;
    }

    public Class<?> getColumnClass(int i) {
        return columns[i].getColumnClass();
    }

    public int getColumnCount() {
        return 3;
    }

    public String getColumnName(int i) {
        return columns[i].getColumnName();
    }

    public int getHierarchicalColumn() {
        return 0;
    }

    public Object getValueAt(Object node, int i) {
        return columns[i].valueOf((PListJXTreeTableNode) node);
    }

    public boolean isCellEditable(Object node, int i) {
        return columns[i].isCellEditable((PListJXTreeTableNode) node);
    }

    public void setValueAt(Object value, Object node, int i) {
        columns[i].setValue((PListJXTreeTableNode) node, value.toString());
    }

    public void setModified(boolean b) {
        modified = b;
    }

    public boolean isModified() {
        return modified;
    }

    public Project getProject() {
        return project;
    }

    public static abstract class PListColumnInfo {
        private String name;
        protected Project project;
        protected PListJXTreeTableModel model;

        public PListColumnInfo(final String name, Project project, PListJXTreeTableModel model) {
            this.name = name;
            this.project = project;
            this.model = model;
        }

        public String getColumnName() {
            return name;
        }

        public abstract Class<?> getColumnClass();

        public abstract Object valueOf(PListJXTreeTableNode node);

        public abstract boolean isCellEditable(PListJXTreeTableNode node);

        public abstract void setValue(PListJXTreeTableNode node, String value);
    }

    public static class PListNameColumnInfo extends PListColumnInfo {
        public PListNameColumnInfo(String name, Project project, PListJXTreeTableModel model) {
            super(name, project, model);
        }

        @Override
        public final Class getColumnClass() { return PListJXTreeTableNode.class; }

        @Override
        public PListJXTreeTableNode valueOf(PListJXTreeTableNode pListTreeNode) {
            return pListTreeNode;
        }

        @Override
        public boolean isCellEditable(PListJXTreeTableNode pListTreeNode) {
            return pListTreeNode.isNameEditable();
        }

        @Override
        public void setValue(PListJXTreeTableNode pListTreeNode, String value) {
            try {
                boolean changed = pListTreeNode.setNameAsString(value);
                model.setModified(changed);
                if (changed) {
                    model.nodeStructureChanged(pListTreeNode.getParent());
                }
            } catch (Throwable ignored) {
            }
        }
    }

    public static class PListTypeColumnInfo extends PListColumnInfo {
        public PListTypeColumnInfo(String name, Project project, PListJXTreeTableModel model) {
            super(name, project, model);
        }

        @Override
        public Class<?> getColumnClass() {
            return String[].class;
        }

        @Override
        public boolean isCellEditable(PListJXTreeTableNode pListTreeNode) {
            return true;
        }

        @Override
        public String valueOf(PListJXTreeTableNode pListTreeNode) {
            return pListTreeNode.getTypeName();
        }

        @Override
        public void setValue(PListJXTreeTableNode pListTreeNode, String value) {
            try {
                boolean changed = pListTreeNode.changeType(value);
                model.setModified(changed);
                if (changed) {
                    model.nodeStructureChanged(pListTreeNode);
                }
            } catch (Throwable ignored) {
            }
        }
    }

    public static class PlistValueColumnInfo extends PListColumnInfo {
        public PlistValueColumnInfo(final String name, Project project, PListJXTreeTableModel model) {
            super(name, project, model);
        }

        @Override
        public Class<?> getColumnClass() {
            return String.class;
        }

        @Override
        public String valueOf(PListJXTreeTableNode pListTreeNode) {
            return pListTreeNode.getAsString();
        }

        @Override
        public boolean isCellEditable(PListJXTreeTableNode pListTreeNode) {
            return pListTreeNode.isLeaf();
        }

        @Override
        public void setValue(PListJXTreeTableNode pListTreeNode, String value) {
            try {
                boolean changed = pListTreeNode.setAsString(value);
                model.setModified(changed);
                if (changed) {
                    model.nodeChanged(pListTreeNode);
                }
            } catch (Throwable ignored) {
            }
        }
    }
}
