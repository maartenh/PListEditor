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

import com.intellij.openapi.diagnostic.Log;
import com.mac.hazewinkel.plist.datamodel.PListRoot;
import org.jetbrains.annotations.NonNls;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Maarten Hazewinkel
 */
public class PListConversionUtil implements Cloneable {
    @NonNls
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    @NonNls
    private static final String XML1_PREFIX = XML_HEADER + "\n" +
            "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
            "<plist version=\"1.0\">\n";

    @NonNls
    private static final String BINARY1_PREFIX = "bplist00";

    @NonNls
    private static final String JSON_PREFIX_1 = "{\"";
    @NonNls
    private static final String JSON_PREFIX_2 = "[";

    private PListConversionUtil() {}

    public static PListFormat determinePListFormat(byte[] fileContent) {
        String fileText;
        try {
            fileText = new String(fileContent, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // ignore. UTF-8 support is required.
            return PListFormat.FORMAT_OTHER;
        }
        if (fileText.startsWith(XML_HEADER))
            return PListFormat.FORMAT_XML1;
        else if (fileText.startsWith(BINARY1_PREFIX))
            return PListFormat.FORMAT_BINARY1;
        else if (fileText.startsWith(JSON_PREFIX_1) || fileText.startsWith(JSON_PREFIX_2))
            return PListFormat.FORMAT_JSON;
        else
            return PListFormat.FORMAT_OTHER;
    }

    public static byte[] convertPlistToFormat(byte[] fileContent, PListFormat format) {
        return convertPlistToFormat(fileContent, format.getExternalFormatName());
    }

    static byte[] convertPlistToFormat(byte[] fileContent, String format) {
        try {
            Process converter = Runtime.getRuntime().exec("plutil -convert " + format + " -o - -- -");
            OutputStream outputStream = converter.getOutputStream();
            outputStream.write(fileContent);
            outputStream.flush();
            outputStream.close();
            InputStream inputStream = converter.getInputStream();

            boolean finished = false;
            int exitcode = -1;
            do {
                try {
                    exitcode = converter.waitFor();
                    finished = true;
                } catch (InterruptedException e) {
                    // ignore, simply retry the wait
                }
            } while (!finished);

            int readBytes;
            int totalBytes = 0;
            byte[] buffer = new byte[1024];
            List<byte[]> chunks = new LinkedList<byte[]>();
            do {
                readBytes = inputStream.read(buffer);
                if (readBytes > 0) {
                    totalBytes += readBytes;
                    byte[] chunk = new byte[readBytes];
                    System.arraycopy(buffer, 0, chunk, 0, readBytes);
                    chunks.add(chunk);
                }
            } while (readBytes >= 0);
            inputStream.close();

            byte[] convertedContent = new byte[totalBytes];
            int copiedPosition = 0;
            for (byte[] chunk : chunks) {
                System.arraycopy(chunk, 0, convertedContent, copiedPosition, chunk.length);
                copiedPosition += chunk.length;
            }
            
            if (exitcode != 0) {
                Log.print("PListConversionUtil failed to convert data. Error: " + new String(convertedContent));
                return new byte[0];
            }
            return convertedContent;
        } catch (IOException e) {
            Log.print("PListConversionUtil failed to convert data. Exception: " + e.toString(), true);
            return new byte[0];
        }
    }

    public static PListRoot parseToPList(byte[] xmlContent) {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        parserFactory.setValidating(false);
        SAXParser parser;
        try {
            parser = parserFactory.newSAXParser();
            PListXmlHandler xmlHandler = new PListXmlHandler();
            parser.parse(new ByteArrayInputStream(xmlContent), xmlHandler);

            return new PListRoot(xmlHandler.getPList());
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot parse xml: " + e.getMessage(), e);
        }
    }

    public static byte[] exportPListToXml(PListRoot plist) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(XML1_PREFIX);
        new PListXmlWriter().write(plist.getRootValue(), buffer);
        buffer.append("</plist>\n");

        try {
            return buffer.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Should never happen. UTF-8 is a required encoding in the JRE
            return new byte[0];
        }
    }

    public static DateFormat getStoredDateFormatter() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        formatter.setTimeZone(TimeZone.getTimeZone("Zulu"));
        return formatter;
    }
}