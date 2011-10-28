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

import java.util.Date;

/**
 * @author Maarten Hazewinkel
 */
public enum PListDataType {
    Array,
    Boolean,
    Data,
    Date,
    Dictionary,
    Integer,
    Real,
    String;

    public PList createDataTypeInstance() {
        switch (this) {
            case Array:
                return new PListArray();
            case Boolean:
                return new PListBoolean(false);
            case Data:
                return new PListData(new byte[0]);
            case Date:
                return new PListDate(new Date());
            case Dictionary:
                return new PListDictionary();
            case Integer:
                return new PListInteger(0);
            case Real:
                return new PListFloat(0);
            case String:
                return new PListString("");
        }
        throw new IllegalArgumentException("Unknown enum value " + this);
    }
}
