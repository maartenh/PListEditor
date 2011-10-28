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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Maarten Hazewinkel
 */
public class PListDictionary extends PListAggregate {

    private ArrayList<PListEntry> dictionary = new ArrayList<PListEntry>();

    @Override
    public void append(PListEntry item) {
        dictionary.add(item);
    }

    @Override
    public List<PListEntry> elements() {
        return Collections.unmodifiableList(dictionary);
    }

    @Override
    public void replaceEntry(PList olddata, PList newdata) {
        ListIterator<PListEntry> iterator = dictionary.listIterator();
        while (iterator.hasNext()) {
            PListEntry oldEntry = iterator.next();
            if (oldEntry.getValue() == olddata) {
                iterator.set(new PListEntry(oldEntry.getKey(), newdata));
                return;
            }
        }
    }

    @Override
    public void deleteEntry(PList olddata) {
        ListIterator<PListEntry> iterator = dictionary.listIterator();
        while (iterator.hasNext()) {
            PListEntry oldEntry = iterator.next();
            if (oldEntry.getValue() == olddata) {
                iterator.remove();
                return;
            }
        }
    }

    @Override
    public void insertFirstChild(PList newValue) {
        dictionary.add(0, new PListEntry(generateNewName(), newValue));
    }

    @Override
    public void insertAfterChild(PList newValue, PList child) {
        ListIterator<PListEntry> iterator = dictionary.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next().getValue() == child) {
                dictionary.add(iterator.nextIndex(), new PListEntry(generateNewName(), newValue));
                return;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PListDictionary that = (PListDictionary) o;

        return !(dictionary != null ? !dictionary.equals(that.dictionary) : that.dictionary != null);

    }

    @Override
    public int hashCode() {
        return dictionary != null ? dictionary.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PListDictionary " + dictionary;
    }

    @Override
    public PListDataType getType() {
        return PListDataType.Dictionary;
    }

    public boolean renameEntry(String name, String value) {
        if (name.equals(value)) {
            return false;
        }
        
        if (hasEntryWithName(value)) {
            return false;
        }
        
        ListIterator<PListEntry> iterator = dictionary.listIterator();
        while (iterator.hasNext()) {
            PListEntry oldEntry = iterator.next();
            if (oldEntry.getKey().equals(name)) {
                iterator.set(new PListEntry(value, oldEntry.getValue()));
                return true;
            }
        }
        return false;
    }

    private boolean hasEntryWithName(String name) {
        for (PListEntry entry : dictionary) {
            if (entry.getKey().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    private String generateNewName() {
        int index = 1;
        String name = "New Item";

        while(hasEntryWithName(name)) {
            index += 1;
            name = "New Item " + index;
        }

        return name;
    }
}
