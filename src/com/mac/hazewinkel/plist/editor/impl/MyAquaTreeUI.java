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

import javax.swing.tree.TreePath;
import java.awt.*;

/**
 * Override to ensure disclosure triangle is always drawn, even
 * when a node does not (yet) have any children.
 *
 * @author Maarten Hazewinkel
 */
public class MyAquaTreeUI extends AquaTreeUI {
    @Override
    /**
     * Override hasBeenExpanded value to ensure disclosure triangle is always drawn, even
     * when a node does not (yet) have any children.
     */
    protected void paintExpandControl(Graphics g,
				      Rectangle clipBounds, Insets insets,
				      Rectangle bounds, TreePath path,
				      int row, boolean isExpanded,
				      boolean hasBeenExpanded,
				      boolean isLeaf) {
        super.paintExpandControl(g, clipBounds, insets, bounds, path, row, isExpanded, false, isLeaf);
    }
}
