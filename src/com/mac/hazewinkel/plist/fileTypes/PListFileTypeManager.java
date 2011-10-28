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

package com.mac.hazewinkel.plist.fileTypes;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author Maarten Hazewinkel
 */
public abstract class PListFileTypeManager extends FileTypeFactory {
    public static PListFileTypeManager getInstance() {
        Application application = ApplicationManager.getApplication();
        return application.getComponent(PListFileTypeManager.class);
    }

    /**
     * Check that file is plist.
     *
     * @param file File to check
     * @return Return <code>true</code> if plist file is file with PList file type
     */
    public abstract boolean isPList(VirtualFile file);

    public abstract FileType getPListFileType();
}