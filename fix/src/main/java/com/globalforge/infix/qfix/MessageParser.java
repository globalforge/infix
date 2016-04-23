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

    public MessageParser(String f, FieldParser cParser, HeaderParser h, ComponentParser c) {
        this.headerParser = h;
        this.fixFileName = f;
        this.fParser = cParser;
        this.cParser = c;
        this.ctxStore = c.getContextStore();
    }

    public abstract void parse() throws XMLStreamException, Exception;

    public DataStore getFIXDataStore() {
        return ctxStore;
    }

    public HeaderParser getHeaderParser() {
        return headerParser;
    }

    public Map<String, Map<String, String>> getMessageMap() {
        return Collections.unmodifiableMap(messageMap);
    }

    public Map<String, Map<String, Integer>> getGroupMap() {
        return Collections.unmodifiableMap(groupMap);
    }

    private String getMantissaHash(int memberPos, int groupSize) {
        BigDecimal dividend = new BigDecimal(memberPos + 1.0, MathContext.DECIMAL32);
        BigDecimal divisor = new BigDecimal(groupSize + 1.0, MathContext.DECIMAL32);
        // --
        BigDecimal memberHash =
            dividend.divide(divisor, MessageParser.mathCtx).stripTrailingZeros();
        String locationHash = memberHash.toPlainString();
        int decimalPoint = locationHash.indexOf('.');
        String mantissa = locationHash.substring(decimalPoint + 1);
        return mantissa;
    }

    public static String getGroupIdCtx(String ctxString) {
        int bracketIdx = ctxString.lastIndexOf("[");
        if (bracketIdx < 0) {
            return null;
        }
        String groupIdCtx = ctxString.substring(0, bracketIdx);
        return groupIdCtx;
    }

    protected LinkedHashMap<String, String> orderMessage(String msgName, int fOrder) {
        HashMap<Integer, String> nestMap = new HashMap<Integer, String>();
        LinkedHashMap<String, String> newMsgFieldMap = new LinkedHashMap<String, String>();
        LinkedHashMap<String, String> msgFields = messageMap.get(msgName);
        Iterator<String> ctxKeys = msgFields.keySet().iterator();
        HashMap<String, Integer> groupMemberCount = new HashMap<String, Integer>();
        while (ctxKeys.hasNext()) {
            int memberPos = 0;
            String ctxKey = ctxKeys.next();
            int starCount = ctxKey.length() - ctxKey.replace("*", "").length();
            String groupIdCtx = MessageParser.getGroupIdCtx(ctxKey);
            LinkedHashMap<String, Integer> msgGroupMap = groupMap.get(msgName);
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
        newMsgFieldMap.put("&93", 1999998 + "");
        newMsgFieldMap.put("&89", 1999999 + "");
        newMsgFieldMap.put("&10", 2000000 + "");
        return newMsgFieldMap;
    }

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
     * Number after last '&'
     * 
     * @param ctxString Full context of field reference.
     * @return String a tag number
     */
    public static String getTagNumber(String ctxString) {
        int tagIdx = ctxString.lastIndexOf("&");
        String tagNum = ctxString.substring(tagIdx + 1);
        return tagNum;
    }

    private boolean isComponentGroup(String groupId) {
        if (ctxStore.isComponentGroup(groupId)) {
            return true;
        }
        return false;
    }

    private RepeatingGroupBuilder getComponentGroup(String groupId) {
        return ctxStore.getComponentGroup(groupId);
    }

    private RepeatingGroupBuilder getMessageGroup(String curMessage, String groupId) {
        if (ctxStore.isMessageGroup(curMessage, groupId)) {
            return ctxStore.getMessageGroup(curMessage, groupId);
        }
        return null;
    }

    protected void addComponents(String curMessage, LinkedList<String> components, String preCtx,
        String groupId) {
        RepeatingGroupBuilder curGroup = null;
        if (groupId != null) {
            curGroup = getMessageGroup(curMessage, groupId);
            if (curGroup == null) {
                curGroup = ctxStore.startMessageGroup(curMessage, groupId);
            }
        }
        LinkedHashMap<String, String> fieldMap = messageMap.get(curMessage);
        Iterator<String> compMems = components.iterator();
        while (compMems.hasNext()) {
            String compCtx = compMems.next();
            String tagNum = MessageParser.getTagNumber(compCtx);
            if (isComponentGroup(tagNum)) {
                groupId = tagNum;
                curGroup = getComponentGroup(groupId);
                ctxStore.startMessageGroup(curMessage, groupId);
                fieldMap.put(preCtx + compCtx, null);
                // preCtx = preCtx + compCtx + "[*]->";
            } else if (curGroup != null) {
                ctxStore.addMessageGroupMember(curMessage, groupId, tagNum);
                fieldMap.put(preCtx + compCtx, null);
            }
        }
    }

    /*-
    protected void addComponents(String curMessage, LinkedList<String> components, String preCtx,
        String gId) {
        LinkedHashMap<String, String> fieldMap = messageMap.get(curMessage);
        Iterator<String> compMems = components.iterator();
        ResolveManager rMgr = cParser.getResolveMgr();
        String groupId = null;
        QuickFixRepeatingGroup curGroup = null;
        if (gId != null) {
            groupId = gId;
            if (!ctxStore.isGroup(curMessage, groupId)) {
                curGroup = ctxStore.startGroup(curMessage, groupId);
            } else {
                curGroup = ctxStore.getGroup(curMessage, groupId);
            }
        }
        while (compMems.hasNext()) {
            String compCtx = compMems.next();
            String tagNum = getTagNumber(compCtx);
            if (rMgr.isGroupId(tagNum)) {
                groupId = tagNum;
                curGroup = rMgr.getRepeatingGroup(groupId);
                ctxStore.startGroup(curMessage, groupId);
            } else if (curGroup != null) {
                ctxStore.addGroupMember(curMessage, groupId, tagNum);
            }
            fieldMap.put(preCtx + compCtx, null);
        }
    }
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
