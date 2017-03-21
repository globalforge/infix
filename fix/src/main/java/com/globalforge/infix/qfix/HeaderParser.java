package com.globalforge.infix.qfix;

import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.globalforge.infix.qfix.AbstractXMLParser.CurrentContext;

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
 * Base class for parsing the header section of a fix xml data dictionary.
 * Header parsing comes after field and component parsing.
 * @author Michael C. Starkie
 */
public class HeaderParser {
    /** logger */
    protected final static Logger logger = LoggerFactory.getLogger(HeaderParser.class);
    /** Map of field context (e.g., 8) to order it appears in the header */
    private final LinkedHashMap<String, String> ctxMap = new LinkedHashMap<String, String>();
    private final Deque<CurrentContext> elementStack = new ArrayDeque<CurrentContext>(100);
    private final Deque<String> curGroupStack = new ArrayDeque<String>(100);
    private final Deque<String> groupCtxStack = new ArrayDeque<String>(100);
    private final XMLInputFactory factory = XMLInputFactory.newInstance();
    private final FieldParser fParser;
    private final String fixFileName;
    /** Keeps track of the number of fields in a header */
    private final AtomicInteger fieldOrder = new AtomicInteger(0);
    /** all information about fields and groups */
    private DataStore ctxStore;

    /**
     * @param f FIX data dictionary file name
     * @param cParser data parses from the fields section
     * @param c component and group information from the component parse
     * @throws Exception
     */
    public HeaderParser(String f, FieldParser cParser, DataStore c) throws Exception {
        this.fixFileName = f;
        this.fParser = cParser;
        this.ctxStore = c;
    }

    /**
     * returns the current number of fields seen so far during a parse.
     * @return int
     */
    public int getCurFieldOrder() {
        return fieldOrder.get();
    }

    /**
     * Begin parsing
     * @throws XMLStreamException
     */
    public void parse() throws XMLStreamException {
        parseHeader();
        printMembers();
    }

    /**
     * Returns the context map (see class member section).
     * @return LinkedHashMap<String, String>
     */
    public LinkedHashMap<String, String> getContextMap() {
        return ctxMap;
    }

    /**
     * Parses components block. Expects field, group, or component.
     */
    public void parseHeader() throws XMLStreamException {
        InputStream dictStream = ClassLoader.getSystemResourceAsStream(fixFileName);
        XMLStreamReader reader = factory.createXMLStreamReader(dictStream);
        int memberOrder = 1;
        while (reader.hasNext()) {
            int event = reader.next();
            CurrentContext curContext = null;
            if (elementStack.peek() != null) {
                curContext = elementStack.peek();
            }
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    String elementName = reader.getLocalName();
                    if ("header".equals(elementName)) {
                        elementStack.push(CurrentContext.HEADER);
                    }
                    if ("group".equals(elementName) && (curContext == CurrentContext.HEADER)) {
                        String tagName = reader.getAttributeValue(null, "name");
                        String tagNum = fParser.getTagNum(tagName);
                        String tagCtx = tagNum;
                        ctxMap.put(tagCtx, fieldOrder.incrementAndGet() + "");
                        elementStack.push(CurrentContext.GROUP);
                        curGroupStack.push(tagNum);
                        groupCtxStack.push(tagCtx);
                        // FIX.4.4 and below only
                        ctxStore.startMessageGroup("HEADER", curGroupStack.peek());
                    }
                    if ("field".equals(elementName) && (curContext == CurrentContext.HEADER)) {
                        String tagName = reader.getAttributeValue(null, "name");
                        String tagNum = fParser.getTagNum(tagName);
                        String tagCtx = tagNum;
                        ctxMap.put(tagCtx, fieldOrder.incrementAndGet() + "");
                    }
                    if ("component".equals(elementName) && (curContext == CurrentContext.HEADER)) {
                        String componentName = reader.getAttributeValue(null, "name");
                        // FIX.5.0 and above only
                        addComponents(componentName);
                    }
                    if ("field".equals(elementName) && (curContext == CurrentContext.GROUP)) {
                        String tagName = reader.getAttributeValue(null, "name");
                        String tagNum = fParser.getTagNum(tagName);
                        String tagCtx = groupCtxStack.peek() + "[*]->" + tagNum;
                        ctxMap.put(tagCtx, fieldOrder + ".*" + memberOrder++);
                        // FIX.4.4 and below only
                        ctxStore.addMessageGroupMember("HEADER", curGroupStack.peek(), tagNum);
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    elementName = reader.getLocalName();
                    if ("header".equals(elementName)) {
                        elementStack.pop();
                    }
                    if ("group".equals(elementName) && (curContext == CurrentContext.GROUP)) {
                        elementStack.pop();
                        groupCtxStack.pop();
                        curGroupStack.pop();
                        memberOrder = 1;
                    }
                    break;
                default:
            }
        }
        reader.close();
    }

    /**
     * Stores component information found in a header. Only used in FIX 5.0 and
     * above where repeating groups in a header are defined as components.
     * @param componentName the name of the component
     */
    private void addComponents(String componentName) {
        LinkedList<String> cList = ctxStore.getComponentContext(componentName);
        if (cList == null) {
            cList = ctxStore.getGroupContext(componentName);
        }
        if (cList == null) { throw new RuntimeException("No such component: " + componentName); }
        Iterator<String> i = cList.iterator();
        int memberOrder = 0;
        String curGrp = null;
        String groupId = null;
        while (i.hasNext()) {
            String ctx = i.next();
            int tagIdx = ctx.lastIndexOf('>');
            String tagNum = ctx.substring(tagIdx + 1);
            if (memberOrder == 0) {
                ctxMap.put(ctx, fieldOrder.incrementAndGet() + "");
                memberOrder++;
                curGrp = ctx;
                groupId = tagNum;
                ctxStore.startMessageGroup("HEADER", groupId);
            } else {
                String tagCtx = curGrp + "[*]->" + tagNum;
                ctxMap.put(tagCtx, fieldOrder + ".*" + memberOrder++);
                ctxStore.addMessageGroupMember("HEADER", groupId, tagNum);
            }
        }
    }

    /**
     * data dump for debugging
     */
    private void printMembers() {
        Set<Entry<String, String>> compMems = ctxMap.entrySet();
        Iterator<Entry<String, String>> memSetIterator = compMems.iterator();
        HeaderParser.logger.info("--- BEGIN HEADER ---");
        while (memSetIterator.hasNext()) {
            Entry<String, String> memSetEntry = memSetIterator.next();
            String tagCtx = memSetEntry.getKey();
            String orderCtx = memSetEntry.getValue();
            HeaderParser.logger.info(tagCtx + ", " + orderCtx);
        }
        HeaderParser.logger.info("--- END HEADER ---");
    }
}
