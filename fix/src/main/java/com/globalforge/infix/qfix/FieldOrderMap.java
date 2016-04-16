package com.globalforge.infix.qfix;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FieldOrderMap {
    /** logger */
    protected final Logger logger = LoggerFactory.getLogger(FieldOrderMap.class);
    protected final Map<String, String> msgFieldOrderMap = new HashMap<String, String>();

    public void putFieldOrder(String fieldCtx, String orderOfField) {
        msgFieldOrderMap.put(fieldCtx, orderOfField);
    }

    public String getFieldOrder(String fieldCtx) {
        return msgFieldOrderMap.get(fieldCtx);
    }
}
