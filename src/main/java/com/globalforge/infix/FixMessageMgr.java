package com.globalforge.infix;

import java.math.BigDecimal;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.globalforge.infix.api.InfixAPI;
import com.globalforge.infix.api.InfixField;
import com.globalforge.infix.api.InfixUserContext;
import com.globalforge.infix.api.InfixUserTerminal;

/*-
 The MIT License (MIT)

 Copyright (c) 2015 Global Forge LLC

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
 * achieved as follows. One map is used to map a context string (a reference to
 * a tag in the rule syntax) to an ordinal value representing the tag's position
 * in the message. A second map is used to map the ordinal value to a
 * {@link InfixField} which contains the fix field data (tag number and value).
 * The only way to access information is via a "context" which is defined as a
 * refernce to a fix field in rule syntax.
 * 
 * @author Michael Starkie
 */
public class FixMessageMgr {
    /** logger */
    final static Logger logger = LoggerFactory.getLogger(FixMessageMgr.class);
    /** maps a tag in rule syntax to a relative point in the message */
    private final Map<String, BigDecimal> ctxDict =
        new HashMap<String, BigDecimal>();
    /** maps the point in the message to a FixField */
    private final Map<BigDecimal, InfixField> fldDict =
        new TreeMap<BigDecimal, InfixField>();
    /** a cache of user defined impl by class name */
    private final Map<String, InfixUserContext> userContextMap =
        new HashMap<String, InfixUserContext>();
    /** a cache of user defined impl by class name */
    private final Map<String, InfixUserTerminal> userAssignMap =
        new HashMap<String, InfixUserTerminal>();
    private FixGroupMgr grpMgr = null;

    /**
     * Parses a fix message, assigns context, and keeps state.
     * 
     * @param baseMsg The input message
     * @throws Exception The fix message can not be parsed.
     */
    public FixMessageMgr(String baseMsg) throws Exception {
        parseMessage(baseMsg);
    }

    public FixMessageMgr(String tag8Value, String tag35Value) throws Exception {
        init(tag8Value);
        putFieldFromMsgParse("8", tag8Value);
        putFieldFromMsgParse("35", tag35Value);
    }

    /**
     * Obtain a view into the context dictionary.
     * 
     * @return Map<String, BigDecimal> A map of immutable objects. The key is
     * the tag number in rule syntax. The value is the unique decimal which
     * describes the order the fix field appears in the fix message.
     */
    Map<String, BigDecimal> getCtxToOrderDict() {
        return new HashMap<String, BigDecimal>(ctxDict);
    }

    /**
     * Obtain a view into the field dictionary
     * 
     * @return Map<BigDecimal, FixField> A map of immutable objects. They key is
     * the unique place or order in which the fix field appears in the fix
     * message and value represents the fix field containing both tag number and
     * tag value.
     */
    Map<BigDecimal, InfixField> getOrderToFieldDict() {
        return new HashMap<BigDecimal, InfixField>(fldDict);
    }

    /**
     * Obtain a mapping of tag num if rule context to FixField
     * 
     * @return Map<String, FixField>. The key is the tag number in rule syntax
     * and the value is the fix data wrapped in {@link InfixField}.
     */
    Map<String, InfixField> getCtxToFieldDict() {
        Map<String, InfixField> retMap = new HashMap<String, InfixField>();
        String[] keySet = ctxDict.keySet().toArray(new String[ctxDict.size()]);
        for (String ctx : keySet) {
            BigDecimal bd = ctxDict.get(ctx);
            InfixField ff = fldDict.get(bd);
            retMap.put(ctx, ff);
        }
        return retMap;
    }

    /**
     * Obtain a mapping of tag number to FixField
     * 
     * @return Map<Integer, FixField>. The key is the tag number and the value
     * is the fix data wrapped in {@link InfixField}.
     */
    Map<Integer, InfixField> getTagNumToFieldDict() {
        Map<Integer, InfixField> retMap = new HashMap<Integer, InfixField>();
        String[] keySet = ctxDict.keySet().toArray(new String[ctxDict.size()]);
        for (String ctx : keySet) {
            BigDecimal bd = ctxDict.get(ctx);
            InfixField ff = fldDict.get(bd);
            retMap.put(ff.getTagNum(), ff);
        }
        return retMap;
    }

    /**
     * Reset the system. Parse a fix message into data structures.
     * 
     * @param baseMsg The base message.
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws Exception Ill formatted baseMsg encountered.
     */
    private void parseMessage(String baseMsg) throws ClassNotFoundException,
        InstantiationException, IllegalAccessException {
        ctxDict.clear();
        fldDict.clear();
        int p = 0;
        int prev = 0;
        String field = null;
        while ((p = baseMsg.indexOf('\001', prev)) != -1) {
            field = baseMsg.substring(prev, p);
            prev = p + 1;
            if (field == null || field.isEmpty()) {
                continue;
            }
            parseField(field.trim());
        }
        field = baseMsg.substring(prev);
        if (field != null && !field.isEmpty()) {
            parseField(field.trim());
        }
    }

    /**
     * Parses a string in the form "35=D" into a tag num (35) and tag value (D)
     * and calls {@link FixMessageMgr#putField(int, String)} to map the results.
     * 
     * @param fixField The string representing a Fix field as it is found in a
     * Fix message.
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException If the fix message contains a Fix version
     * in tag 8 that is unrecognized the system will fail when it tries to
     * instantiate a {@link FixGroupMgr} for that version at runtime.
     */
    private void parseField(String fixField) throws ClassNotFoundException,
        InstantiationException, IllegalAccessException {
        int index = fixField.indexOf('=');
        String tagNum = fixField.substring(0, index);
        String tagVal = fixField.substring(index + 1);
        if (tagNum.equals("8")) {
            init(tagVal);
        }
        putFieldFromMsgParse(tagNum, tagVal);
    }

    /**
     * Dynamically creates a {@link FixGroupMgr} representing the fixVersion at
     * runtime. Each fixVersion is assigned a unique FixGroupMgr because each
     * fix version has new definitions of repeating groups.
     * 
     * @param fixVersion The value found in tag 8 in a Fix message.
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException If the fix message contains a Fix version
     * in tag 8 that is unrecognized the system will fail when it tries to
     * instantiate a {@link FixGroupMgr} for that version at runtime.
     */
    private void init(String fixVersion) throws ClassNotFoundException,
        InstantiationException, IllegalAccessException {
        grpMgr = FixContextMgr.getInstance().getGroupMgr(fixVersion);
    }

    /**
     * Obtain the Fix message type of the message being transformed.
     * 
     * @return String The Fix message type.
     */
    private String getMsgType() {
        String tagVal = null;
        InfixField msgType = getField(35);
        if (msgType != null) {
            tagVal = msgType.getTagVal();
        }
        return tagVal;
    }

    /**
     * The method to use when putting a field during a message parse.
     * 
     * @param tagNum The tag number associated with a fix field.
     * @param tagVal The tag value associated with a fix field.
     */
    private void putFieldFromMsgParse(String tagNum, String tagVal) {
        putField(tagNum, tagVal, true);
    }

    /**
     * The method to use when putting a field during a rule parse.
     * 
     * @param tagNum The tag number associated with a fix field.
     * @param tagVal The tag value associated with a fix field.
     */
    void putFieldFromPostMsgParse(String tagNum, String tagVal) {
        putField(tagNum, tagVal, false);
    }

    /**
     * Inserts a fix field into the mapping. Converts a tag number and value
     * into a rule context and inserts the context and associated field data
     * into the mappings. Order of tag data within the message is maintained.
     * 
     * @param tagNum The tag number
     * @param tagVal The tag value
     * @param rememberGroups Whether to keep track of repeating groups.
     */
    private void putField(String tagNum, String tagVal, boolean rememberGroups) {
        FixFieldContext f =
            grpMgr.getContext(getMsgType(), tagNum, tagVal, rememberGroups);
        String ctx = f.getKey();
        if (containsContext(ctx)) {
            BigDecimal ordinal = ctxDict.get(ctx);
            InfixField fld =
                new InfixField(fldDict.get(ordinal).getTagNum(), tagVal);
            fldDict.put(ordinal, fld);
        } else {
            ctxDict.put(f.getKey(), f.getValue());
            fldDict.put(f.getValue(), new InfixField(Integer.parseInt(tagNum),
                tagVal));
        }
    }

    /**
     * Maps a tag number to a tag value.<br>
     * Insertion of a repeating tag requires location information.<br>
     * A stack of tag contexts is provided to help with the task of identifying
     * where in memory to insert a repeating tag. Example: Insert tag 524 with
     * value "FOO". <br>
     * Tag 524 is a member of the NestedParites repeating group inside an
     * execution report. <br>
     * NestedParties itself is nested inside the InstrmtLegExecGrp repeating
     * group.<br>
     * The user may wisih to assign a value to the 2nd nesting of
     * InstrmtLegExecGrp inside the first nesting of NestedParites and will
     * indicate this using the rule context: &555[0]->&539[1]->&524 <br>
     * If the user is simply replacing the value of this tag then the
     * <code> &555[0]->&539[1]->&524 </code> context will already exist in
     * memory. <br>
     * If the tag doesn't exist in the 2nd nesting but other tags in the 2nd
     * nesting do exists then then 524 should come after the last tag in the 2nd
     * nesting. That location is referenced by the context
     * <code> &555[0]->&539[1]-> </code>. <br>
     * What if the user is starting a brand new nesting of NestedParites? In
     * that case the correct order is after the last tag of the previous nesting
     * whose location can be referenced by <code> &555[0]->&539[0]-> </code> The
     * below information is provided for every repeating group which represents
     * a complete logical hierarchy of locations to check for completeness even
     * though the correct location will be found before the bottom of the stack
     * is reached in the example given. It can be thought of as follows: <br>
     * <code>
     * &555[0]->&539[1]->&525 (if not here look below)
     * &555[0]->&539[0]-> (if not here look below)
     * &555[0]->&539 (if not here look below)
     * &555[0]-> (if not here look below)
     * &555 (look here)
     * </code> <br>
     * One of the elements on the stack will always point to a reference in
     * memory that holds the point after which the tag should be inserted as
     * long as the rules are used correctly.<br>
     * The parser builds this information naturally during a rule parse reducing
     * it's cost in terms of performance. The stack is ordered by most "likely
     * first" reducing the iteration loops needed to identify the location.
     * 
     * @param contexts A stack of rule contexts describing potential insertion
     * points.
     * @param tagNum The tagnum to insert
     * @param tagVal The tagval to insert
     */
    void putContext(Deque<String> contexts, String tagNum, String tagVal) {
        String fullContext = contexts.pop();
        if (containsContext(fullContext)) {
            BigDecimal ordinal = ctxDict.get(fullContext);
            InfixField fld =
                new InfixField(fldDict.get(ordinal).getTagNum(), tagVal);
            fldDict.put(ordinal, fld);
            return;
        }
        BigDecimal grpLocation = grpMgr.getCxtPosition(contexts);
        if (grpLocation != null) {
            ctxDict.put(fullContext, grpLocation);
            fldDict.put(grpLocation, new InfixField(Integer.parseInt(tagNum),
                tagVal));
        } else {
            putFieldFromPostMsgParse(tagNum, tagVal);
        }
    }

    /**
     * Returns a {@link InfixField} associated with the tag number.
     * 
     * @param tagNum The tag number to look up.
     * @return The FixField mapped to the tag number.
     */
    private InfixField getField(int tagNum) {
        return getContext("&" + tagNum);
    }

    /**
     * Returns a {@link InfixField} associated with the tag number in rule
     * syntax.
     * 
     * @param ctx The tag number to look up in rule syntax (e.g., &35).
     * @return The FixField mapped to the tag number.
     */
    InfixField getContext(String ctx) {
        if (!ctxDict.containsKey(ctx)) { return null; }
        BigDecimal ordinal = ctxDict.get(ctx);
        InfixField fld = fldDict.get(ordinal);
        return fld;
    }

    /**
     * Removes a context and it's value from memory.
     * 
     * @param ctx The tag context to remove.
     */
    private void remove(String ctx) {
        BigDecimal ordinal = ctxDict.remove(ctx);
        if (ordinal == null) {
            // logger.error(
            // "no context found (if group member, check syntax): {}", ctx);
            return;
        }
        fldDict.remove(ordinal);
    }

    /**
     * Removes a fix field given a tag reference in rule syntax.
     * 
     * @param ctx The tag number in rule syntax.
     */
    void removeContext(String ctx) {
        if (ctx.equals("&8")) {
            logger.info("Context not allowed for deletion: " + ctx);
            return;
        }
        remove(ctx);
        grpMgr.removeCtxPosition(ctx);
        int idxOfTagRef = ctx.lastIndexOf("&");
        String tagNum = ctx.substring(idxOfTagRef + 1);
        boolean isGroupId = FixGroupMgr.containsGrpId(getMsgType(), tagNum);
        if (isGroupId) {
            String[] keySet =
                ctxDict.keySet().toArray(new String[ctxDict.size()]);
            for (String key : keySet) {
                if (key.startsWith(ctx)) {
                    remove(key);
                    idxOfTagRef = key.lastIndexOf("&");
                    String tagRef = key.substring(0, idxOfTagRef);
                    grpMgr.removeCtxPosition(tagRef);
                }
            }
        }
    }

    /**
     * Removes a set of Fix fields given a set of tag numbers in rule syntax.
     * Group IDs in the provided set will remove all member tags associated with
     * the group.
     * 
     * @param removeTags The set of tags to remove.
     */
    void removeContext(Set<String> removeTags) {
        String[] keySet = ctxDict.keySet().toArray(new String[ctxDict.size()]);
        for (String ctx : keySet) {
            Iterator<String> removeSet = removeTags.iterator();
            while (removeSet.hasNext()) {
                String removeTag = removeSet.next();
                if (ctx.equals(removeTag) || ctx.startsWith(removeTag + "[")) {
                    removeContext(ctx);
                    break;
                }
            }
        }
    }

    /**
     * Removes all the tags except those in the given set. Tags 8 (FixVersion)
     * and 35 (MsgType) can not be removed. Group IDs provided in the set will
     * presere the entire set of member tags within the group.
     * 
     * @param keepTags The set of tags to keep.
     */
    void keepContext(Set<String> keepTags) {
        Iterator<String> ctxSet = ctxDict.keySet().iterator();
        while (ctxSet.hasNext()) {
            String ctx = ctxSet.next();
            Iterator<String> keepSet = keepTags.iterator();
            boolean keep = false;
            while (keepSet.hasNext()) {
                String keepTag = keepSet.next();
                if (ctx.equals(keepTag) || ctx.startsWith(keepTag + "[")
                    || ctx.equals("&8")) {
                    keep = true;
                    break;
                }
            }
            if (!keep) {
                BigDecimal ordinal = ctxDict.get(ctx);
                if (ordinal != null) {
                    ctxSet.remove();
                    fldDict.remove(ordinal);
                    grpMgr.removeCtxPosition(ctx);
                }
            }
        }
    }

    /**
     * Determines if a tag in rule syntax is present.
     * 
     * @param ctx The tag in rule syntax
     * @return boolean true if tag is present.
     */
    boolean containsContext(String ctx) {
        return ctxDict.containsKey(ctx);
    }

    /**
     * Debug method to print out the dictionaries.
     */
    void printDict() {
        // logger.info("--- Ctx Dictionary ---");
        Iterator<String> iter = ctxDict.keySet().iterator();
        int chksum = 0;
        while (iter.hasNext()) {
            String k = iter.next();
            if (k.equals("&10")) {
                // logger.info("Key: {}, Value: {} (ignore chksum)", k,
                // ctxDict.get(k));
                chksum = ctxDict.get(k).intValue();
            } else {
                // logger.info("Key: {}, Value: {}", k, ctxDict.get(k));
            }
        }
        // logger.info("--- Fld Dictionary ---");
        Iterator<Entry<BigDecimal, InfixField>> it =
            fldDict.entrySet().iterator();
        while (it.hasNext()) {
            Entry<BigDecimal, InfixField> entry = it.next();
            BigDecimal k = entry.getKey();
            if (k.intValue() == chksum) {
                // logger.info("Key: {}, Value: {} (ignore chksum)",
                // entry.getKey(), entry.getValue());
            } else {
                // logger.info("Key: {}, Value: {}", entry.getKey(),
                // entry.getValue());
            }
        }
        grpMgr.printMarks();
    }

    /**
     * Producted a valid FIX string from the state of this instance.
     * 
     * @return String A valid FIX string.
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        int bodyLength = 0;
        Iterator<Entry<BigDecimal, InfixField>> it =
            fldDict.entrySet().iterator();
        String fieldStr = null;
        while (it.hasNext()) {
            Entry<BigDecimal, InfixField> entry = it.next();
            InfixField field = entry.getValue();
            if ((field.getTagNum() == 8) || (field.getTagNum() == 9)
                || (field.getTagNum() == 10)) {
                continue;
            }
            fieldStr = field.toString() + '\u0001';
            bodyLength += fieldStr.length();
            str.append(fieldStr);
        }
        putFieldFromPostMsgParse(Integer.toString(9),
            Integer.toString(bodyLength));
        InfixField bodyLen = getField(9);
        fieldStr = bodyLen.toString() + '\u0001';
        str.insert(0, fieldStr);
        InfixField version = getField(8);
        fieldStr = version.toString() + '\u0001';
        str.insert(0, fieldStr);
        char[] inputChars = str.toString().toCharArray();
        int checkSum = 0;
        for (int aChar : inputChars) {
            checkSum += aChar;
        }
        putField(Integer.toString(10), String.format("%03d", checkSum % 256),
            false);
        InfixField chksum = getField(10);
        str.append(chksum).append('\u0001');
        return str.toString();
    }

    /**
     * Calls into the class provided as an argument with the result of
     * toString(). This allows a caller to obtain a fix message representation
     * of the state contained in an instance of this class. The caller can
     * manipulate the message and pass it back where it will be parsed creating
     * a new in memory state.
     * 
     * @param userCtx The caller's class to call back into with the FIX string.
     */
    private void handleCallVisitMessage(InfixUserContext userCtx) {
        String curMsg = toString();
        String newMsg = userCtx.visitMessage(curMsg);
        try {
            parseMessage(newMsg);
        } catch (Throwable e) {
            logger
                .error(
                    "Could not parse new msg. Attempting to recover original msg. [bad msg: {}]",
                    newMsg, e);
            try {
                parseMessage(curMsg);
            } catch (Throwable e1) {
                logger
                    .error(
                        "Could not recover original msg. Abandon All Hope. [orig msg: {}]",
                        curMsg, e);
                return;
            }
            logger.info("Recovered original msg: {}", curMsg);
        }
    }

    /**
     * Calls into the class provided as an argument with an instance of an API
     * handle to an instance of this class allowing the argument class to call
     * back using a limited number of read-only operations.
     * 
     * @param userCtx The class that is allowed to call back using the API.
     * @see InfixUserContext
     */
    private void handleCallVisitAPI(InfixUserContext userCtx) {
        userCtx.visitInfixAPI(new FixAPIImpl(this));
    }

    /**
     * Create an instance of a user defined implementation of InfixUserContext
     * and call the visit method
     * 
     * @param className The name of the class to instantiate.
     */
    void handleUserDefinedContext(String className, String methodName) {
        InfixUserContext userCtx = userContextMap.get(className);;
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
                throw new IllegalArgumentException("No such method: "
                    + methodName);
            }
        }
    }

    /**
     * Calls into a user defined class to obtain a value for an assignment.
     * 
     * @param userCtx The user's implementation of the class producting the
     * value in the assignment.
     * @see InfixUserTerminal#visitTerminal(InfixAPI)
     * @return String the value to use in the assignment.
     */
    private String handleCallVisitUserTerm(InfixUserTerminal userCtx) {
        return userCtx.visitTerminal(new FixAPIImpl(this));
    }

    /**
     * Calls into a user defined class to obtain a value for an assignment.
     * 
     * @param className an implementation of
     * {@linkInfixUserAssignment#visitAssignment(InfixAPI)}
     * @see FixMessageMgr#handleCallVisitUserTerm(InfixUserTerminal)
     * @return String the value to use in the assignment.
     */
    String handleUserDefinedTerminal(String className) {
        try {
            InfixUserTerminal userCtx = userAssignMap.get(className);;
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
}
