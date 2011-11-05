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

import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.mac.hazewinkel.plist.fileTypes.PListFileTypeManager;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author Maarten Hazewinkel
 */
public class PListEditorProvider implements FileEditorProvider, DumbAware {
    @NonNls
    private static final String EDITOR_TYPE_ID = "pList";
    private PListFileTypeManager typeManager;

    PListEditorProvider(PListFileTypeManager typeManager) {
        this.typeManager = typeManager;
    }

    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        return typeManager.isPList(file);
    }

    @NotNull
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        return new PListFileEditorImpl(project, file);
    }

    public void disposeEditor(@NotNull FileEditor editor) {
        Disposer.dispose(editor);
    }

    @NotNull
    public FileEditorState readState(@NotNull Element sourceElement, @NotNull Project project, @NotNull VirtualFile file) {
        return new PListFileEditorState();
    }

    public void writeState(@NotNull FileEditorState state, @NotNull Project project, @NotNull Element targetElement) {
    }

    @NotNull
    public String getEditorTypeId() {
        return EDITOR_TYPE_ID;
    }

    @NotNull
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
    }

}
