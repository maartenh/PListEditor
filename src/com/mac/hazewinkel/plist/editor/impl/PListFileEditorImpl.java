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

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.FileEditorStateLevel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.mac.hazewinkel.plist.editor.PListFileEditor;
import com.mac.hazewinkel.plist.util.PListConversionUtil;
import com.mac.hazewinkel.plist.util.PListFormat;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Maarten Hazewinkel
 */
public class PListFileEditorImpl extends UserDataHolderBase implements PListFileEditor {
    @NonNls
    private static final String NAME = "pList Editor";

    private VirtualFile file;
    private PListEditor editor;
    private PListFormat storageFormat = PListFormat.FORMAT_XML1;

    private static List<PListFileEditorImpl> openEditors = new LinkedList<PListFileEditorImpl>();

    public PListFileEditorImpl(@NotNull Project project, @NotNull VirtualFile file) {
        this.file = file;
        byte[] fileContent;
        try {
            fileContent = file.contentsToByteArray();
            storageFormat = PListConversionUtil.determinePListFormat(fileContent);

            if (storageFormat != PListFormat.FORMAT_XML1) {
                fileContent = PListConversionUtil.convertPlistToFormat(fileContent, PListFormat.FORMAT_XML1);
            }
        } catch (IOException e) {
            Logger.getInstance(PListFileEditorImpl.class).error("Failed to load file '" + file.getPresentableUrl() + '\'', e);
            fileContent = new byte[0];
        }

        this.editor = new PListEditor(fileContent, storageFormat, project);

        openEditors.add(this);
    }

    @NotNull
    public JComponent getComponent() {
        return editor.getComponent();
    }

    public JComponent getPreferredFocusedComponent() {
        return editor.getPreferredFocusedComponent();
    }

    @NotNull
    public String getName() {
        return NAME;
    }

    @NotNull
    public FileEditorState getState(@NotNull FileEditorStateLevel level) {
        return new PListFileEditorState();
    }

    public void setState(@NotNull FileEditorState state) {
    }

    public boolean isModified() {
        return editor.isModified();
    }

    public boolean isValid() {
        return file.isValid() && editor.isValidForFormat(storageFormat);
    }

    public void selectNotify() {
    }

    public void deselectNotify() {
    }

    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    public BackgroundEditorHighlighter getBackgroundHighlighter() {
        return null;
    }

    public FileEditorLocation getCurrentLocation() {
        return null;
    }

    public StructureViewBuilder getStructureViewBuilder() {
        return null;
    }

    public void dispose() {
        saveDocument();
        openEditors.remove(this);
    }

    public static void saveAllDocuments() {
        for (PListFileEditorImpl openEditor : openEditors) {
            openEditor.saveDocument();
        }
    }

    private void saveDocument() {
        if (isModified() && isValid()) {
            byte[] documentData = PListConversionUtil.convertPlistToFormat(editor.getPListXmlBytes(), storageFormat);
            try {
                file.setBinaryContent(documentData);
                editor.setModified(false);
            } catch (IOException e) {
                Logger.getInstance(PListFileEditorImpl.class).error("Failed to save file '" + file.getPresentableUrl() + '\'', e);
            }
        }
    }
}
