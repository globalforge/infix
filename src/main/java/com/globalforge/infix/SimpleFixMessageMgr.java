package com.globalforge.infix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.globalforge.infix.api.InfixField;
import com.globalforge.infix.api.InfixFieldInfo;

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
 * Holds the state of the fix message being transformed. Tag and value lookups
 * are based on maps for speed. Tag order is maintained. Ordered mappings are
 * maintained as tag number to InfixFieldInfo where InfixFieldInfo contains a
 * FixField and a number representing the position of the field in the message.
 * <p>
 * Order is important because Repeating Groups are ordered sets of fields.
 *
 * @author Michael Starkie
 */
public class SimpleFixMessageMgr extends FixMessageMgr {
    /** logger */
    final static Logger logger = LoggerFactory.getLogger(SimpleFixMessageMgr.class);
    /** maps a tag in rule syntax to a relative point in the message */
    private HashMap<String, InfixField> msgMap = new HashMap<String, InfixField>();

    /**
     * You better know what you're doing.
     * <p>
     * Use this constructor when you want to build the message map from scratch.
     * <br>
     * You should call the constructor and then make repeated calls to
     * parseField(). <br>
     * Start with tags 8 and 35 to be safe (e.g., parseField("8=FIX.4.2")
     * followed by parseField("35=D")) or parseField("8", "FIX.4.2") followed by
     * parseField("35", "D")
     * <p>
     * Take care when adding repeating groups. Delimiter fields in groups are an
     * absolute necessity.
     */
    public SimpleFixMessageMgr() {
    }

    /**
     * Parses a fix message in raw fix format, assigns context, and keeps state.
     *
     * @param baseMsg The input message
     * @throws Exception reflection error
     */
    public SimpleFixMessageMgr(String baseMsg) throws Exception {
        parseMessage(baseMsg);
    }

    /**
     * Initialize the message manager with a map.
     *
     * @param mMap A map of tag number to InfixFieldInfo
     * @throws Exception Tag 8 and Tag 35 mandatory.
     */
    public SimpleFixMessageMgr(Map<String, InfixFieldInfo> mMap) throws Exception {
        ArrayList<InfixFieldInfo> orderedFields = new ArrayList<InfixFieldInfo>(mMap.values());
        Collections.sort(orderedFields);
        for (InfixFieldInfo fieldInfo : orderedFields) {
            InfixField field = fieldInfo.getField();
            parseField(field.getTagNum() + "", field.getTagVal());
        }
    }

    /**
     * Parses a string in the form "35=D" into a tag number (35) and tag value
     * (D) and calls {@link SimpleFixMessageMgr#putField(int, String)} to map
     * the results.
     *
     * @param fixField The string representing a Fix field as it is found in a
     * Fix message.
     * @throws Exception can't create the runtime classes specified by the FIX
     * version.
     */
    public void parseField(String tagStr, String tagVal) throws Exception {
        int tagNum = 0;
        try {
            tagNum = Integer.parseInt(tagStr);
        } catch (NumberFormatException e) {
            SimpleFixMessageMgr.logger.warn("Dropping non-numeric tag: [" + tagStr + "]");
            return;
        }
        putField(tagNum, tagVal);
    }

    /**
     * Inserts a fix field into the mapping. Converts a tag number and value
     * into a rule context and inserts the context and associated field data
     * into the mappings. Order of tag data within the message is maintained.
     *
     * @param tagNum The tag number
     * @param tagVal The tag value
     */
    private void putField(int tagNum, String tagVal) {
        String tagStr = Integer.toString(tagNum);
        String ctx = null;
        ctx = "" + tagNum;
        msgMap.put(ctx, new InfixField(tagStr, tagVal));
    }

    /**
     * Inserts an Infix context directly into the message map
     *
     * @param ctx The Infix context
     * @param tagVal The tag value and order of the field referenced by the
     * Infix context
     */
    /*-
    void putContext(String ctx, String tagVal) {
        InfixField fldPos = msgMap.get(ctx);
        if (fldPos != null) {
            msgMap.put(ctx, new InfixFieldInfo(fldPos.getField().getTagNum() + "", tagVal,
                fldPos.getPosition()));
            return;
        }
        String tagNum = FixFieldOrderHash.getTagNumber(ctx);
        try {
            Integer.parseInt(tagNum);
        } catch (NumberFormatException e) {
            SimpleFixMessageMgr.logger.warn("Dropping non-numeric tag number: [" + tagNum + "]");
            return;
        }
        BigDecimal pos = posGen.getFieldPosition(getMsgType(), ctx);
        msgMap.put(ctx, new InfixFieldInfo(tagNum, tagVal, pos));
    }
    
    */
    /**
     * Returns a {@link InfixField} associated with the tag number.
     *
     * @param tagNum The tag number to look up.
     * @return The FixField mapped to the tag number.
     */
    private InfixFieldInfo getField(int tagNum) {
        return getContext("" + tagNum);
    }
    /**
     * Returns a {@link InfixField} associated with the tag number in rule
     * syntax.
     *
     * @param ctx The tag number to look up in rule syntax (e.g., 35).
     * @return The FixField mapped to the tag number.
     */
    /*-
    InfixFieldInfo getContext(String ctx) {
        return msgMap.get(ctx);
    }
    */
    /**
     * Removes a context and it's value from memory.
     *
     * @param ctx The tag context to remove.
     */
    /*-
    private InfixFieldInfo remove(String ctx) {
        return msgMap.remove(ctx);
    }
    */
    /**
     * Removes a fix field given a tag reference in rule syntax.
     *
     * @param ctx The tag number in rule syntax.
     */

    /*-
    void removeContext(String ctx) {
        if (ctx.equals("8") || ctx.equals("35")) {
            SimpleFixMessageMgr.logger.info("Context not allowed for deletion: " + ctx);
            return;
        }
        remove(ctx);
        int idxOfTagRef = ctx.lastIndexOf(">");
        String tagNum = ctx.substring(idxOfTagRef + 1);
        boolean isGroupId = msgData.getGroupMgr(getMsgType()).containsGrpId(tagNum);
        if (isGroupId) {
            for (String key : msgMap.keySet().toArray(new String[msgMap.size()])) {
                if (key.startsWith(ctx)) {
                    remove(key);
                }
            }
        }
    }
    */
    /**
     * Determines if a tag in rule syntax is present.
     *
     * @param ctx The tag in rule syntax
     * @return boolean true if tag is present.
     */
    boolean containsContext(String ctx) {
        return msgMap.containsKey(ctx);
    }

    /**
     * Debug method to print out the dictionaries.
     */
    void printDict() {
        SimpleFixMessageMgr.logger.info("--- InfixMessageMap ---");
        Iterator<String> iter = msgMap.keySet().iterator();
        while (iter.hasNext()) {
            String fldCtx = iter.next();
            SimpleFixMessageMgr.logger.info("Key: {}, Value: {}", fldCtx, msgMap.get(fldCtx));
        }
    }
    /**
     * Produces a valid FIX string from the state of this instance.
     *
     * @return String A valid FIX string.
     */
    /*-
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        int bodyLength = 0;
        ArrayList<InfixFieldInfo> orderedFields = new ArrayList<InfixFieldInfo>(msgMap.values());
        Collections.sort(orderedFields);
        String fieldStr = null;
        for (InfixFieldInfo fieldInfo : orderedFields) {
            InfixField field = fieldInfo.getField();
            if ((field.getTagNum() == 8) || (field.getTagNum() == 9) || (field.getTagNum() == 10)) {
                continue;
            }
            fieldStr = field.toString() + '\u0001';
            bodyLength += fieldStr.length();
            str.append(fieldStr);
        }
        putField(9, Integer.toString(bodyLength));
        InfixFieldInfo bodyLen = getContext("9");
        fieldStr = bodyLen.getField().toString() + '\u0001';
        str.insert(0, fieldStr);
        InfixFieldInfo version = getContext("8");
        fieldStr = version.getField().toString() + '\u0001';
        str.insert(0, fieldStr);
        char[] inputChars = str.toString().toCharArray();
        int checkSum = 0;
        for (int aChar : inputChars) {
            checkSum += aChar;
        }
        putField(10, String.format("%03d", checkSum % 256));
        InfixFieldInfo chksum = getContext("10");
        str.append(chksum.getField()).append('\u0001');
        return str.toString();
    }
    */
    /**
     * Calls into the class provided as an argument with the result of
     * toString(). This allows a caller to obtain a fix message representation
     * of the state contained in an instance of this class. The caller can
     * manipulate the message and pass it back where it will be parsed creating
     * a new in memory state.
     *
     * @param userCtx The caller's class to call back into with the FIX string.
     */
    /*-
    private void handleCallVisitMessage(InfixUserContext userCtx) {
        String curMsg = toString();
        String newMsg = userCtx.visitMessage(curMsg);
        try {
            msgMap.clear();
            parseMessage(newMsg);
        } catch (Throwable e) {
            SimpleFixMessageMgr.logger.error(
                "Could not parse new msg. Attempting to recover original msg. [bad msg: {}]",
                newMsg, e);
            try {
                parseMessage(curMsg);
            } catch (Throwable e1) {
                SimpleFixMessageMgr.logger.error(
                    "Could not recover original msg. Abandon All Hope. [orig msg: {}]", curMsg, e);
                return;
            }
            SimpleFixMessageMgr.logger.info("Recovered original msg: {}", curMsg);
        }
    }
    */
    /**
     * Calls into the class provided as an argument with an instance of an API
     * handle to an instance of this class allowing the argument class to call
     * back using a limited number of read-only operations.
     *
     * @param userCtx The class that is allowed to call back using the API.
     * @see InfixUserContext
     */
    /*-
    private void handleCallVisitAPI(InfixUserContext userCtx) {
        userCtx.visitInfixAPI(new FixAPIImpl(this));
    }
    */
    /**
     * Create an instance of a user defined implementation of InfixUserContext
     * and call the visit method
     *
     * @param className The name of the class to instantiate.
     */
    /*-
    void handleUserDefinedContext(String className, String methodName) {
        InfixUserContext userCtx = userContextMap.get(className);
        if (userCtx == null) {
            Class<?> c = null;
            try {
                c = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }
            try {
                userCtx = (InfixUserContext) c.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
                return;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return;
            }
            userContextMap.put(className, userCtx);
        }
        if (methodName == null) {
            handleCallVisitAPI(userCtx);
        } else {
            if (methodName.equals("visitMessage")) {
                handleCallVisitMessage(userCtx);
            } else if (methodName.equals("visitInfixAPI")) {
                handleCallVisitAPI(userCtx);
            } else {
                throw new IllegalArgumentException("No such method: " + methodName);
            }
        }
    }
    */
    /**
     * Calls into a user defined class to obtain a value for an assignment.
     *
     * @param userCtx The user's implementation of the class producting the
     * value in the assignment.
     * @see InfixUserTerminal#visitTerminal(InfixAPI)
     * @return String the value to use in the assignment.
     */
    /*-
    private String handleCallVisitUserTerm(InfixUserTerminal userCtx) {
        return userCtx.visitTerminal(new FixAPIImpl(this));
    }
    */
    /**
     * Calls into a user defined class to obtain a value for an assignment.
     *
     * @param className an implementation of
     * {@linkInfixUserAssignment#visitAssignment(InfixAPI)}
     * @see SimpleFixMessageMgr#handleCallVisitUserTerm(InfixUserTerminal)
     * @return String the value to use in the assignment.
     */
    /*-
    String handleUserDefinedTerminal(String className) {
        try {
            InfixUserTerminal userCtx = userAssignMap.get(className);
            if (userCtx == null) {
                Class<?> c = Class.forName(className);
                userCtx = (InfixUserTerminal) c.newInstance();
                userAssignMap.put(className, userCtx);
            }
            return handleCallVisitUserTerm(userCtx);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException("Error ivoking class " + className, e);
        }
    }
    
    */
}
