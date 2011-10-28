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

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.util.Arrays;

/**
 * @author Maarten Hazewinkel
 */
public class PListData extends PListPrimitive {
    private byte[] value;

    public PListData(byte[] value) {
        this.value = value;
    }

    @Override
    public byte[] getValue() {
        return value;
    }
    
    @Override
    public String getAsString() {
        char[] chars = Hex.encodeHex(value);
        return new String(chars);
    }

    @Override
    public void setAsString(String newValue) throws IllegalArgumentException {
        if (newValue == null) {
            throw new IllegalArgumentException("value cannot be Null");
        }
        try {
            value = Hex.decodeHex(newValue.toCharArray());
        } catch (DecoderException e) {
            throw new IllegalArgumentException("Cannot parse data: " + e.getMessage());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof PListData))
            return false;
        return Arrays.equals(getValue(), ((PListData) obj).getValue());
    }

    @Override
    public PListDataType getType() {
        return PListDataType.Data;
    }
}
