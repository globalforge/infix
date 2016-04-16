package com.globalforge.infix.qfix;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DataGenerator {
    private Map<String, ContextOrderMap> orderMap =
        new HashMap<String, ContextOrderMap>();
    private Map<String, RepeatingGroupBuilderMap> groupMap =
        new HashMap<String, RepeatingGroupBuilderMap>();

    public ContextOrderMap getContextOrderMap(String fVersion) {
        return orderMap.get(fVersion);
    }

    public RepeatingGroupBuilderMap getRepeatingGroupMap(String fVersion) {
        return groupMap.get(fVersion);
    }

    private void parseFIX(String fVersion) throws Exception {
        // Parse all the xml data into objects we can manage.
        DataDictionaryParser eng = new DataDictionaryParser(fVersion);
        eng.parseFields();
        eng.parseComponents();
        eng.parseHeader();
        eng.parseMessages();
        // Here we collect all mappings of infix contexts to their relative
        // order for each message type and stuff it into a
        // QuickFixContextOrderMap. We also prepend the header field contexts to
        // the set of fields in each message type. The trailer contexts are
        // post-peneded by hard-code because they are the same in each FIX
        // version. At the end we have every field context listed in each
        // message type mapped to their relative order in the message.
        ContextOrderMap ctxMap = orderMap.get(fVersion);
        if (ctxMap == null) {
            ctxMap = new ContextOrderMap();
            orderMap.put(fVersion, ctxMap);
        }
        HeaderParser hdrParser = eng.getHeaderParser();
        MessageParser msgParser = eng.getMessageParser();
        Set<Entry<String, Map<String, String>>> compMems = msgParser.getMessageMap().entrySet();
        Iterator<Entry<String, Map<String, String>>> memSetIterator = compMems.iterator();
        while (memSetIterator.hasNext()) {
            Entry<String, Map<String, String>> ctxEntry = memSetIterator.next();
            String msgType = ctxEntry.getKey();
            LinkedHashMap<String, String> ctxOrderMap =
                (LinkedHashMap<String, String>) ctxEntry.getValue();
            LinkedHashMap<String, String> ctxHdrMap = hdrParser.getContextMap();
            ctxMap.addAll(msgType, ctxHdrMap);;
            ctxMap.addAll(msgType, ctxOrderMap);
        }
        // Here we collect all repeating groups found in each message type.
        // Maintain a map of message type to each repeating group found in the
        // message type. Repeating groups list only their member fields, not
        // nested fields. Nested fields are found in their own repeating group
        // type.
        RepeatingGroupBuilderMap grpMap = groupMap.get(fVersion);
        if (grpMap == null) {
            grpMap = new RepeatingGroupBuilderMap();
            groupMap.put(fVersion, grpMap);
        }
        DataStore dataStore = eng.getMessageParser().getFIXDataStore();
        Set<String> msgTypes = dataStore.getRepeatingGroupMsgTypes();
        Iterator<String> msgTypesIT = msgTypes.iterator();
        while (msgTypesIT.hasNext()) {
            String msgType = msgTypesIT.next();
            Map<String, RepeatingGroupBuilder> gMap = dataStore.getGroupsInMessage(msgType);
            grpMap.addAll(msgType, gMap);
        }
    }

    private void parseFIX5(String f5Version) throws Exception {
        if (!orderMap.containsKey("FIXT.1.1")) {
            parseFIX("FIXT.1.1");
        }
        ContextOrderMap fixTMap = orderMap.get("FIXT.1.1");
        ContextOrderMap ctxMap = new ContextOrderMap();
        orderMap.put(f5Version, ctxMap);
        ctxMap.addAll(fixTMap);
        parseFIX(f5Version);
    }

    private void parseFIX4(String f4Version) throws Exception {
        parseFIX(f4Version);
    }

    public void parseFIX40() throws Exception {
        parseFIX4("FIX.4.0");
    }

    public void parseFIX41() throws Exception {
        parseFIX4("FIX.4.1");
    }

    public void parseFIX42() throws Exception {
        parseFIX4("FIX.4.2");
    }

    public void parseFIX43() throws Exception {
        parseFIX4("FIX.4.3");
    }

    public void parseFIX44() throws Exception {
        parseFIX4("FIX.4.4");
    }

    public void parseFIX50() throws Exception {
        parseFIX5("FIX.5.0");
    }

    public void parseFIX50SP1() throws Exception {
        parseFIX5("FIX.5.0SP1");
    }

    public void parseFIX50SP2() throws Exception {
        parseFIX5("FIX.5.0SP2");
    }

    public void clear() {
        orderMap.clear();
    }

    /**
     * Testing only
     *
     * @param args
     */
    public static void main(String[] args) {
        DataGenerator dataGen = new DataGenerator();
        try {
            dataGen.parseFIX50SP2();
            FieldOrderMapCodeGenerator fieldGen =
                new FieldOrderMapCodeGenerator("FIX.5.0SP2", dataGen);
            fieldGen.generateClass();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
