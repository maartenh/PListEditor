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
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;

/**
 * @author Maarten Hazewinkel
 */
public class PListXmlHandler extends DefaultHandler {

    private PList root;
    private LinkedList<char[]> charBuffer = new LinkedList<char[]>();
    private LinkedList<PListAggregate> aggregateStack = new LinkedList<PListAggregate>();
    private LinkedList<String> aggregateDictionaryNameStack = new LinkedList<String>();
    private String dictionaryKey;

    public PList getPList() {
        return root;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        charBuffer.clear();

        if ("array".equals(qName) || "dict".equals(qName)) {
            if (dictionaryKey != null) {
                aggregateDictionaryNameStack.push(dictionaryKey);
                dictionaryKey = null;
            }
            if ("array".equals(qName)) {
                aggregateStack.push(new PListArray());
            } else {
                aggregateStack.push(new PListDictionary());
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("true".equals(qName)) {
            push(new PListBoolean(true));
        } else if ("false".equals(qName)) {
            push(new PListBoolean(false));
        } else if ("data".equals(qName)) {
            push(new PListData(parseData(getBufferedText())));
        } else if ("date".equals(qName)) {
            push(new PListDate(parseDate(getBufferedText())));
        } else if ("integer".equals(qName)) {
            push(new PListInteger(parseInteger(getBufferedText())));
        } else if ("real".equals(qName)) {
            push(new PListFloat(parseFloat(getBufferedText())));
        } else if ("string".equals(qName)) {
            push(new PListString(getBufferedText()));
        } else if ("array".equals(qName) || "dict".equals(qName)) {
            PListAggregate aggregate = aggregateStack.pop();
            if (!aggregateStack.isEmpty() && aggregateStack.peek() instanceof PListDictionary) {
                dictionaryKey = aggregateDictionaryNameStack.pop();
            }
            push(aggregate);
        } else if ("key".equals(qName)) {
            setDictionaryKey(getBufferedText());
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (length > 0) {
            char[] chunk = new char[length];
            System.arraycopy(ch, start, chunk, 0, length);
            charBuffer.addLast(chunk);
        }
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
        if ("http://www.apple.com/DTDs/PropertyList-1.0.dtd".equals(systemId)) {
            return new InputSource(new StringReader(PROPERTY_LIST_DTD_10));
        }
        return super.resolveEntity(publicId, systemId);
    }

    private void push(PList item) {
        if (aggregateStack.isEmpty() && root == null) {
            root = item;
        } else {
            if (dictionaryKey != null) {
                PListEntry dictionaryEntry = new PListEntry(dictionaryKey, item);
                dictionaryKey = null;
                aggregateStack.peek().append(dictionaryEntry);
            } else {
                aggregateStack.peek().append(new PListEntry(null, item));
            }
        }
    }

    private void setDictionaryKey(String key) {
        dictionaryKey = key;
    }

    private String getBufferedText() {
        StringBuilder buffer = new StringBuilder();
        for (char[] chunk : charBuffer) {
            buffer.append(chunk);
        }
        charBuffer.clear();
        return buffer.toString();
    }

    private int parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return 0;
        }
    }

    private double parseFloat(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return 0;
        }
    }

    private byte[] parseData(String value) {
        try {
            return Base64.decodeBase64(value.getBytes("ISO8859-1"));
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new byte[0];
        }
    }

    private DateFormat parser;

    private Date parseDate(String value) {
        if (parser == null) {
            parser = PListConversionUtil.getStoredDateFormatter();
        }

        try {
            return parser.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new Date(0);
        }
    }

    private static final String PROPERTY_LIST_DTD_10 =
            "<!ENTITY % plistObject \"(array | data | date | dict | real | integer | string | true | false )\" >\n" +
            "<!ELEMENT plist %plistObject;>\n" +
            "<!ATTLIST plist version CDATA \"1.0\" >\n" +
            "\n" +
            "<!-- Collections -->\n" +
            "<!ELEMENT array (%plistObject;)*>\n" +
            "<!ELEMENT dict (key, %plistObject;)*>\n" +
            "<!ELEMENT key (#PCDATA)>\n" +
            "\n" +
            "<!--- Primitive types -->\n" +
            "<!ELEMENT string (#PCDATA)>\n" +
            "<!ELEMENT data (#PCDATA)> <!-- Contents interpreted as Base-64 encoded -->\n" +
            "<!ELEMENT date (#PCDATA)> <!-- Contents should conform to a subset of ISO 8601 (in particular, YYYY '-' MM '-' DD 'T' HH ':' MM ':' SS 'Z'.  Smaller units may be omitted with a loss of precision) -->\n" +
            "\n" +
            "<!-- Numerical primitives -->\n" +
            "<!ELEMENT true EMPTY>  <!-- Boolean constant true -->\n" +
            "<!ELEMENT false EMPTY> <!-- Boolean constant false -->\n" +
            "<!ELEMENT real (#PCDATA)> <!-- Contents should represent a floating point number matching (\"+\" | \"-\")? d+ (\".\"d*)? (\"E\" (\"+\" | \"-\") d+)? where d is a digit 0-9.  -->\n" +
            "<!ELEMENT integer (#PCDATA)> <!-- Contents should represent a (possibly signed) integer number in base 10 -->";
}
