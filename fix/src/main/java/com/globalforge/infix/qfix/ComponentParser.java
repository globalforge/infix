package com.globalforge.infix.qfix;

import javax.xml.stream.XMLStreamException;

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
 * Base class for all component parser element types found in an xml fix
 * dictionary.
 * 
 * @author Michael C. Starkie
 */
public abstract class ComponentParser {
    protected ComponentManager componentMgr;
    protected ResolveManager resolveMgr;
    protected GroupManager groupMgr;
    protected FieldParser fParser;
    protected String fixFileName;

    /**
     * Parse a component section of a fix data dictionary
     * 
     * @throws XMLStreamException
     * @throws Exception
     */
    public abstract void parse() throws XMLStreamException, Exception;
    /*
     * Beyond FIX.4.3 this is provided by ResolverManager.
     */
    protected DataStore ctxStore = new DataStore();

    /**
     * Obtains the data store holding all the field and group information for
     * all components.
     * 
     * @return
     */
    public DataStore getContextStore() {
        return ctxStore;
    }

    /**
     * Obtain the resolver algorithm which resolves all component and group
     * references.
     * 
     * @return
     */
    public ResolveManager getResolveMgr() {
        return resolveMgr;
    }
}
