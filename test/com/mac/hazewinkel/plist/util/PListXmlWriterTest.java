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
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Maarten Hazewinkel
 */
public class PListXmlWriterTest {
    private static final String writeXmlExpectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
            "<plist version=\"1.0\">\n" +
            "<dict>\n" +
            "	<key>Available Updates</key>\n" +
            "	<dict>\n" +
            "		<key>Last Update Check Time</key>\n" +
            "		<real>317035088.45302403</real>\n" +
            "		<key>Updates List</key>\n" +
            "		<array>\n" +
            "			<dict>\n" +
            "				<key>Displayed Version Number</key>\n" +
            "				<string>2.2.1</string>\n" +
            "				<key>Identifier</key>\n" +
            "				<string>com.verticalforest.youtube5-B7HHQRRC44</string>\n" +
            "				<key>Update URL</key>\n" +
            "				<string>http://www.verticalforest.com/youtube5/YouTube5.safariextz</string>\n" +
            "				<key>Version Number</key>\n" +
            "				<string>14</string>\n" +
            "			</dict>\n" +
            "		</array>\n" +
            "	</dict>\n" +
            "	<key>Installed Extensions</key>\n" +
            "	<array>\n" +
            "		<dict>\n" +
            "			<key>Archive File Name</key>\n" +
            "			<string>JavaScript Blacklist-1.safariextz</string>\n" +
            "			<key>Bundle Directory Name</key>\n" +
            "			<string>JavaScript Blacklist-1.safariextension</string>\n" +
            "			<key>Enabled</key>\n" +
            "			<true/>\n" +
            "		</dict>\n" +
            "		<dict>\n" +
            "			<key>Archive File Name</key>\n" +
            "			<string>YouTube5.safariextz</string>\n" +
            "			<key>Bundle Directory Name</key>\n" +
            "			<string>YouTube5.safariextension</string>\n" +
            "			<key>Enabled</key>\n" +
            "			<true/>\n" +
            "		</dict>\n" +
            "	</array>\n" +
            "	<key>Version</key>\n" +
            "	<integer>1</integer>\n" +
            "</dict>\n" +
            "</plist>\n";

    @Test
    public void testWrite() throws Exception {
        PListDictionary plist = new PListDictionary();

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
        plist.append(new PListEntry("Available Updates", dict));

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
        plist.append(new PListEntry("Installed Extensions", array));

        plist.append(new PListEntry("Version", new PListInteger(1)));

        byte[] xmlData = PListConversionUtil.exportPListToXml(new PListRoot(plist));
        byte[] xml1Data = PListConversionUtil.convertPlistToFormat(xmlData, PListFormat.FORMAT_XML1);

        assertEquals(writeXmlExpectedResult, new String(xml1Data, "UTF-8"));
    }
}
