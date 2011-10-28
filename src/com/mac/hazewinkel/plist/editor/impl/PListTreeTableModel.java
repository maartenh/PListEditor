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
import com.intellij.openapi.ui.ComboBoxTableRenderer;
import com.intellij.refactoring.ui.StringTableCellEditor;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModelOnColumns;
import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import com.intellij.util.ui.ColumnInfo;
import com.mac.hazewinkel.plist.datamodel.PList;
import com.mac.hazewinkel.plist.datamodel.PListDataType;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.util.ArrayList;

/**
 * @author Maarten Hazewinkel
 */
public class PListTreeTableModel extends ListTreeTableModelOnColumns {

    private boolean modified;

    public PListTreeTableModel(PList plist, Project project) {
        super(new PListTreeNode(plist), null);
        setColumns(getPListColumns(project));
    }

    private ColumnInfo[] getPListColumns(Project project) {
        ColumnInfo[] columns = new ColumnInfo[3];
        columns[0] = new PListNameColumnInfo("Key", project, this);
        columns[1] = new PListTypeColumnInfo("Type", project, this);
        columns[2] = new PlistValueColumnInfo("Value", project, this);
        return columns;
    }

    public static abstract class PListColumnInfo extends ColumnInfo<PListTreeNode, String> {
        protected Project project;
        protected PListTreeTableModel model;

        public PListColumnInfo(final String name, Project project, PListTreeTableModel model) {
            super(name);
            this.project = project;
            this.model = model;
        }

        public Class getColumnClass() { return PListTreeNode.class; }
    }

    public void setModified(boolean b) {
        modified = b;
    }

    public boolean isModified() {
        return modified;
    }

    public static class PListTreeColumnInfo extends PListColumnInfo {
        public PListTreeColumnInfo(String name, Project project, PListTreeTableModel model) {
            super(name, project, model);
        }

        @Override
        public final Class getColumnClass() { return TreeTableModel.class; }

        @Override
        public String valueOf(PListTreeNode pListTreeNode) { return null; }
    }

    public static class PListNameColumnInfo extends PListColumnInfo {
        public PListNameColumnInfo(String name, Project project, PListTreeTableModel model) {
            super(name, project, model);
        }

        @Override
        public final Class getColumnClass() { return TreeTableModel.class; }

        @Override
        public String valueOf(PListTreeNode pListTreeNode) {
            return pListTreeNode.getName();
        }

        @Override
        public boolean isCellEditable(PListTreeNode pListTreeNode) {
            return pListTreeNode.isNameEditable();
        }

        @Override
        public TableCellEditor getEditor(PListTreeNode pListTreeNode) {
            return new StringTableCellEditor(project);
        }

        @Override
        public TableCellRenderer getRenderer(PListTreeNode pListTreeNode) {
            return super.getRenderer(pListTreeNode);
        }

        @Override
        public TableCellRenderer getCustomizedRenderer(PListTreeNode o, TableCellRenderer renderer) {
            return super.getCustomizedRenderer(o, renderer);
        }

        @Override
        public void setValue(PListTreeNode pListTreeNode, String value) {
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
        static String[] typeValues;
        static {
            ArrayList<String> typeValuesList = new ArrayList<String>(PListDataType.values().length);
            for (PListDataType pListDataType : PListDataType.values()) {
                typeValuesList.add(pListDataType.name());
            }
            typeValues = typeValuesList.toArray(new String[typeValuesList.size()]);
        }

        public PListTypeColumnInfo(String name, Project project, PListTreeTableModel model) {
            super(name, project, model);
        }

        @Override
        public boolean isCellEditable(PListTreeNode pListTreeNode) {
            return true;
        }

        @Override
        public String valueOf(PListTreeNode pListTreeNode) {
            return pListTreeNode.getTypeName();
        }

        @Override public int getWidth(JTable table) { return 100; }

        @Override
        public TableCellEditor getEditor(PListTreeNode pListTreeNode) {
            return new ComboBoxTableRenderer<String>(typeValues);
        }

        @Override
        public TableCellRenderer getRenderer(PListTreeNode pListTreeNode) {
            return super.getRenderer(pListTreeNode);
        }

        @Override
        public TableCellRenderer getCustomizedRenderer(PListTreeNode o, TableCellRenderer renderer) {
            return super.getCustomizedRenderer(o, renderer);
        }

        @Override
        public void setValue(PListTreeNode pListTreeNode, String value) {
            System.out.println("Value set on " + pListTreeNode + " = " + value);
        }
    }

    public static class PlistValueColumnInfo extends PListColumnInfo {
        public PlistValueColumnInfo(final String name, Project project, PListTreeTableModel model) {
            super(name, project, model);
        }

        @Override
        public String valueOf(PListTreeNode pListTreeNode) {
            return pListTreeNode.getAsString();
        }

        @Override
        public boolean isCellEditable(PListTreeNode pListTreeNode) {
            return pListTreeNode.isLeaf();
        }

        @Override
        public TableCellEditor getEditor(PListTreeNode pListTreeNode) {
            return new StringTableCellEditor(project);
        }

        @Override
        public void setValue(PListTreeNode pListTreeNode, String value) {
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
