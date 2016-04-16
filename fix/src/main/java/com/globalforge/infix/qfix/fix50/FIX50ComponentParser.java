package com.globalforge.infix.qfix.fix50;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.globalforge.infix.qfix.ComponentManager;
import com.globalforge.infix.qfix.ComponentParser;
import com.globalforge.infix.qfix.FieldParser;
import com.globalforge.infix.qfix.GroupManager;
import com.globalforge.infix.qfix.ResolveManager;

public class FIX50ComponentParser extends ComponentParser {
    @SuppressWarnings("unused")
    final private static Logger logger = LoggerFactory.getLogger(FIX50ComponentParser.class);
    protected Document doc = null;

    public FIX50ComponentParser(String f, FieldParser cParser) throws Exception {
        this.fParser = cParser;
        this.fixFileName = f;
        this.componentMgr = new ComponentManager();
        this.groupMgr = new GroupManager();
        this.resolveMgr = new ResolveManager(componentMgr, groupMgr, fParser);
    }

    @Override
    public void parse() throws XMLStreamException, Exception {
        buildDocument();
        walkComponents();
        resolveMgr.runAlgo();
        resolveMgr.printCompletedComponents();
        resolveMgr.printCompletedGroups();
        this.ctxStore = resolveMgr.getContextStore();
    }

    protected void buildDocument() throws ParserConfigurationException, SAXException, IOException {
        InputStream dictStream = ClassLoader.getSystemResourceAsStream(fixFileName);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(dictStream);
        doc.getDocumentElement().normalize();
    }

    protected void walkComponents() {
        NodeList nList = doc.getElementsByTagName("components");
        Node root = nList.item(0);
        NodeList components = root.getChildNodes();
        for (int j = 0; j < components.getLength(); j++) {
            Node componentNode = components.item(j);
            if (componentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) componentNode;
                String name = element.getAttribute("name");
                componentMgr.initializeComponent(name);
                parseComponentElements(componentNode, name);
            }
        }
    }

    protected void parseComponentElements(Node root, String compName) {
        // LinkedList<String> memberList = ctxMap.get(compName);
        NodeList compChildren = root.getChildNodes();
        for (int j = 0; j < compChildren.getLength(); j++) {
            Node compChild = compChildren.item(j);
            if (compChild.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) compChild;
                String elementName = element.getTagName();
                String attrName = element.getAttribute("name");
                if ("field".equals(elementName)) {
                    componentMgr.addMember(compName, attrName);
                } else if ("group".equals(elementName)) {
                    // refer to the outer component name, not the name attribute
                    // of the group
                    groupMgr.setGroupId(compName, attrName);
                    parseGroupElements(compChild, compName);
                } else if ("component".equals(elementName)) {
                    componentMgr.addNestedComponent(compName, attrName);
                }
            }
        }
    }

    protected void parseGroupElements(Node root, String componentName) {
        NodeList compChildren = root.getChildNodes();
        for (int j = 0; j < compChildren.getLength(); j++) {
            Node compChild = compChildren.item(j);
            if (compChild.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) compChild;
                String elementName = element.getTagName();
                String attrName = element.getAttribute("name");
                if ("field".equals(elementName)) {
                    groupMgr.addMember(componentName, attrName);
                } else if ("component".equals(elementName)) {
                    groupMgr.addNestedComponent(componentName, attrName);
                } else if ("group".equals(elementName)) { throw new RuntimeException(
                    "Nested groups not handled for this fix version: " + elementName); }
            }
        }
    }
}
