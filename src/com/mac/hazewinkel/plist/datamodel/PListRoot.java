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

package com.mac.hazewinkel.plist.datamodel;

import com.mac.hazewinkel.plist.util.PListFormat;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Maarten Hazewinkel
 */
public class PListRoot extends PListAggregate {

    private PList rootValue;
    private PListFormat storageFormat;

    public PListRoot(PList rootValue) {

        this.rootValue = rootValue;
    }
    
    @Override
    public void append(PListEntry item) {
        throw new UnsupportedOperationException("Cannot set multiple items to root");
    }

    @Override
    public List<PListEntry> elements() {
        List<PListEntry> result = new LinkedList<PListEntry>();
        result.add(new PListEntry("<plist>", rootValue));
        return result;
    }

    @Override
    public void replaceEntry(PList oldData, PList newData) {
        if (oldData == rootValue) {
            rootValue = newData;
        }
    }

    @Override
    public void deleteEntry(PList oldData) {
        throw new UnsupportedOperationException("Cannot delete root value");
    }

    @Override
    public void insertFirstChild(PList newValue) {
        throw new UnsupportedOperationException("Cannot set multiple items to root");
    }

    @Override
    public void insertAfterChild(PList newValue, PList child) {
        throw new UnsupportedOperationException("Cannot set multiple items to root");
    }

    @Override
    public PListDataType getType() {
        return rootValue.getType();
    }

    public PList getRootValue() {
        return rootValue;
    }

    public void setStorageFormat(PListFormat storageFormat) {
        this.storageFormat = storageFormat;
    }

    public PListFormat getStorageFormat() {
        return storageFormat;
    }
}
