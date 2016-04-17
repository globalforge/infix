package com.globalforge.infix.qfix;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MessageData {
    private final static Logger logger = LoggerFactory
        .getLogger(MessageData.class);
    protected Map<String, FieldOrderMap> fieldOrderMap = new HashMap<String, FieldOrderMap>();
    protected Map<String, FixGroupMgr> groupMap = new HashMap<String, FixGroupMgr>();

    public FieldOrderMap getFieldOrderMap(String msgType) {
        FieldOrderMap fldMap = fieldOrderMap.get(msgType);
        if (fldMap != null) {
            return fldMap;
        }
        String methodName = "initMessageType_" + msgType + "_"
            + msgType.hashCode();
        try {
            Method method = getClass().getMethod(methodName);
            method.invoke(this);
            fldMap = fieldOrderMap.get(msgType);
        } catch (Exception e) {
            logger.error(
                "Can't obtain Field Order Map for Message Type: " + msgType, e);
            logger.error("Check Data Dictionary for Message Type: " + msgType);
        }
        return fldMap;
    }

    public FixGroupMgr getGroupMgr(String msgType) {
        FixGroupMgr grpMgr = groupMap.get(msgType);
        if (grpMgr != null) {
            return grpMgr;
        }
        String methodName = "initMessageType_" + msgType + "_"
            + msgType.hashCode();
        try {
            Method method = getClass().getMethod(methodName);
            method.invoke(this);
            grpMgr = groupMap.get(msgType);
        } catch (Exception e) {
            logger.error(
                "Can't obtain Group Manager for Message Type: " + msgType, e);
            logger.error("Check Data Dictionary for Message Type: " + msgType);
        }
        return grpMgr;
    }
}
