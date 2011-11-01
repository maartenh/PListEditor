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

import java.util.List;

/**
 * @author Maarten Hazewinkel
 */
public abstract class PListAggregate extends PList {
    public abstract void append(PListEntry item);
    public abstract List<PListEntry> elements();

    @Override
    public String getAsString() {
        int count = elements().size();
        String multipleExtension = (count == 1) ? "" : "s";
        return "(" + count + " item" + multipleExtension + ")";
    }

    public abstract void replaceEntry(PList oldData, PList newData);

    public abstract void deleteEntry(PList oldData);

    public abstract void insertFirstChild(PList newValue);

    public abstract void insertAfterChild(PList newValue, PList child);
}
