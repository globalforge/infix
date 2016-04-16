package com.globalforge.infix.qfix;

import java.util.HashMap;
import java.util.Map;

public abstract class MessageData {
    protected Map<String, FieldOrderMap> fieldOrderMap = new HashMap<String, FieldOrderMap>();
    protected Map<String, FixGroupMgr> groupMap = new HashMap<String, FixGroupMgr>();

    public FieldOrderMap getFieldOrderMap(String msgType) {
        return fieldOrderMap.get(msgType);
    }

    public FixGroupMgr getGroupMgr(String msgType) {
        return groupMap.get(msgType);
    }
}
