package com.globalforge.infix.qfix;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class RepeatingGroupBuilderMap {
    /** A map of MsgType to repeating groups mapped by groupId */
    protected final Map<String, Map<String, RepeatingGroupBuilder>> grpMap = new HashMap<String, Map<String, RepeatingGroupBuilder>>();

    public Map<String, Map<String, RepeatingGroupBuilder>> getGroupMap() {
        return grpMap;
    }

    public void addAll(String msgType,
        Map<String, RepeatingGroupBuilder> gMap) {
        Map<String, RepeatingGroupBuilder> ctxMap = grpMap.get(msgType);
        if (ctxMap == null) {
            ctxMap = new HashMap<String, RepeatingGroupBuilder>();
            grpMap.put(msgType, ctxMap);
        }
        ctxMap.putAll(gMap);
    }

    public void addAll(RepeatingGroupBuilderMap otherMap) {
        Map<String, Map<String, RepeatingGroupBuilder>> otherMessageMap = otherMap.grpMap;
        Iterator<Entry<String, Map<String, RepeatingGroupBuilder>>> otherEntries = otherMessageMap
            .entrySet().iterator();
        while (otherEntries.hasNext()) {
            Entry<String, Map<String, RepeatingGroupBuilder>> otherEntry = otherEntries
                .next();
            String msgType = otherEntry.getKey();
            Map<String, RepeatingGroupBuilder> cMap = otherEntry.getValue();
            addAll(msgType, cMap);
        }
    }
}
