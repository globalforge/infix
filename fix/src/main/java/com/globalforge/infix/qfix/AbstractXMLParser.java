package com.globalforge.infix.qfix;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

public abstract class AbstractXMLParser {
    protected final XMLInputFactory factory = XMLInputFactory.newInstance();
    protected final String fixFileName;

    public static enum CurrentContext {
        HEADER, MESSAGES, MESSAGE, COMPONENTS, COMPONENT, GROUP, FIELD, TRAILER
    }

    public AbstractXMLParser(String f) {
        this.fixFileName = f;
    }

    public abstract void parse() throws XMLStreamException;
}