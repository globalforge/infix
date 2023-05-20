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

 Copyright (c) 2019-2022 Global Forge LLC

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
 * 
 * @author Michael C. Starkie
 */
public class FieldParser {
	/** logger */
	protected final XMLInputFactory factory = XMLInputFactory.newInstance();
	protected final String fixFileName;
	final static Logger logger = LoggerFactory.getLogger(FieldParser.class);
	private final Map<String, String> tagNameToNumber = new ConcurrentHashMap<String, String>();
	private final Map<String, String> tagNumberToName = new ConcurrentHashMap<String, String>();
	private final Map<String, Map<String, String>> tagNameToTagValueDesc = new ConcurrentHashMap<>();

	private final Set<String> elementNames = new HashSet<String>();
	private boolean isFieldsElement = false;

	/**
	 * Requires a name of a fix file
	 * 
	 * @param f the filename
	 * @throws Exception For caller to capture all types of exceptions including
	 *                   runtime exceptions. This is done so that an application is
	 *                   forced to make a conscience choice to ignore or fail any
	 *                   business logic associated with the application resulting
	 *                   from any exception.
	 */
	public FieldParser(String f) throws Exception {
		this.fixFileName = f;
	}

	public Map<String, String> getTagNameToNumber() {
		return tagNameToNumber;
	}

	public Map<String, String> getTagNumberToName() {
		return tagNumberToName;
	}

	public Map<String, Map<String, String>> getTagNameToTagValueDesc() {
		return tagNameToTagValueDesc;
	}

	/**
	 * Get the field number associated with a field name
	 * 
	 * @param tagName the name of the field
	 * @return the number associated with the field.
	 */
	public String getTagNum(String tagName) {
		return tagNameToNumber.get(tagName);
		// return tagName;
	}

	/**
	 * Parses the fix repository and associates all possible repeating groups that
	 * may legally found in each Message Type. Associations include repeating
	 * groups, references to repeating groups within other repeating groups and
	 * block.
	 * 
	 * @throws XMLStreamException XML file is corrupted.
	 */
	public void parse() throws XMLStreamException {
		InputStream dictStream = ClassLoader.getSystemResourceAsStream(fixFileName);
		XMLStreamReader reader = factory.createXMLStreamReader(dictStream);
		String curFieldName = "";
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
					curFieldName = reader.getAttributeValue(null, "name");
					FieldParser.logger.info(num + ", " + curFieldName);
					// Integer tagNumber = Integer.parseInt(num);
					tagNameToNumber.put(curFieldName, num);
					tagNumberToName.put(num, curFieldName);
					Map<String, String> tagValueToTagValueDesc = tagNameToTagValueDesc.get(curFieldName);
					if (tagValueToTagValueDesc == null) {
						tagValueToTagValueDesc = new ConcurrentHashMap<>();
						tagNameToTagValueDesc.put(curFieldName, tagValueToTagValueDesc);
					}
					continue;
				}
				if ("value".equals(elementName) && !"".equals(curFieldName)) {
					Map<String, String> tagValueToTagValueDesc = tagNameToTagValueDesc.get(curFieldName);
					String enumNum = reader.getAttributeValue(null, "enum");
					String enumDesc = reader.getAttributeValue(null, "description");
					tagValueToTagValueDesc.put(enumNum, enumDesc);
					continue;
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				elementName = reader.getLocalName();
				if ("fields".equals(elementName)) {
					isFieldsElement = false;
					curFieldName = "";
					return;
				}

				break;
			default:
			}
		}
		reader.close();
	}
}
