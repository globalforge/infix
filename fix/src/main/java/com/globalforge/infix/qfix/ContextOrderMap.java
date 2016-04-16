package com.globalforge.infix.qfix;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class ContextOrderMap {
    protected final LinkedHashMap<String, LinkedHashMap<String, String>> messageMap = new LinkedHashMap<String, LinkedHashMap<String, String>>();

    public LinkedHashMap<String, LinkedHashMap<String, String>> getMessageMap() {
        return messageMap;
    }

    public void addAll(String msgType,
        LinkedHashMap<String, String> ctxOrderMap) {
        LinkedHashMap<String, String> ctxMap = messageMap.get(msgType);
        if (ctxMap == null) {
            ctxMap = new LinkedHashMap<String, String>();
            messageMap.put(msgType, ctxMap);
        }
        ctxMap.putAll(ctxOrderMap);
    }

    public void addAll(ContextOrderMap otherMap) {
        LinkedHashMap<String, LinkedHashMap<String, String>> otherMessageMap = otherMap.messageMap;
        Iterator<Entry<String, LinkedHashMap<String, String>>> otherEntries = otherMessageMap
            .entrySet().iterator();
        while (otherEntries.hasNext()) {
            Entry<String, LinkedHashMap<String, String>> otherEntry = otherEntries
                .next();
            String msgType = otherEntry.getKey();
            LinkedHashMap<String, String> cMap = otherEntry.getValue();
            addAll(msgType, cMap);
        }
    }
}
