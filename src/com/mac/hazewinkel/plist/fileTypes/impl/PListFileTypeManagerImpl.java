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

package com.mac.hazewinkel.plist.fileTypes.impl;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.mac.hazewinkel.plist.PListBundle;
import com.mac.hazewinkel.plist.fileTypes.PListFileTypeManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author Maarten Hazewinkel
 */
public class PListFileTypeManagerImpl extends PListFileTypeManager implements ApplicationComponent {
    @NonNls private static final String NAME = "PListFileTypeManager";
    @NonNls private static final String PLIST_FILE_TYPE_EXTENSIONS = PListBundle.message("plist.filetype.extensions");

    public static final FileType plistFileType = new PListFileType();

    public PListFileTypeManagerImpl() {
    }

    public boolean isPList(VirtualFile file) {
      FileTypeManager fileTypeManager = FileTypeManager.getInstance();
      FileType fileTypeByFile = fileTypeManager.getFileTypeByFile(file);
      return fileTypeByFile instanceof PListFileType;
    }

    public FileType getPListFileType() {
      return plistFileType;
    }

    @NotNull
    public String getComponentName() {
      return NAME;
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }

    public static final class PListFileType implements FileType {
        @NonNls public static final String DEFAULT_EXTENSION = "plist";
        private static final Icon ICON = IconLoader.getIcon("/com/mac/hazewinkel/plist/plist-icon.png");
        @NonNls private static final String PLIST_FILE_TYPE_NAME = "pList";
        @NonNls private static final String PLIST_FILE_TYPE_DESCRIPTION = PListBundle.message("plist.filetype.description");

        private PListFileType() {
        }

        @NotNull
        public String getName() {
            return PLIST_FILE_TYPE_NAME;
        }

        @NotNull
        public String getDescription() {
            return PLIST_FILE_TYPE_DESCRIPTION;
        }

        @NotNull
        public String getDefaultExtension() {
            return DEFAULT_EXTENSION;
        }

        public Icon getIcon() {
            return ICON;
        }

        public boolean isBinary() {
            return false;
        }

        public boolean isReadOnly() {
            return false;
        }

        public String getCharset(@NotNull VirtualFile file, byte[] content) {
            return null;
        }
    }

    public void createFileTypes(final @NotNull FileTypeConsumer consumer) {
      consumer.consume(plistFileType, PLIST_FILE_TYPE_EXTENSIONS);
    }
}
