package com.globalforge.infix.qfix.fix44;

import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.globalforge.infix.qfix.AbstractXMLParser.CurrentContext;
import com.globalforge.infix.qfix.ComponentManager;
import com.globalforge.infix.qfix.ComponentParser;
import com.globalforge.infix.qfix.FieldParser;
import com.globalforge.infix.qfix.GroupManager;
import com.globalforge.infix.qfix.ResolveManager;

/*-
 The MIT License (MIT)

 Copyright (c) 2016 Global Forge LLC

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
 * Parse FIX 4.4 components section
 * @author Michael C. Starkie
 */
public class FIX44ComponentParser extends ComponentParser {
    /** logger */
    final static Logger logger = LoggerFactory.getLogger(FIX44ComponentParser.class);
    private final Deque<CurrentContext> elementStack = new ArrayDeque<CurrentContext>(100);
    private final Deque<String> curGroupStack = new ArrayDeque<String>(100);
    private final XMLInputFactory factory = XMLInputFactory.newInstance();

    public FIX44ComponentParser(String f, FieldParser cParser) throws Exception {
        this.fixFileName = f;
        this.fParser = cParser;
        this.componentMgr = new ComponentManager();
        this.groupMgr = new GroupManager();
        this.resolveMgr = new ResolveManager(componentMgr, groupMgr, fParser);
    }

    @Override
    public void parse() throws XMLStreamException {
        parseComponents();
        resolveMgr.runAlgo();
        resolveMgr.printCompletedComponents();
        resolveMgr.printCompletedGroups();
        this.ctxStore = resolveMgr.getContextStore();
    }

    /**
     * Parses components block. Expects field, group, or component.
     */
    public void parseComponents() throws XMLStreamException {
        InputStream dictStream = ClassLoader.getSystemResourceAsStream(fixFileName);
        XMLStreamReader reader = factory.createXMLStreamReader(dictStream);
        String curComponent = null;
        while (reader.hasNext()) {
            int event = reader.next();
            CurrentContext curContext = null;
            if (elementStack.peek() != null) {
                curContext = elementStack.peek();
            }
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    String elementName = reader.getLocalName();
                    if ("components".equals(elementName)) {
                        elementStack.push(CurrentContext.COMPONENTS);
                    } else if ("component".equals(elementName)
                        && (curContext == CurrentContext.COMPONENTS)) {
                        // new component
                        elementStack.push(CurrentContext.COMPONENT);
                        String name = reader.getAttributeValue(0);
                        curComponent = name;
                        if ("Instrument".equals(curComponent)) {
                            // System.out.println();
                        }
                        componentMgr.initializeComponent(name);
                    } else if ("component".equals(elementName)
                        && (curContext == CurrentContext.COMPONENT)) {
                        // component within a component
                        elementStack.push(CurrentContext.COMPONENT);
                        String componentName = reader.getAttributeValue(0);
                        componentMgr.addNestedComponent(curComponent, componentName);
                    } else if ("group".equals(elementName)
                        && (curContext == CurrentContext.COMPONENT)) {
                        // group within a component or group within a group
                        elementStack.push(CurrentContext.GROUP);
                        String groupName = reader.getAttributeValue(0);
                        groupMgr.setGroupId(groupName, groupName);
                        componentMgr.addNestedComponent(curComponent, groupName);
                        curGroupStack.push(groupName);
                    } else if ("group".equals(elementName)
                        && (curContext == CurrentContext.GROUP)) {
                        // group within a component or group within a group
                        elementStack.push(CurrentContext.GROUP);
                        String groupName = reader.getAttributeValue(0);
                        groupMgr.setGroupId(groupName, groupName);
                        groupMgr.addNestedGroup(curGroupStack.peek(), groupName);
                        curGroupStack.push(groupName);
                    } else if ("field".equals(elementName)
                        && (curContext == CurrentContext.COMPONENT)) {
                        // field within a component
                        String fieldName = reader.getAttributeValue(0);
                        componentMgr.addMember(curComponent, fieldName);
                    } else if ("component".equals(elementName)
                        && (curContext == CurrentContext.GROUP)) {
                        // component within a group
                        elementStack.push(CurrentContext.COMPONENT);
                        String componentName = reader.getAttributeValue(0);
                        groupMgr.addNestedComponent(curGroupStack.peek(), componentName);
                    } else if ("field".equals(elementName)
                        && (curContext == CurrentContext.GROUP)) {
                        // field within a group
                        String fieldName = reader.getAttributeValue(0);
                        groupMgr.addMember(curGroupStack.peek(), fieldName);
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    elementName = reader.getLocalName();
                    if ("components".equals(elementName)) {
                        elementStack.pop();
                    }
                    if ("component".equals(elementName)
                        && (curContext == CurrentContext.COMPONENTS)) {
                        elementStack.pop();
                    }
                    if ("component".equals(elementName)
                        && (curContext == CurrentContext.COMPONENT)) {
                        elementStack.pop();
                    }
                    if ("component".equals(elementName) && (curContext == CurrentContext.GROUP)) {
                        elementStack.pop();
                    }
                    if ("group".equals(elementName) && (curContext == CurrentContext.GROUP)) {
                        elementStack.pop();
                        curGroupStack.pop();
                    }
                    if ("group".equals(elementName) && (curContext == CurrentContext.COMPONENT)) {
                        elementStack.pop();
                        curGroupStack.pop();
                    }
                    break;
                default:
            }
        }
        reader.close();
    }
}
