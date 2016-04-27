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
                        String tagCtx = "&" + fParser.getTagNum(tagName);
                        LinkedHashMap<String, String> fieldMap = messageMap.get(curMessage);
                        fieldMap.put(tagCtx, null);
                    }
                    // No components before FIX 4.3 - but it's ok here. Never
                    // get's called.
                    if ("component".equals(elementName) && (curContext == CurrentContext.MESSAGE)) {
                        String componentName = reader.getAttributeValue(null, "name");
                        LinkedList<String> components = ctxStore.getComponentContext(componentName);
                        addComponents(curMessage, components, "", null);
                    }
                    if ("group".equals(elementName) && (curContext == CurrentContext.MESSAGE)) {
                        elementStack.push(CurrentContext.GROUP);
                        String tagName = reader.getAttributeValue(null, "name");
                        String tagNum = fParser.getTagNum(tagName);
                        curGroupStack.push(tagNum);
                        String tagCtx = "&" + tagNum;
                        LinkedHashMap<String, String> fieldMap = messageMap.get(curMessage);
                        fieldMap.put(tagCtx, null);
                        groupCtxStack.push(tagCtx);
                        ctxStore.startMessageGroup(curMessage, curGroupStack.peek());
                    }
                    if ("field".equals(elementName) && (curContext == CurrentContext.GROUP)) {
                        String tagName = reader.getAttributeValue(null, "name");
                        String tagNum = fParser.getTagNum(tagName);
                        String tagCtx = groupCtxStack.peek() + "[*]->" + "&" + tagNum;
                        LinkedHashMap<String, String> fieldMap = messageMap.get(curMessage);
                        fieldMap.put(tagCtx, null);
                        ctxStore.addMessageGroupMember(curMessage, curGroupStack.peek(), tagNum);
                    }
                    // No components before FIX 4.3 - but it's ok here. Never
                    // get's called.
                    if ("component".equals(elementName) && (curContext == CurrentContext.GROUP)) {
                        String componentName = reader.getAttributeValue(null, "name");
                        if ("R".equals(curMessage) && "Stipulations".equals(componentName)) {
                            System.out.println();
                        }
                        LinkedList<String> components = ctxStore.getComponentContext(componentName);
                        addComponents(curMessage, components, groupCtxStack.peek() + "[*]->",
                            curGroupStack.peek());
                    }
                    if ("group".equals(elementName) && (curContext == CurrentContext.GROUP)) {
                        String tagName = reader.getAttributeValue(null, "name");
                        String tagNum = fParser.getTagNum(tagName);
                        if ("E".equals(curMessage) && "78".equals(tagNum)) {
                            System.out.println();
                        }
                        curGroupStack.push(tagNum);
                        String tagCtx = "&" + tagNum;
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
