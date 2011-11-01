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

import java.util.*;

/**
 * @author Maarten Hazewinkel
 */
public class PListArray extends PListAggregate {

    private ArrayList<PList> array = new ArrayList<PList>();

    @Override
    public void append(PListEntry item) {
        array.add(item.getValue());
    }

    @Override
    public List<PListEntry> elements() {
        ArrayList<PListEntry> elements = new ArrayList<PListEntry>();
        int index = 0;
        for (PList element : array) {
            elements.add(new PListEntry("Item " + index, element));
            index += 1;
        }
        return Collections.unmodifiableList(elements);
    }

    @Override
    public void replaceEntry(PList oldData, PList newData) {
        ListIterator<PList> iterator = array.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next() == oldData) {
                iterator.set(newData);
                return;
            }
        }
    }

    @Override
    public void deleteEntry(PList oldData) {
        ListIterator<PList> iterator = array.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next() == oldData) {
                iterator.remove();
                return;
            }
        }
    }

    @Override
    public void insertFirstChild(PList newValue) {
        array.add(0, newValue);
    }

    @Override
    public void insertAfterChild(PList newValue, PList child) {
        ListIterator<PList> iterator = array.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next() == child) {
                array.add(iterator.nextIndex(), newValue);
                return;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PListArray that = (PListArray) o;

        if (array != null ? !array.equals(that.array) : that.array != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return array != null ? array.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PListArray " + array;
    }

    @Override
    public PListDataType getType() {
        return PListDataType.Array;
    }
}
