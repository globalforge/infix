package com.globalforge.infix.qfix;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*-
 The MIT License (MIT)

 Copyright (c) 2019-2020 Global Forge LLC

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
/**
 * Parses the fields section of a quick fix data dictionary and records the
 * field name and field numeric value in a map.
 * @author Michael C. Starkie
 */
public class FieldParser {
    /** logger */
    protected final XMLInputFactory factory = XMLInputFactory.newInstance();
    protected final String fixFileName;
    final static Logger logger = LoggerFactory.getLogger(FieldParser.class);
    private final Map<String, String> tagNameToNumber = new ConcurrentHashMap<String, String>();
    private final Map<String, String> tagNumberToName = new ConcurrentHashMap<String, String>();
    private final Set<String> elementNames = new HashSet<String>();
    private boolean isFieldsElement = false;

    /**
     * Requires a name of a fix file
     * @param f the filename
     * @throws Exception
     */
    public FieldParser(String f) throws Exception {
        this.fixFileName = f;
    }

    /**
     * Get the field number associated with a field name
     * @param tagName the name of the field
     * @return the number associated with the field.
     */
    public String getTagNum(String tagName) {
        return tagNameToNumber.get(tagName);
        // return tagName;
    }

    /**
     * Parses the fix repository and associates all possible repeating groups
     * that may legally found in each Message Type. Associations include
     * repeating groups, references to repeating groups within other repeating
     * groups and block.
     * @param v The fix version we should parse (e.g., FIX.4.4)
     * @throws XMLStreamException XML file is corrupted.
     */
    public void parse() throws XMLStreamException {
        InputStream dictStream = ClassLoader.getSystemResourceAsStream(fixFileName);
        XMLStreamReader reader = factory.createXMLStreamReader(dictStream);
        while (reader.hasNext()) {
            int event = reader.next();
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    String elementName = reader.getLocalName();
                    elementNames.add(elementName);
                    if ("fields".equals(elementName)) {
                        isFieldsElement = true;
                        continue;
                    }
                    if ("field".equals(elementName) && isFieldsElement) {
                        String num = reader.getAttributeValue(null, "number");
                        String name = reader.getAttributeValue(null, "name");
                        FieldParser.logger.info(num + ", " + name);
                        // Integer tagNumber = Integer.parseInt(num);
                        tagNameToNumber.put(name, num);
                        tagNumberToName.put(num, name);
                        continue;
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    elementName = reader.getLocalName();
                    if ("fields".equals(elementName)) {
                        isFieldsElement = false;
                        return;
                    }
                    break;
                default:
            }
        }
        reader.close();
    }
}
