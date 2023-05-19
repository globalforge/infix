package com.globalforge.infix.qfix;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

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
 * Intermediate data store used by DataGenerator hold field context maps for
 * message types.
 * @author Michael C. Starkie
 */
public class ContextOrderMap {
    protected final LinkedHashMap<String, LinkedHashMap<String, String>> messageMap =
        new LinkedHashMap<String, LinkedHashMap<String, String>>();

    public LinkedHashMap<String, LinkedHashMap<String, String>> getMessageMap() {
        return messageMap;
    }

    /**
     * Transfers all context mappings into this class.
     * @param msgType The value of FIX message type
     * @param ctxOrderMap field contexts belonging to a message type
     */
    public void addAll(String msgType, LinkedHashMap<String, String> ctxOrderMap) {
        LinkedHashMap<String, String> ctxMap = messageMap.get(msgType);
        if (ctxMap == null) {
            ctxMap = new LinkedHashMap<String, String>();
            messageMap.put(msgType, ctxMap);
        }
        ctxMap.putAll(ctxOrderMap);
    }

    /**
     * Transfers all group information into this class.
     * @param otherMap the other map to transfer from.
     */
    public void addAll(ContextOrderMap otherMap) {
        LinkedHashMap<String, LinkedHashMap<String, String>> otherMessageMap = otherMap.messageMap;
        Iterator<Entry<String, LinkedHashMap<String, String>>> otherEntries =
            otherMessageMap.entrySet().iterator();
        while (otherEntries.hasNext()) {
            Entry<String, LinkedHashMap<String, String>> otherEntry = otherEntries.next();
            String msgType = otherEntry.getKey();
            LinkedHashMap<String, String> cMap = otherEntry.getValue();
            addAll(msgType, cMap);
        }
    }
}
