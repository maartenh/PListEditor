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

package com.mac.hazewinkel.plist.editor.impl;

import com.mac.hazewinkel.plist.datamodel.*;
import com.mac.hazewinkel.plist.util.PListConversionUtil;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author Maarten Hazewinkel
 */
public class PListXmlHandlerTest {

    private static final String parseStringSource = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
            "<plist version=\"1.0\">\n" +
            "<string>2.2.1</string>\n" +
            "</plist>";

    private static final String parseRealSource = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
            "<plist version=\"1.0\">\n" +
            "<real>317035088.45302403</real>\n" +
            "</plist>";

    private static final String parseIntegerSource = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
            "<plist version=\"1.0\">\n" +
            "<integer>14</integer>\n" +
            "</plist>";

    private static final String parseDateSource = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
            "<plist version=\"1.0\">\n" +
            "<date>2011-04-12T05:21:33Z</date>\n" +
            "</plist>";

    private static final String parseBooleanSource = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
            "<plist version=\"1.0\">\n" +
            "<true/>\n" +
            "</plist>";

    private static final String parseDataSource = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
            "<plist version=\"1.0\">\n" +
            "<data>\n" +
            "PEKBpYGlmYFCPA==\n" +
            "</data>\n" +
            "</plist>";

    private static final String parseLongDataSource = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
            "<plist version=\"1.0\">\n" +
            "        <data>\n" +
            "        YnBsaXN0MDDUAQIDBAUGGhtYJHZlcnNpb25YJG9iamVjdHNZJGFyY2hpdmVyVCR0b3AS\n" +
            "        AAGGoKUHCBESE1UkbnVsbNMJCgsMDQ9WJGNsYXNzV05TLmtleXNaTlMub2JqZWN0c4AE\n" +
            "        oQ6AAqEQgANfEBlBQkluc3RhbnRNZXNzZW5nZXJTZXJ2aWNlXUphYmJlckluc3RhbnTS\n" +
            "        FBUWF1okY2xhc3NuYW1lWCRjbGFzc2VzXxATTlNNdXRhYmxlRGljdGlvbmFyeaMWGBlc\n" +
            "        TlNEaWN0aW9uYXJ5WE5TT2JqZWN0XxAPTlNLZXllZEFyY2hpdmVy0RwdVHJvb3SAAQgR\n" +
            "        GiMtMjc9Q0pRWWRmaGpsboqYnaixx8vY4fP2+wAAAAAAAAEBAAAAAAAAAB4AAAAAAAAA\n" +
            "        AAAAAAAAAAD9\n" +
            "        </data>\n" +
            "</plist>";

    private static final String parseFilledArraySource = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
            "<plist version=\"1.0\">\n" +
            "<array>\n" +
            "        <integer>14</integer>\n" +
            "        <integer>21</integer>\n" +
            "        <true/>\n" +
            "</array>\n" +
            "</plist>";

    private static final String parseEmptyArraySource = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
            "<plist version=\"1.0\">\n" +
            "<array/>\n" +
            "</plist>";

    private static final String parseFilledDictSource = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
            "<plist version=\"1.0\">\n" +
            "<dict>\n" +
            "        <key>value1</key>\n" +
            "        <integer>14</integer>\n" +
            "        <key>value2</key>\n" +
            "        <integer>21</integer>\n" +
            "        <key>value3</key>\n" +
            "        <true/>\n" +
            "</dict>\n" +
            "</plist>";

    private static final String parseEmptyDictSource = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
            "<plist version=\"1.0\">\n" +
            "<dict/>\n" +
            "</plist>";

    private static final String parse1source = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
            "<plist version=\"1.0\">\n" +
            "<dict>\n" +
            "        <key>Available Updates</key>\n" +
            "        <dict>\n" +
            "                <key>Last Update Check Time</key>\n" +
            "                <real>317035088.45302403</real>\n" +
            "                <key>Updates List</key>\n" +
            "                <array>\n" +
            "                        <dict>\n" +
            "                                <key>Displayed Version Number</key>\n" +
            "                                <string>2.2.1</string>\n" +
            "                                <key>Identifier</key>\n" +
            "                                <string>com.verticalforest.youtube5-B7HHQRRC44</string>\n" +
            "                                <key>Update URL</key>\n" +
            "                                <string>http://www.verticalforest.com/youtube5/YouTube5.safariextz</string>\n" +
            "                                <key>Version Number</key>\n" +
            "                                <string>14</string>\n" +
            "                        </dict>\n" +
            "                </array>\n" +
            "        </dict>\n" +
            "        <key>Installed Extensions</key>\n" +
            "        <array>\n" +
            "                <dict>\n" +
            "                        <key>Archive File Name</key>\n" +
            "                        <string>JavaScript Blacklist-1.safariextz</string>\n" +
            "                        <key>Bundle Directory Name</key>\n" +
            "                        <string>JavaScript Blacklist-1.safariextension</string>\n" +
            "                        <key>Enabled</key>\n" +
            "                        <true/>\n" +
            "                </dict>\n" +
            "                <dict>\n" +
            "                        <key>Archive File Name</key>\n" +
            "                        <string>YouTube5.safariextz</string>\n" +
            "                        <key>Bundle Directory Name</key>\n" +
            "                        <string>YouTube5.safariextension</string>\n" +
            "                        <key>Enabled</key>\n" +
            "                        <true/>\n" +
            "                </dict>\n" +
            "        </array>\n" +
            "        <key>Version</key>\n" +
            "        <integer>1</integer>\n" +
            "</dict>\n" +
            "</plist>";

    private byte[] longDataValue = new byte[]{
            (byte) 0x62, (byte) 0x70, (byte) 0x6C, (byte) 0x69, (byte) 0x73, (byte) 0x74, (byte) 0x30, (byte) 0x30,
            (byte) 0xD4, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x1A,
            (byte) 0x1B, (byte) 0x58, (byte) 0x24, (byte) 0x76, (byte) 0x65, (byte) 0x72, (byte) 0x73, (byte) 0x69,
            (byte) 0x6F, (byte) 0x6E, (byte) 0x58, (byte) 0x24, (byte) 0x6F, (byte) 0x62, (byte) 0x6A, (byte) 0x65,
            (byte) 0x63, (byte) 0x74, (byte) 0x73, (byte) 0x59, (byte) 0x24, (byte) 0x61, (byte) 0x72, (byte) 0x63,
            (byte) 0x68, (byte) 0x69, (byte) 0x76, (byte) 0x65, (byte) 0x72, (byte) 0x54, (byte) 0x24, (byte) 0x74,
            (byte) 0x6F, (byte) 0x70, (byte) 0x12, (byte) 0x00, (byte) 0x01, (byte) 0x86, (byte) 0xA0, (byte) 0xA5,
            (byte) 0x07, (byte) 0x08, (byte) 0x11, (byte) 0x12, (byte) 0x13, (byte) 0x55, (byte) 0x24, (byte) 0x6E,
            (byte) 0x75, (byte) 0x6C, (byte) 0x6C, (byte) 0xD3, (byte) 0x09, (byte) 0x0A, (byte) 0x0B, (byte) 0x0C,
            (byte) 0x0D, (byte) 0x0F, (byte) 0x56, (byte) 0x24, (byte) 0x63, (byte) 0x6C, (byte) 0x61, (byte) 0x73,
            (byte) 0x73, (byte) 0x57, (byte) 0x4E, (byte) 0x53, (byte) 0x2E, (byte) 0x6B, (byte) 0x65, (byte) 0x79,
            (byte) 0x73, (byte) 0x5A, (byte) 0x4E, (byte) 0x53, (byte) 0x2E, (byte) 0x6F, (byte) 0x62, (byte) 0x6A,
            (byte) 0x65, (byte) 0x63, (byte) 0x74, (byte) 0x73, (byte) 0x80, (byte) 0x04, (byte) 0xA1, (byte) 0x0E,
            (byte) 0x80, (byte) 0x02, (byte) 0xA1, (byte) 0x10, (byte) 0x80, (byte) 0x03, (byte) 0x5F, (byte) 0x10,
            (byte) 0x19, (byte) 0x41, (byte) 0x42, (byte) 0x49, (byte) 0x6E, (byte) 0x73, (byte) 0x74, (byte) 0x61,
            (byte) 0x6E, (byte) 0x74, (byte) 0x4D, (byte) 0x65, (byte) 0x73, (byte) 0x73, (byte) 0x65, (byte) 0x6E,
            (byte) 0x67, (byte) 0x65, (byte) 0x72, (byte) 0x53, (byte) 0x65, (byte) 0x72, (byte) 0x76, (byte) 0x69,
            (byte) 0x63, (byte) 0x65, (byte) 0x5D, (byte) 0x4A, (byte) 0x61, (byte) 0x62, (byte) 0x62, (byte) 0x65,
            (byte) 0x72, (byte) 0x49, (byte) 0x6E, (byte) 0x73, (byte) 0x74, (byte) 0x61, (byte) 0x6E, (byte) 0x74,
            (byte) 0xD2, (byte) 0x14, (byte) 0x15, (byte) 0x16, (byte) 0x17, (byte) 0x5A, (byte) 0x24, (byte) 0x63,
            (byte) 0x6C, (byte) 0x61, (byte) 0x73, (byte) 0x73, (byte) 0x6E, (byte) 0x61, (byte) 0x6D, (byte) 0x65,
            (byte) 0x58, (byte) 0x24, (byte) 0x63, (byte) 0x6C, (byte) 0x61, (byte) 0x73, (byte) 0x73, (byte) 0x65,
            (byte) 0x73, (byte) 0x5F, (byte) 0x10, (byte) 0x13, (byte) 0x4E, (byte) 0x53, (byte) 0x4D, (byte) 0x75,
            (byte) 0x74, (byte) 0x61, (byte) 0x62, (byte) 0x6C, (byte) 0x65, (byte) 0x44, (byte) 0x69, (byte) 0x63,
            (byte) 0x74, (byte) 0x69, (byte) 0x6F, (byte) 0x6E, (byte) 0x61, (byte) 0x72, (byte) 0x79, (byte) 0xA3,
            (byte) 0x16, (byte) 0x18, (byte) 0x19, (byte) 0x5C, (byte) 0x4E, (byte) 0x53, (byte) 0x44, (byte) 0x69,
            (byte) 0x63, (byte) 0x74, (byte) 0x69, (byte) 0x6F, (byte) 0x6E, (byte) 0x61, (byte) 0x72, (byte) 0x79,
            (byte) 0x58, (byte) 0x4E, (byte) 0x53, (byte) 0x4F, (byte) 0x62, (byte) 0x6A, (byte) 0x65, (byte) 0x63,
            (byte) 0x74, (byte) 0x5F, (byte) 0x10, (byte) 0x0F, (byte) 0x4E, (byte) 0x53, (byte) 0x4B, (byte) 0x65,
            (byte) 0x79, (byte) 0x65, (byte) 0x64, (byte) 0x41, (byte) 0x72, (byte) 0x63, (byte) 0x68, (byte) 0x69,
            (byte) 0x76, (byte) 0x65, (byte) 0x72, (byte) 0xD1, (byte) 0x1C, (byte) 0x1D, (byte) 0x54, (byte) 0x72,
            (byte) 0x6F, (byte) 0x6F, (byte) 0x74, (byte) 0x80, (byte) 0x01, (byte) 0x08, (byte) 0x11, (byte) 0x1A,
            (byte) 0x23, (byte) 0x2D, (byte) 0x32, (byte) 0x37, (byte) 0x3D, (byte) 0x43, (byte) 0x4A, (byte) 0x51,
            (byte) 0x59, (byte) 0x64, (byte) 0x66, (byte) 0x68, (byte) 0x6A, (byte) 0x6C, (byte) 0x6E, (byte) 0x8A,
            (byte) 0x98, (byte) 0x9D, (byte) 0xA8, (byte) 0xB1, (byte) 0xC7, (byte) 0xCB, (byte) 0xD8, (byte) 0xE1,
            (byte) 0xF3, (byte) 0xF6, (byte) 0xFB, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x1E, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0xFD};

    @Test
    public void testParseBoolean() throws UnsupportedEncodingException {
        PList plist = PListConversionUtil.parseToPList(parseBooleanSource.getBytes("UTF-8")).getRootValue();
        assertEquals(new PListBoolean(true), plist);
        assertFalse(new PListBoolean(false).equals(plist));
    }

    @Test
    public void testParseData() throws UnsupportedEncodingException {
        PList plist = PListConversionUtil.parseToPList(parseDataSource.getBytes("UTF-8")).getRootValue();
        byte[] expectedBytes = {0x3c, 0x42, (byte) 0x81, (byte) 0xa5, (byte) 0x81, (byte) 0xa5, (byte) 0x99, (byte) 0x81, 0x42, 0x3c};
        assertEquals(new PListData(expectedBytes), plist);
    }

    @Test
    public void testParseLongData() throws UnsupportedEncodingException {
        PList plist = PListConversionUtil.parseToPList(parseLongDataSource.getBytes("UTF-8")).getRootValue();
        assertEquals(new PListData(longDataValue), plist);
    }

    @Test
    public void testParseDate() throws UnsupportedEncodingException {
        PList plist = PListConversionUtil.parseToPList(parseDateSource.getBytes("UTF-8")).getRootValue();
        Date expectedDate = new GregorianCalendar(2011, 3, 12, 7, 21, 33).getTime();
        assertEquals(new PListDate(expectedDate), plist);
    }

    @Test
    public void testParseFloat() throws UnsupportedEncodingException {
        PList plist = PListConversionUtil.parseToPList(parseRealSource.getBytes("UTF-8")).getRootValue();
        assertEquals(new PListFloat(317035088.45302403), plist);
    }

    @Test
    public void testParseInteger() throws UnsupportedEncodingException {
        PList plist = PListConversionUtil.parseToPList(parseIntegerSource.getBytes("UTF-8")).getRootValue();
        assertEquals(new PListInteger(14), plist);
        assertFalse(new PListInteger(13).equals(plist));
        assertFalse(new PListFloat(14).equals(plist));
    }

    @Test
    public void testParseString() throws UnsupportedEncodingException {
        PList plist = PListConversionUtil.parseToPList(parseStringSource.getBytes("UTF-8")).getRootValue();
        assertEquals(new PListString("2.2.1"), plist);
    }

    @Test
    public void testParseEmptyArray() throws UnsupportedEncodingException {
        PList plist = PListConversionUtil.parseToPList(parseEmptyArraySource.getBytes("UTF-8")).getRootValue();
        assertEquals(new PListArray(), plist);
    }

    @Test
    public void testParseFilledArray() throws UnsupportedEncodingException {
        PList plist = PListConversionUtil.parseToPList(parseFilledArraySource.getBytes("UTF-8")).getRootValue();
        PListArray expected = new PListArray();
        expected.append(new PListEntry(null, new PListInteger(14)));
        expected.append(new PListEntry(null, new PListInteger(21)));
        expected.append(new PListEntry(null, new PListBoolean(true)));
        assertEquals(expected, plist);
    }

    @Test
    public void testParseEmptyDict() throws UnsupportedEncodingException {
        PList plist = PListConversionUtil.parseToPList(parseEmptyDictSource.getBytes("UTF-8")).getRootValue();
        assertEquals(new PListDictionary(), plist);
    }

    @Test
    public void testParseFilledDict() throws UnsupportedEncodingException {
        PList plist = PListConversionUtil.parseToPList(parseFilledDictSource.getBytes("UTF-8")).getRootValue();
        PListDictionary expected = new PListDictionary();
        expected.append(new PListEntry("value1", new PListInteger(14)));
        expected.append(new PListEntry("value2", new PListInteger(21)));
        expected.append(new PListEntry("value3", new PListBoolean(true)));
        assertEquals(expected, plist);
    }

    @Test
    public void testParse1() throws UnsupportedEncodingException {
        PList plist = PListConversionUtil.parseToPList(parse1source.getBytes("UTF-8")).getRootValue();
        PListDictionary expected = new PListDictionary();

        PListDictionary dict = new PListDictionary();
        dict.append(new PListEntry("Last Update Check Time", new PListFloat(317035088.45302403)));
        PListArray array = new PListArray();
        PListDictionary dict2 = new PListDictionary();
        dict2.append(new PListEntry("Displayed Version Number", new PListString("2.2.1")));
        dict2.append(new PListEntry("Identifier", new PListString("com.verticalforest.youtube5-B7HHQRRC44")));
        dict2.append(new PListEntry("Update URL", new PListString("http://www.verticalforest.com/youtube5/YouTube5.safariextz")));
        dict2.append(new PListEntry("Version Number", new PListString("14")));
        array.append(new PListEntry(null, dict2));
        dict.append(new PListEntry("Updates List", array));
        expected.append(new PListEntry("Available Updates", dict));

        array = new PListArray();
        dict = new PListDictionary();
        dict.append(new PListEntry("Archive File Name", new PListString("JavaScript Blacklist-1.safariextz")));
        dict.append(new PListEntry("Bundle Directory Name", new PListString("JavaScript Blacklist-1.safariextension")));
        dict.append(new PListEntry("Enabled", new PListBoolean(true)));
        array.append(new PListEntry(null, dict));
        dict = new PListDictionary();
        dict.append(new PListEntry("Archive File Name", new PListString("YouTube5.safariextz")));
        dict.append(new PListEntry("Bundle Directory Name", new PListString("YouTube5.safariextension")));
        dict.append(new PListEntry("Enabled", new PListBoolean(true)));
        array.append(new PListEntry(null, dict));
        expected.append(new PListEntry("Installed Extensions", array));

        expected.append(new PListEntry("Version", new PListInteger(1)));
        assertEquals(expected, plist);
    }
}
