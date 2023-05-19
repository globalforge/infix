package com.globalforge.infix.qfix;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.stream.XMLStreamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.globalforge.infix.qfix.RepeatingGroupStateManager.ComponentContextState;

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
 * Parses the messages section of a FIX data dictionary
 * @author Michael C. Starkie
 */
public abstract class MessageParser {
    final static Logger logger = LoggerFactory.getLogger(MessageParser.class);
    // the precision is based on the largest repeating group. there should be no
    // group with more than 1000 members (000-999).
    private static MathContext mathCtx = new MathContext(3, RoundingMode.HALF_EVEN);
    protected final LinkedHashMap<String, LinkedHashMap<String, String>> messageMap =
        new LinkedHashMap<String, LinkedHashMap<String, String>>();
    private final LinkedHashMap<String, LinkedHashMap<String, Integer>> groupMap =
        new LinkedHashMap<String, LinkedHashMap<String, Integer>>();
    protected final HeaderParser headerParser;
    protected final ComponentParser cParser;
    protected final FieldParser fParser;
    protected final String fixFileName;
    protected final DataStore ctxStore;

    /**
     * @param f FIX File name
     * @param cParser Field parse data
     * @param h Header parse data
     * @param c Component parse data
     */
    public MessageParser(String f, FieldParser cParser, HeaderParser h, ComponentParser c) {
        this.headerParser = h;
        this.fixFileName = f;
        this.fParser = cParser;
        this.cParser = c;
        this.ctxStore = c.getContextStore();
    }

    /**
     * Entry point
     * @throws XMLStreamException XML related parsing error.
     * @throws Exception Non XML related exception.
     */
    public abstract void parse() throws XMLStreamException, Exception;

    /**
     * Obtain a reference to the data store
     * @return DataStore
     */
    public DataStore getFIXDataStore() {
        return ctxStore;
    }

    /**
     * Obtain a reference to the header parse data
     * @return HeaderParser
     */
    public HeaderParser getHeaderParser() {
        return headerParser;
    }

    /**
     * Obtain a reference to the map of message types to the map of field
     * contexts to field order.
     * @return A reference to the map of message types to the map of field contexts to field order.
     */
    public Map<String, Map<String, String>> getMessageMap() {
        return Collections.unmodifiableMap(messageMap);
    }

    /**
     * Obtain a reference to the map of message types to the map of field
     * contexts to field order.
     * @return a reference to the map of message types to the map of field
     * contexts to field order.
     */
    public Map<String, Map<String, Integer>> getGroupMap() {
        return Collections.unmodifiableMap(groupMap);
    }

    /**
     * For repeating group members, the final order of any particular member
     * within a fix message is unknown before runtime because we don't know how
     * many times a particular group will repeat. We can infer a syntax for a
     * relative order that will heuristically all but guarantee the proper order
     * of a repeating member regardless of the number of repeating member fields
     * at runtime.
     * @param memberPos
     * @param groupSize
     * @return
     */
    private String getMantissaHash(int memberPos, int groupSize) {
        BigDecimal dividend = new BigDecimal(memberPos + 1.0, MathContext.DECIMAL32);
        BigDecimal divisor = new BigDecimal(groupSize + 1.0, MathContext.DECIMAL32);
        BigDecimal memberHash =
            dividend.divide(divisor, MessageParser.mathCtx).stripTrailingZeros();
        String locationHash = memberHash.toPlainString();
        int decimalPoint = locationHash.indexOf('.');
        String mantissa = locationHash.substring(decimalPoint + 1);
        return mantissa;
    }

    /**
     * returns the innermost group identifier of a group context
     * @param ctxString The group context
     * @return String the innermost group identifier
     */
    public static String getGroupIdCtx(String ctxString) {
        int bracketIdx = ctxString.lastIndexOf("[");
        if (bracketIdx < 0) { return null; }
        String groupIdCtx = ctxString.substring(0, bracketIdx);
        return groupIdCtx;
    }

    /**
     * The algorithm which preassigns an order value to a fix field. Calculates
     * the relative order of a group member in relation to its group identifier
     * and leave a placeholder that is dependent upon it's nesting level at
     * runtime.
     * @param msgType The message type
     * @param fOrder the current order of the last field seen
     * @return A list of field contexts and their
     * order in the message.
     */
    protected LinkedHashMap<String, String> orderMessage(String msgType, int fOrder) {
        HashMap<Integer, String> nestMap = new HashMap<Integer, String>();
        LinkedHashMap<String, String> newMsgFieldMap = new LinkedHashMap<String, String>();
        LinkedHashMap<String, String> msgFields = messageMap.get(msgType);
        Iterator<String> ctxKeys = msgFields.keySet().iterator();
        HashMap<String, Integer> groupMemberCount = new HashMap<String, Integer>();
        while (ctxKeys.hasNext()) {
            int memberPos = 0;
            String ctxKey = ctxKeys.next();
            int starCount = ctxKey.length() - ctxKey.replace("*", "").length();
            String groupIdCtx = MessageParser.getGroupIdCtx(ctxKey);
            LinkedHashMap<String, Integer> msgGroupMap = groupMap.get(msgType);
            if (groupIdCtx != null) {
                int groupSize = msgGroupMap.get(groupIdCtx);
                if (!groupMemberCount.containsKey(groupIdCtx)) {
                    memberPos = 0;
                } else {
                    memberPos = groupMemberCount.get(groupIdCtx);
                    memberPos += 1;
                }
                groupMemberCount.put(groupIdCtx, memberPos);
                String mantissaHash = getMantissaHash(memberPos, groupSize);
                String nextOrder = nestMap.get(starCount - 1) + mantissaHash;
                newMsgFieldMap.put(ctxKey, nextOrder);
                nestMap.put(starCount, nextOrder + "*");
            } else {
                String nextOrder = ++fOrder + "";
                newMsgFieldMap.put(ctxKey, nextOrder);
                nestMap.put(starCount, nextOrder + ".*");
            }
        }
        // a hack to make sure the trailer fields are last.
        newMsgFieldMap.put("93", 2999998 + "");
        newMsgFieldMap.put("89", 2999999 + "");
        newMsgFieldMap.put("10", 3000000 + "");
        return newMsgFieldMap;
    }

    /**
     * Orders all fields in a message type for all message types in a FIX
     * version.
     */
    protected void orderAllMessages() {
        LinkedHashMap<String, LinkedHashMap<String, String>> newMessageMap =
            new LinkedHashMap<String, LinkedHashMap<String, String>>();
        Set<Entry<String, LinkedHashMap<String, String>>> compMems = messageMap.entrySet();
        Iterator<Entry<String, LinkedHashMap<String, String>>> memSetIterator = compMems.iterator();
        while (memSetIterator.hasNext()) {
            AtomicInteger fOrder = new AtomicInteger(headerParser.getCurFieldOrder());
            Entry<String, LinkedHashMap<String, String>> ctxEntry = memSetIterator.next();
            String msgName = ctxEntry.getKey();
            LinkedHashMap<String, String> newMsgFieldMap = orderMessage(msgName, fOrder.get());
            newMessageMap.put(msgName, newMsgFieldMap);
            // nestMap.clear();
        }
        messageMap.clear();
        messageMap.putAll(newMessageMap);
    }

    /**
     * In order to assign a relative position of a member within a repeating
     * group the algorithm needs to know the size of the repeating group in
     * terms of the number of member fields.
     */
    protected void calcMemberSizes() {
        Set<Entry<String, LinkedHashMap<String, String>>> compMems = messageMap.entrySet();
        Iterator<Entry<String, LinkedHashMap<String, String>>> memSetIterator = compMems.iterator();
        while (memSetIterator.hasNext()) {
            Entry<String, LinkedHashMap<String, String>> ctxEntry = memSetIterator.next();
            String msgName = ctxEntry.getKey();
            LinkedHashMap<String, String> msgMembers = ctxEntry.getValue();
            LinkedHashMap<String, Integer> grpOrderMap = groupMap.get(msgName);
            if (grpOrderMap == null) {
                grpOrderMap = new LinkedHashMap<String, Integer>();
                groupMap.put(msgName, grpOrderMap);
            }
            ListIterator<String> iter =
                new ArrayList<String>(msgMembers.keySet()).listIterator(msgMembers.size());
            while (iter.hasPrevious()) {
                String ctx = iter.previous();
                int brackIdx = ctx.lastIndexOf('[');
                if (brackIdx > 0) {
                    String grpId = ctx.substring(0, brackIdx);
                    Integer order = grpOrderMap.get(grpId);
                    if (order == null) {
                        grpOrderMap.put(grpId, 1);
                    } else {
                        order = new Integer(order.intValue() + 1);
                        grpOrderMap.put(grpId, order);
                    }
                }
            }
        }
    }

    /**
     * Number after last '{@literal &}'
     * @param ctxString Full context of field reference.
     * @return String a tag number
     */
    public static String getTagNumber(String ctxString) {
        int tagIdx = ctxString.lastIndexOf(">");
        String tagNum = ctxString.substring(tagIdx + 1);
        return tagNum;
    }

    /**
     * Places the field context in the map of field to order and sets the order
     * to null. The order will be assigned later.
     * @param msgType the message type
     * @param fieldCtx the field context
     */
    protected void addFieldContextKeyToOrderMap(String msgType, String fieldCtx) {
        LinkedHashMap<String, String> fieldMap = messageMap.get(msgType);
        fieldMap.put(fieldCtx, null);
    }

    /**
     * Given a list of components in infix syntax build parse out repeating
     * group if any exist.
     * @param curMessage The current message being parsed
     * @param components A list of parsed components that are referenced in a
     * message
     * @param preCtx If a component is nested in a group then the component
     * elements are members.
     * @param groupId The current group within a message being parsed (may be
     * null).
     */
    protected void addComponents(String curMessage, LinkedList<String> components, String preCtx,
        String groupId) {
        LinkedHashMap<String, String> fieldMap = messageMap.get(curMessage);
        RepeatingGroupStateManager stateMgr = new RepeatingGroupStateManager(ctxStore, fieldMap);
        if (groupId != null) {
            stateMgr.setGroupInProgressState(groupId);
        }
        Iterator<String> compMems = components.iterator();
        while (compMems.hasNext()) {
            String compCtx = preCtx + compMems.next();
            ComponentContextState curState = stateMgr.getState();
            switch (curState) {
                case FREE_FIELD:
                    stateMgr.fieldToGroupIDStateTransition(curMessage, compCtx);
                    break;
                case GROUP_START:
                    stateMgr.groupIdToMemberStateTransition(curMessage, compCtx);
                    break;
                case GROUP_MEMBER:
                    stateMgr.memberStateTransition(curMessage, compCtx);
                    break;
                default:
                    throw new RuntimeException("Unrecognized state: " + curState);
            }
        }
    }

    /**
     * data dump
     */
    protected void printMembers() {
        Set<Entry<String, LinkedHashMap<String, String>>> compMems = messageMap.entrySet();
        Iterator<Entry<String, LinkedHashMap<String, String>>> memSetIterator = compMems.iterator();
        while (memSetIterator.hasNext()) {
            Entry<String, LinkedHashMap<String, String>> ctxEntry = memSetIterator.next();
            String msgName = ctxEntry.getKey();
            Iterator<String> ctxSet = ctxEntry.getValue().keySet().iterator();
            MessageParser.logger.info("--- BEGIN MESSAGE:  " + msgName + " ---");
            while (ctxSet.hasNext()) {
                String tagCtx = ctxSet.next();
                String orderCtx = ctxEntry.getValue().get(tagCtx);
                MessageParser.logger.info(tagCtx + ", " + orderCtx);
            }
            MessageParser.logger.info("--- END MESSAGE:  " + msgName + " ---");
        }
    }

    /**
     * data dump in reverse?
     */
    protected void printMembersReverse() {
        Set<Entry<String, LinkedHashMap<String, String>>> compMems = messageMap.entrySet();
        Iterator<Entry<String, LinkedHashMap<String, String>>> memSetIterator = compMems.iterator();
        while (memSetIterator.hasNext()) {
            Entry<String, LinkedHashMap<String, String>> ctxEntry = memSetIterator.next();
            String msgName = ctxEntry.getKey();
            LinkedHashMap<String, String> msgMembers = ctxEntry.getValue();
            ListIterator<String> iter =
                new ArrayList<String>(msgMembers.keySet()).listIterator(msgMembers.size());
            System.out.println("MESSAGE NAME: " + msgName);
            while (iter.hasPrevious()) {
                String ctx = iter.previous();
                System.out.println("MESSAGE KEY: " + ctx);
            }
        }
    }

    /**
     * data dump of all group identifiers in a message
     */
    protected void printGroupIds() {
        Set<Entry<String, LinkedHashMap<String, Integer>>> compMems = groupMap.entrySet();
        Iterator<Entry<String, LinkedHashMap<String, Integer>>> memSetIterator =
            compMems.iterator();
        while (memSetIterator.hasNext()) {
            Entry<String, LinkedHashMap<String, Integer>> ctxEntry = memSetIterator.next();
            String msgName = ctxEntry.getKey();
            Iterator<String> ctxSet = ctxEntry.getValue().keySet().iterator();
            MessageParser.logger.info("--- BEGIN GROUP ORDERS:  " + msgName + " ---");
            while (ctxSet.hasNext()) {
                String tagCtx = ctxSet.next();
                Integer orderCtx = ctxEntry.getValue().get(tagCtx);
                MessageParser.logger.info(tagCtx + ", " + orderCtx);
            }
            MessageParser.logger.info("--- END GROUP ORDERS:  " + msgName + " ---");
        }
    }
}
