package com.globalforge.infix.qfix;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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
 * Transfers all repeating group info collected during a fix data dictionary
 * parse and consolidates it in a class used by the code generator.
 * @author Michael C. Starkie
 */
public class RepeatingGroupBuilderMap {
    /** A map of MsgType to repeating groups mapped by groupId */
    protected final Map<String, Map<String, RepeatingGroupBuilder>> grpMap =
        new HashMap<String, Map<String, RepeatingGroupBuilder>>();

    /**
     * Return map of MsgType to repeating groups mapped by groupId
     * @return Map<String, Map<String, RepeatingGroupBuilder>>
     */
    public Map<String, Map<String, RepeatingGroupBuilder>> getGroupMap() {
        return grpMap;
    }

    /**
     * Collect all the repeating group info found in a message section.
     * @param msgType The message type
     * @param gMap The group info
     */
    public void addAll(String msgType, Map<String, RepeatingGroupBuilder> gMap) {
        Map<String, RepeatingGroupBuilder> ctxMap = grpMap.get(msgType);
        if (ctxMap == null) {
            ctxMap = new HashMap<String, RepeatingGroupBuilder>();
            grpMap.put(msgType, ctxMap);
        }
        ctxMap.putAll(gMap);
    }

    /**
     * Collect all the repeating group info found elsewhere like the components
     * section.
     * @param otherMap A map of groups collected during a components parse.
     */
    public void addAll(RepeatingGroupBuilderMap otherMap) {
        Map<String, Map<String, RepeatingGroupBuilder>> otherMessageMap = otherMap.grpMap;
        Iterator<Entry<String, Map<String, RepeatingGroupBuilder>>> otherEntries =
            otherMessageMap.entrySet().iterator();
        while (otherEntries.hasNext()) {
            Entry<String, Map<String, RepeatingGroupBuilder>> otherEntry = otherEntries.next();
            String msgType = otherEntry.getKey();
            Map<String, RepeatingGroupBuilder> cMap = otherEntry.getValue();
            addAll(msgType, cMap);
        }
    }
}
