package com.globalforge.infix.qfix;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*-
The MIT License (MIT)

Copyright (c) 2019-2020 Global Forge LLC

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
 * Base class for runtime classes which load all the generated classes and
 * static data into memory.
 * @author Michael C. Starkie
 */
public abstract class MessageData {
    private final static Logger logger = LoggerFactory.getLogger(MessageData.class);
    /** information used in field order classes */
    protected Map<String, FieldOrderMap> fieldOrderMap = new HashMap<String, FieldOrderMap>();
    /** information used in group definition classes */
    protected Map<String, FixGroupMgr> groupMap = new HashMap<String, FixGroupMgr>();

    /**
     * Dynamically loads the extension of FieldOrderMap associated with a
     * message type.
     * @param msgType
     * @return FieldOrderMap
     */
    public FieldOrderMap getFieldOrderMap(String msgType) {
        FieldOrderMap fldMap = fieldOrderMap.get(msgType);
        if (fldMap != null) { return fldMap; }
        String methodName = "initMessageType_" + msgType + "_" + msgType.hashCode();
        try {
            Method method = getClass().getMethod(methodName);
            method.invoke(this);
            fldMap = fieldOrderMap.get(msgType);
        } catch (Exception e) {
            MessageData.logger.error("Can't obtain Field Order Map for Message Type: " + msgType,
                e);
            MessageData.logger.error("Check Data Dictionary for Message Type: " + msgType);
        }
        return fldMap;
    }

    /**
     * Dynamically loads the extension of FixGroupMgr associated with a message
     * type.
     * @param msgType
     * @return FixGroupMgr
     */
    public FixGroupMgr getGroupMgr(String msgType) {
        FixGroupMgr grpMgr = groupMap.get(msgType);
        if (grpMgr != null) { return grpMgr; }
        String methodName = "initMessageType_" + msgType + "_" + msgType.hashCode();
        try {
            Method method = getClass().getMethod(methodName);
            method.invoke(this);
            grpMgr = groupMap.get(msgType);
        } catch (Exception e) {
            MessageData.logger.error("Can't obtain Group Manager for Message Type: " + msgType, e);
            MessageData.logger.error("Check Data Dictionary for Message Type: " + msgType);
        }
        return grpMgr;
    }
}
