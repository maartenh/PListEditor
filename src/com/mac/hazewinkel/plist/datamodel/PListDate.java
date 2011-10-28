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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Maarten Hazewinkel
 */
public class PListDate extends PListPrimitive {
    private Date value;
    private static final DateFormat formatter = getFormatter();

    public PListDate(Date value) {
        this.value = value;
    }

    @Override
    public Date getValue() {
        return value;
    }

    @Override
    public String getAsString() {
        synchronized (formatter) {
            return getFormatter().format(value);
        }
    }

    @Override
    public void setAsString(String newValue) throws IllegalArgumentException {
        if (newValue == null) {
            throw new IllegalArgumentException("value cannot be Null");
        }
        try {
            synchronized (formatter) {
                value = getFormatter().parse(newValue);
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Cannot parse date: " + e.getMessage());
        }
    }

    private static DateFormat getFormatter() {
        return SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM, SimpleDateFormat.MEDIUM);
    }

    @Override
    public PListDataType getType() {
        return PListDataType.Date;
    }
}
