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
public abstract class PListPrimitive extends PList {
    protected abstract Object getValue();

    public abstract void setAsString(String newValue) throws IllegalArgumentException;

    @Override
    public String toString() {
        return getAsString();
    }

    @Override
    public boolean equals(Object obj) {
        if (! getClass().equals(obj.getClass()))
            return false;

        return getValue().equals(((PListPrimitive) obj).getValue());
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }
}
