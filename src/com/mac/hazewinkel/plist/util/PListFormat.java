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

/**
 * @author Maarten Hazewinkel
 */
public enum PListFormat {
    FORMAT_XML1, FORMAT_BINARY1, FORMAT_JSON, FORMAT_OTHER;

    public String getExternalFormatName() {
        switch (this) {
            case FORMAT_XML1:
                return "xml1";
            case FORMAT_BINARY1:
                return "binary1";
            case FORMAT_JSON:
                return "json";
            default:
                return "xml1";
        }
    }
}
