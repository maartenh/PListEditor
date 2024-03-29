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

import com.apple.laf.AquaTreeUI;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBoxTableRenderer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.ColoredTableCellRenderer;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SimpleTextAttributes;
import com.mac.hazewinkel.plist.datamodel.*;
import com.mac.hazewinkel.plist.util.PListConversionUtil;
import com.mac.hazewinkel.plist.util.PListFormat;
import org.jdesktop.swingx.JXTreeTable;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EventObject;

/**
 * @author Maarten Hazewinkel
 */
public class PListEditor {
    public static final Insets ZERO_INSETS = new Insets(0, 2, 0, 0);
    private JScrollPane mainComponent;
    private PListRoot plist = null;

    private MyJXTreeTable plistTree;
    PListJXTreeTableModel treeTableModel;

    static String[] typeValues;
    static String[] aggregateTypeValues;

    static {
        ArrayList<String> typeValuesList = new ArrayList<String>(PListDataType.values().length);
        ArrayList<String> aggregateTypeValuesList = new ArrayList<String>(PListDataType.values().length);
        for (PListDataType pListDataType : PListDataType.values()) {
            typeValuesList.add(pListDataType.name());
            if (pListDataType.createDataTypeInstance() instanceof PListAggregate) {
                aggregateTypeValuesList.add(pListDataType.name());
            }
        }
        typeValues = typeValuesList.toArray(new String[typeValuesList.size()]);
        aggregateTypeValues = aggregateTypeValuesList.toArray(new String[aggregateTypeValuesList.size()]);
    }

    public PListEditor(byte[] bytes, PListFormat storageFormat, Project project) {
        loadPList(bytes, storageFormat);
        treeTableModel = new PListJXTreeTableModel(plist, project);
        plistTree = new MyJXTreeTable(treeTableModel);
        plistTree.setClosedIcon(null);
        plistTree.setOpenIcon(null);
        plistTree.setLeafIcon(null);
        plistTree.setColumnSelectionAllowed(true);
        plistTree.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        plistTree.getColumn(1).setMaxWidth(100);
        plistTree.getColumn(1).setMinWidth(100);
        plistTree.setSurrendersFocusOnKeystroke(true);
        plistTree.getTableHeader().setReorderingAllowed(false);
        plistTree.setSortable(false);

        plistTree.setDefaultRenderer(String[].class, new ComboBoxTableRenderer<String>(typeValues));
        plistTree.setDefaultEditor(String[].class, new StringComboBoxTableRenderer(typeValues));

        plistTree.setDefaultRenderer(String.class, new MyStringTableCellRenderer());
        plistTree.setDefaultEditor(String.class, new MyStringTableCellEditor(project, plistTree));

        mainComponent = ScrollPaneFactory.createScrollPane(plistTree);
        plistTree.setScrollView(mainComponent);
    }

    private void loadPList(byte[] bytes, PListFormat storageFormat) {
        if (bytes.length == 0) {
            plist = new PListRoot(new PListDictionary());
            plist.setStorageFormat(PListFormat.FORMAT_XML1);
            return;
        }
        plist = PListConversionUtil.parseToPList(bytes);
        plist.setStorageFormat(storageFormat);
    }

    public JComponent getComponent() {
        return mainComponent;
    }

    public JComponent getPreferredFocusedComponent() {
        return plistTree;
    }

    public boolean isModified() {
        return treeTableModel.isModified();
    }

    public byte[] getPListXmlBytes() {
        return PListConversionUtil.exportPListToXml(plist);
    }

    public boolean isValidForFormat(PListFormat format) {
        return !PListFormat.FORMAT_JSON.equals(format) || plist.getRootValue() instanceof PListDictionary || plist.getRootValue() instanceof PListArray;
    }

    public void setModified(boolean b) {
        treeTableModel.setModified(b);
    }

    private static class MyJXTreeTable extends JXTreeTable {
        private MyStringTableCellEditor treeColumnEditor;

        boolean plusDown = false;
        boolean minusDown = false;
        int downRow = -1;
        private PListJXTreeTableModel treeTableModel;
        private JScrollPane scrollView;

        public MyJXTreeTable(PListJXTreeTableModel treeTableModel) {
            super(treeTableModel);
            this.treeTableModel = treeTableModel;
            treeColumnEditor = new MyStringTableCellEditor(treeTableModel.getProject(), this);
            
            JTree tree = (JTree) getCellRenderer(0 ,getHierarchicalColumn());
            if (tree.getUI() instanceof AquaTreeUI) {
                tree.setUI(new MyAquaTreeUI());
            }
        }

        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            Component rendererComponent = super.prepareRenderer(renderer, row, column);
            rendererComponent.setFont(UIManager.getFont("TextField.font"));
            return rendererComponent;
        }

        @Override
        public TableCellEditor getCellEditor(int row, int column) {
            if (row == 0 && column == 1
                    && treeTableModel.getPlist().getStorageFormat().equals(PListFormat.FORMAT_JSON)) {
               return new StringComboBoxTableRenderer(aggregateTypeValues);
            }

            TableCellEditor defaultCellEditor = super.getCellEditor(row, column);
            if (isHierarchical(column)) {
                JTree tree = (JTree) getCellRenderer(row ,getHierarchicalColumn());
                treeColumnEditor.setTreeIndent(tree.getRowBounds(row).x);
                return treeColumnEditor;
            }
            return defaultCellEditor;
        }

        {
            addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                }

                public void mousePressed(MouseEvent e) {
                    int x = e.getX();
                    int maxX = getColumn(0).getWidth();
                    int currentMouseRow = e.getY() / getRowHeight();
                    plusDown = x < maxX - 19 && x >= maxX - 35 && currentMouseRow < getRowCount();
                    minusDown = x < maxX - 1 && x >= maxX - 17 && currentMouseRow < getRowCount();
                    downRow = currentMouseRow;
                    if (plusDown || minusDown) {
                        repaint();
                    }
                }

                public void mouseReleased(MouseEvent e) {
                    if (plusDown || minusDown) {
                        int x = e.getX();
                        int maxX = getColumn(0).getWidth();
                        int currentMouseRow = e.getY() / getRowHeight();
                        if (plusDown && x < maxX - 19 && x >= maxX - 35 && currentMouseRow == downRow) {
                            addRow(currentMouseRow);
                        } else if (minusDown && x < maxX - 1 && x >= maxX - 17 && currentMouseRow == downRow) {
                            deleteRow(currentMouseRow);
                        }
                        downRow = -1;
                        plusDown = false;
                        minusDown = false;
                        repaint();
                    }
                }

                public void mouseEntered(MouseEvent e) {
                }

                public void mouseExited(MouseEvent e) {
                    if (e.getClickCount() == 0) {
                        plusDown = false;
                        minusDown = false;
                        repaint();
                    }
                }
            });

        }

        public void addRow(int baseRow) {
            PListJXTreeTableNode node = (PListJXTreeTableNode) MyJXTreeTable.this.getValueAt(baseRow, 0);
            boolean isNodeExpanded = isExpanded(baseRow);
            PListJXTreeTableNode changedNode = node.addRow(isNodeExpanded);
            treeTableModel.setModified(true);
            if (node.getAllowsChildren() && isNodeExpanded) {
                treeTableModel.nodesWereInserted(changedNode, new int[]{0});
            } else {
                treeTableModel.nodesWereInserted(changedNode, new int[]{changedNode.getIndex(node) + 1});
            }
        }

        public void deleteRow(int baseRow) {
            PListJXTreeTableNode node = (PListJXTreeTableNode) MyJXTreeTable.this.getValueAt(baseRow, 0);
            int nodeIndex = node.getParent().getIndex(node);
            if (node.deleteRow()) {
                treeTableModel.setModified(true);
                treeTableModel.nodesWereRemoved(node.getParent(), new int[]{nodeIndex}, new PListJXTreeTableNode[]{node});
            }
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            int firstRow = rowAtPoint(scrollView.getViewport().getViewPosition());
            int lastRow = rowAtPoint(new Point(0, scrollView.getViewport().getHeight() + scrollView.getViewport().getViewPosition().y));
            if (firstRow == -1) {
                firstRow = 0;
            }
            if (lastRow == -1) {
                lastRow = getRowCount() -1;
            }
            for (int currentRow = firstRow; currentRow <= lastRow; currentRow++) {
                int x = getColumn(0).getWidth() - 35;
                int y = currentRow * getRowHeight() + 1;

                // Enable this if using non-alpha button images. Looks ugly otherwise.
                // g.setColor(getBackground());
                // g.fillRect(x - 1, y - 1, 36, getRowHeight());
                
                if (plusDown && downRow == currentRow) {
                    plusPressed.paintIcon(null, g, x, y);
                } else {
                    plusNormal.paintIcon(null, g, x, y);
                }
                x += 18;
                if (minusDown && downRow == currentRow) {
                    minusPressed.paintIcon(null, g, x, y);
                } else {
                    minusNormal.paintIcon(null, g, x, y);
                }
            }
        }
        
        private static final Icon plusNormal = IconLoader.getIcon("/com/mac/hazewinkel/plist/editor/impl/plus-normal-alpha.png");
        private static final Icon plusPressed = IconLoader.getIcon("/com/mac/hazewinkel/plist/editor/impl/plus-pressed-alpha.png");
        private static final Icon minusNormal = IconLoader.getIcon("/com/mac/hazewinkel/plist/editor/impl/minus-normal-alpha.png");
        private static final Icon minusPressed = IconLoader.getIcon("/com/mac/hazewinkel/plist/editor/impl/minus-pressed-alpha.png");

        public void setScrollView(JScrollPane scrollView) {
            this.scrollView = scrollView;
        }
    }

    public static class MyStringTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        private Document myDocument;
        private final Project myProject;
        private MyJXTreeTable treeTable;
        private int treeIndent;

        public MyStringTableCellEditor(final Project project, MyJXTreeTable treeTable) {
            myProject = project;
            this.treeTable = treeTable;
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, final int column) {
            final EditorTextField editorTextField = new EditorTextField(value.toString(), myProject, StdFileTypes.PLAIN_TEXT) {
                @Override
                public Insets getInsets() {
                    if (column != 0) {
                        return ZERO_INSETS;
                    }
                    return new Insets(0, treeIndent - 1, 0, 36);
                }
            };
            myDocument = editorTextField.getDocument();
            return editorTextField;
        }

        public Object getCellEditorValue() {
            return myDocument.getText();
        }

        public void setTreeIndent(int x) {
            this.treeIndent = x;
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            if (e instanceof MouseEvent) {
                MouseEvent me = (MouseEvent) e;
                if (treeTable.columnAtPoint(me.getPoint()) == 0
                        && me.getPoint().x >= treeTable.getColumn(0).getWidth()-36) {
                    return false;
                }
            }
            return super.isCellEditable(e);
        }
    }

    public static class MyStringTableCellRenderer extends ColoredTableCellRenderer {
        public void customizeCellRenderer(JTable table, Object value,
                                          boolean isSelected, boolean hasFocus, int row, int column) {
            if (value == null) return;
            setPaintFocusBorder(false);
            setFont(UIManager.getFont("TextField.font"));
            append((String) value, new SimpleTextAttributes(Font.PLAIN, null));
        }
    }

    private static class StringComboBoxTableRenderer extends ComboBoxTableRenderer<String> {
        public StringComboBoxTableRenderer(String[] typeValues) {
            super(typeValues);
        }

        @Override
        public boolean isCellEditable(EventObject event) {
            if (event instanceof MouseEvent) {
                return ((MouseEvent) event).getClickCount() >= 1;
            }
            return super.isCellEditable(event);
        }
    }
}
