package com.globalforge.infix.qfix.fix44;

import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.globalforge.infix.qfix.AbstractXMLParser.CurrentContext;
import com.globalforge.infix.qfix.ComponentParser;
import com.globalforge.infix.qfix.FieldParser;
import com.globalforge.infix.qfix.HeaderParser;
import com.globalforge.infix.qfix.MessageParser;

/*-
The MIT License (MIT)

Copyright (c) 2017 Global Forge LLC

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
 * Parse a FIX 4.4 message and all FIX versions prior to FIX 4.4
 * @author Michael C. Starkie
 */
public class FIX44MessageParser extends MessageParser {
    /** logger */
    final static Logger logger = LoggerFactory.getLogger(FIX44MessageParser.class);
    private final Deque<CurrentContext> elementStack = new ArrayDeque<CurrentContext>(100);
    private final Deque<String> groupCtxStack = new ArrayDeque<String>(100);
    private final Deque<String> curGroupStack = new ArrayDeque<String>(100);
    private final XMLInputFactory factory = XMLInputFactory.newInstance();

    public FIX44MessageParser(String f, FieldParser p, HeaderParser h, ComponentParser c)
        throws Exception {
        super(f, p, h, c);
    }

    @Override
    public void parse() throws XMLStreamException {
        parseMessages();
        calcMemberSizes();
        orderAllMessages();
        printMembers();
    }

    /**
     * Parses components block. Expects field, group, or component.
     */
    public void parseMessages() throws XMLStreamException {
        InputStream dictStream = ClassLoader.getSystemResourceAsStream(fixFileName);
        XMLStreamReader reader = factory.createXMLStreamReader(dictStream);
        String curMessage = null;
        FIX44MessageParser.logger.info("--- BEGIN MESSAGES ---");
        while (reader.hasNext()) {
            int event = reader.next();
            CurrentContext curContext = null;
            if (elementStack.peek() != null) {
                curContext = elementStack.peek();
            }
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    String elementName = reader.getLocalName();
                    if ("message".equals(elementName)) {
                        elementStack.push(CurrentContext.MESSAGE);
                        curMessage = reader.getAttributeValue(null, "msgtype");
                        LinkedHashMap<String, String> fieldMap = messageMap.get(curMessage);
                        if (fieldMap == null) {
                            fieldMap = new LinkedHashMap<String, String>();
                            messageMap.put(curMessage, fieldMap);
                        }
                    }
                    if ("field".equals(elementName) && (curContext == CurrentContext.MESSAGE)) {
                        String tagName = reader.getAttributeValue(null, "name");
                        String tagCtx = fParser.getTagNum(tagName);
                        LinkedHashMap<String, String> fieldMap = messageMap.get(curMessage);
                        fieldMap.put(tagCtx, null);
                    }
                    // No components before FIX 4.3 - but it's ok here. Never
                    // get's called.
                    if ("component".equals(elementName) && (curContext == CurrentContext.MESSAGE)) {
                        String componentName = reader.getAttributeValue(null, "name");
                        LinkedList<String> components = ctxStore.getComponentContext(componentName);
                        if ("7".equals(curMessage)) {
                            // System.out.println();
                        }
                        addComponents(curMessage, components, "", null);
                    }
                    if ("group".equals(elementName) && (curContext == CurrentContext.MESSAGE)) {
                        elementStack.push(CurrentContext.GROUP);
                        String tagName = reader.getAttributeValue(null, "name");
                        String tagNum = fParser.getTagNum(tagName);
                        curGroupStack.push(tagNum);
                        String tagCtx = tagNum;
                        LinkedHashMap<String, String> fieldMap = messageMap.get(curMessage);
                        fieldMap.put(tagCtx, null);
                        groupCtxStack.push(tagCtx);
                        ctxStore.startMessageGroup(curMessage, curGroupStack.peek());
                    }
                    if ("field".equals(elementName) && (curContext == CurrentContext.GROUP)) {
                        String tagName = reader.getAttributeValue(null, "name");
                        String tagNum = fParser.getTagNum(tagName);
                        String tagCtx = groupCtxStack.peek() + "[*]->" + tagNum;
                        LinkedHashMap<String, String> fieldMap = messageMap.get(curMessage);
                        fieldMap.put(tagCtx, null);
                        ctxStore.addMessageGroupMember(curMessage, curGroupStack.peek(), tagNum);
                    }
                    // No components before FIX 4.3 - but it's ok here. Never
                    // get's called.
                    if ("component".equals(elementName) && (curContext == CurrentContext.GROUP)) {
                        String componentName = reader.getAttributeValue(null, "name");
                        if ("E".equals(curMessage) && "Stipulations".equals(componentName)) {
                            // System.out.println();
                        }
                        LinkedList<String> components = ctxStore.getComponentContext(componentName);
                        addComponents(curMessage, components, groupCtxStack.peek() + "[*]->",
                            curGroupStack.peek());
                    }
                    if ("group".equals(elementName) && (curContext == CurrentContext.GROUP)) {
                        String tagName = reader.getAttributeValue(null, "name");
                        String tagNum = fParser.getTagNum(tagName);
                        if ("E".equals(curMessage) && "78".equals(tagNum)) {
                            // System.out.println();
                        }
                        ctxStore.addMessageGroupReference(curMessage, curGroupStack.peek(), tagNum);
                        curGroupStack.push(tagNum);
                        String tagCtx = tagNum;
                        String curGrpCtx = groupCtxStack.peek() + "[*]->" + tagCtx;
                        groupCtxStack.push(curGrpCtx);
                        LinkedHashMap<String, String> fieldMap = messageMap.get(curMessage);
                        fieldMap.put(curGrpCtx, null);
                        elementStack.push(CurrentContext.GROUP);
                        ctxStore.startMessageGroup(curMessage, curGroupStack.peek());
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    elementName = reader.getLocalName();
                    if ("message".equals(elementName)) {
                        elementStack.pop();
                        curMessage = null;
                    }
                    if ("group".equals(elementName) && (curContext == CurrentContext.GROUP)) {
                        elementStack.pop();
                        groupCtxStack.pop();
                        curGroupStack.pop();
                    }
                    break;
                default:
            }
        }
        FIX44MessageParser.logger.info("--- END MESSAGES ---");
        reader.close();
    }
}
