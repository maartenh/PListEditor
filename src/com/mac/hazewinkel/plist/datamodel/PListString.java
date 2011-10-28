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

/**
 * @author Maarten Hazewinkel
 */
public class PListString extends PListPrimitive {
    private String value;

    public PListString(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getAsString() {
        return value;
    }

    @Override
    public void setAsString(String newValue) throws IllegalArgumentException {
        if (newValue == null) {
            throw new IllegalArgumentException("value cannot be Null");
        }
        value = newValue;
    }

    @Override
    public PListDataType getType() {
        return PListDataType.String;
    }
}
