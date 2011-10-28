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

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBoxTableRenderer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.ColoredTableCellRenderer;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SimpleTextAttributes;
import com.mac.hazewinkel.plist.datamodel.PList;
import com.mac.hazewinkel.plist.datamodel.PListArray;
import com.mac.hazewinkel.plist.datamodel.PListDataType;
import com.mac.hazewinkel.plist.datamodel.PListDictionary;
import com.mac.hazewinkel.plist.util.PListConversionUtil;
import com.mac.hazewinkel.plist.util.PListFormat;
import org.jdesktop.swingx.JXTreeTable;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.EventObject;

/**
 * @author Maarten Hazewinkel
 */
public class PListEditor {
    public static final Insets ZERO_INSETS = new Insets(0, 2, 0, 0);
    private JScrollPane mainComponent;
    private PList plist = null;

    private JXTreeTable plistTree;
    PListJXTreeTableModel treeTableModel;

    static String[] typeValues;

    static {
        ArrayList<String> typeValuesList = new ArrayList<String>(PListDataType.values().length);
        for (PListDataType pListDataType : PListDataType.values()) {
            typeValuesList.add(pListDataType.name());
        }
        typeValues = typeValuesList.toArray(new String[typeValuesList.size()]);
    }

    public PListEditor(byte[] bytes, Project project) {
        loadPList(bytes);
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
        plistTree.setDefaultEditor(String[].class, new ComboBoxTableRenderer<String>(typeValues) {
            @Override
            public boolean isCellEditable(EventObject event) {
                if (event instanceof MouseEvent) {
                    return ((MouseEvent) event).getClickCount() >= 1;
                }
                return super.isCellEditable(event);
            }
        });

        plistTree.setDefaultRenderer(String.class, new MyStringTableCellRenderer());
        plistTree.setDefaultEditor(String.class, new MyStringTableCellEditor(project, plistTree));

        mainComponent = ScrollPaneFactory.createScrollPane(plistTree);
    }

    private void loadPList(byte[] bytes) {
        if (bytes.length == 0) {
            plist = new PListDictionary();
            return;
        }
        plist = PListConversionUtil.parseToPList(bytes);
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
        return !PListFormat.FORMAT_JSON.equals(format) || plist instanceof PListDictionary || plist instanceof PListArray;
    }

    public void setModified(boolean b) {
        treeTableModel.setModified(b);
    }

    private static class MyJXTreeTable extends JXTreeTable {
        private MyStringTableCellEditor treeColumnEditor;

        boolean showPlusMinus = false;
        boolean plusDown = false;
        boolean minusDown = false;
        int currentMouseRow = -1;
        int downRow = -1;
        private PListJXTreeTableModel treeTableModel;

        public MyJXTreeTable(PListJXTreeTableModel treeTableModel) {
            super(treeTableModel);
            this.treeTableModel = treeTableModel;
            treeColumnEditor = new MyStringTableCellEditor(treeTableModel.getProject(), this);
        }

        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            Component rendererComponent = super.prepareRenderer(renderer, row, column);
            rendererComponent.setFont(UIManager.getFont("TextField.font"));
            return rendererComponent;
        }

        @Override
        public TableCellEditor getCellEditor(int row, int column) {
            TableCellEditor defaultCellEditor = super.getCellEditor(row, column);
            if (isHierarchical(column)) {
                JTree tree = (JTree) getCellRenderer(row ,getHierarchicalColumn());
                treeColumnEditor.setTreeIndent(tree.getRowBounds(row).x);
                return treeColumnEditor;
            }
            return defaultCellEditor;
        }

        {
            addMouseMotionListener(new MouseMotionListener() {
                public void mouseDragged(MouseEvent e) {}

                public void mouseMoved(MouseEvent e) {
                    //System.out.println(e);
                    int x = e.getX();
                    int maxX = getColumn(0).getWidth();
                    boolean oldShow = showPlusMinus;
                    int oldRow = currentMouseRow;
                    currentMouseRow = e.getY() / getRowHeight();
                    showPlusMinus = x < maxX && x >= maxX - 36 && currentMouseRow < getRowCount();
                    if (oldShow != showPlusMinus || oldRow != currentMouseRow) {
                        repaint();
                    }
                }
            });
            addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                }

                public void mousePressed(MouseEvent e) {
                    int x = e.getX();
                    int maxX = getColumn(0).getWidth();
                    boolean oldShow = showPlusMinus;
                    int oldRow = currentMouseRow;
                    currentMouseRow = e.getY() / getRowHeight();
                    showPlusMinus = x < maxX && x >= maxX - 36 && currentMouseRow < getRowCount();
                    plusDown = x < maxX - 19 && x >= maxX - 35 && currentMouseRow < getRowCount();
                    minusDown = x < maxX - 1 && x >= maxX - 17 && currentMouseRow < getRowCount();
                    downRow = currentMouseRow;
                    if (oldShow != showPlusMinus || oldRow != currentMouseRow || plusDown || minusDown) {
                        repaint();
                    }
                }

                public void mouseReleased(MouseEvent e) {
                    if (plusDown || minusDown) {
                        int x = e.getX();
                        int maxX = getColumn(0).getWidth();
                        currentMouseRow = e.getY() / getRowHeight();
                        showPlusMinus = x < maxX && x >= maxX - 36 && currentMouseRow < getRowCount();
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
                    if (showPlusMinus && e.getClickCount() == 0) {
                        showPlusMinus = false;
                        plusDown = false;
                        minusDown = false;
                        currentMouseRow = -1;
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
            if (showPlusMinus) {
                int x = getColumn(0).getWidth() - 35;
                int y = currentMouseRow * getRowHeight() + 1;

                // Enable this if using non-alpha button images. Looks ugly otherwise.
                // g.setColor(getBackground());
                // g.fillRect(x - 1, y - 1, 36, getRowHeight());
                
                if (plusDown) {
                    plusPressed.paintIcon(null, g, x, y);
                } else {
                    plusNormal.paintIcon(null, g, x, y);
                }
                x += 18;
                if (minusDown) {
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
    }

    public static class MyStringTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        private Document myDocument;
        private final Project myProject;
        private JXTreeTable treeTable;
        private int treeIndent;

        public MyStringTableCellEditor(final Project project, JXTreeTable treeTable) {
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
}
