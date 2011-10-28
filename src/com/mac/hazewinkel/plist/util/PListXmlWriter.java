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

package com.mac.hazewinkel.plist.util;

import com.mac.hazewinkel.plist.datamodel.*;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;

/**
 * @author Maarten Hazewinkel
 */
public class PListXmlWriter {
    public void write(PList plist, StringBuilder buffer) {
        if (plist instanceof PListBoolean) {
            if (((PListBoolean) plist).getValue())
                buffer.append("<true/>\n");
            else
                buffer.append("<false/>\n");
        } else if (plist instanceof PListData) {
            byte[] data = ((PListData) plist).getValue();
            buffer.append("<data>\n");
            try {
                buffer.append(new String(Base64.encodeBase64Chunked(data), "ISO8859-1"));
            } catch (UnsupportedEncodingException e) {
                // should never happen. Latin1 is a required part of the JRE
            }
            buffer.append("</data>\n");
        } else if (plist instanceof PListDate) {
            Date date = ((PListDate) plist).getValue();
            buffer.append("<date>").append(getDateFormatter().format(date)).append("</date>\n");
        } else if (plist instanceof PListFloat) {
            double real = ((PListFloat) plist).getValue();
            buffer.append("<real>").append(Double.toString(real)).append("</real>\n");
        } else if (plist instanceof PListInteger) {
            int integer = ((PListInteger) plist).getValue();
            buffer.append("<integer>").append(String.valueOf(integer)).append("</integer>\n");
        } else if (plist instanceof PListString) {
            String string = ((PListString) plist).getValue();
            buffer.append("<string>").append(string).append("</string>\n");
        } else if (plist instanceof PListArray) {
            buffer.append("<array>\n");
            for (PListEntry entry : ((PListArray) plist).elements()) {
                write(entry.getValue(), buffer);
            }
            buffer.append("</array>\n");
        } else if (plist instanceof PListDictionary) {
            buffer.append("<dict>\n");
            for (PListEntry entry : ((PListDictionary) plist).elements()) {
                buffer.append("<key>").append(entry.getKey()).append("</key>\n");
                write(entry.getValue(), buffer);
            }
            buffer.append("</dict>\n");
        }
    }

    private DateFormat dateFormatter;
    private DateFormat getDateFormatter() {
        if (dateFormatter == null) {
            dateFormatter = PListConversionUtil.getStoredDateFormatter();
        }
        return dateFormatter;
    }
}
