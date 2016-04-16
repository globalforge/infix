package com.globalforge.infix.qfix;

import javax.xml.stream.XMLStreamException;

public abstract class ComponentParser {
    protected ComponentManager componentMgr;
    protected ResolveManager resolveMgr;
    protected GroupManager groupMgr;
    protected FieldParser fParser;
    protected String fixFileName;

    public abstract void parse() throws XMLStreamException, Exception;
    /*
     * Beyond FIX.4.3 this is provided by ResolverManager.
     */
    protected DataStore ctxStore = new DataStore();

    public DataStore getContextStore() {
        return ctxStore;
    }

    public ResolveManager getResolveMgr() {
        return resolveMgr;
    }
}
