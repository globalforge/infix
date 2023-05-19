package com.globalforge.infix.qfix;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
 * Consolidates all the data from the different FIX xml parsers and assembles it
 * into a common form understood by the CodeGenerator.
 * @author Michael C. Starkie
 */
public class DataGenerator {
    /** field contexts to order maps for all message types */
    private Map<String, ContextOrderMap> orderMap = new HashMap<String, ContextOrderMap>();
    /** repeating group information for all message types */
    private Map<String, RepeatingGroupBuilderMap> groupMap =
        new HashMap<String, RepeatingGroupBuilderMap>();

    /**
     * Get the field contexts to field order map for all messages types within a
     * specified FIX version.
     * @param fVersion The fix version
     * @return ContextOrderMap field context to field order
     */
    public ContextOrderMap getContextOrderMap(String fVersion) {
        return orderMap.get(fVersion);
    }

    /**
     * Get the repeating groups for all message types given a FIX version.
     * @param fVersion The FIX version
     * @return RepeatingGroupBuilderMap map of all repeating groups for each
     * message type.
     */
    public RepeatingGroupBuilderMap getRepeatingGroupMap(String fVersion) {
        return groupMap.get(fVersion);
    }

    /**
     * Parse a FIX file and generate all the data.
     * @param fVersion The FIX version
     * @throws Exception Some bad thing happened.
     */
    private DataDictionaryParser parseFIX(String fVersion) throws Exception {
        return parseFIX(fVersion, null);
    }

    /**
     * Parse a FIX file and generate all the data for a custom FIX data
     * dictionary file.
     * @param fVersion The name of the custom FIX version
     * @param basedOnVer Must be based on a well known FIX version (usually
     * FIX44 or FIX50).
     * @throws Exception Some bad thing happened.
     */
    private DataDictionaryParser parseFIX(String fVersion, String basedOnVer) throws Exception {
        // Parse all the xml data into objects we can manage.
        DataDictionaryParser eng = null;
        if (basedOnVer == null) {
            eng = new DataDictionaryParser(fVersion);
        } else {
            eng = new DataDictionaryParser(fVersion, basedOnVer);
        }
        eng.parseFields();
        eng.parseComponents();
        eng.parseHeader();
        eng.parseMessages();
        // Here we collect all mappings of infix contexts to their relative
        // order for each message type and stuff it into a
        // QuickFixContextOrderMap. We also prepend the header field contexts to
        // the set of fields in each message type. The trailer contexts are
        // post-pended by hard-code because they are the same in each FIX
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
        
        return eng;
    }

    /**
     * Parse a fix version which is a flavor of FIX50
     * @param f5Version some version in FIX50 format.
     * @throws Exception Some bad thing happened.
     */
    public DataDictionaryParser parseFIX5(String f5Version) throws Exception {
        if (!orderMap.containsKey("FIXT.1.1")) {
            parseFIX("FIXT.1.1");
        }
        ContextOrderMap fixTMap = orderMap.get("FIXT.1.1");
        ContextOrderMap ctxMap = new ContextOrderMap();
        orderMap.put(f5Version, ctxMap);
        ctxMap.addAll(fixTMap);
        return parseFIX(f5Version);
    }

    /**
     * Parse a custom FIX version. Must contain FIX4 or FIX5 in the name and be
     * based on one of those standard quick fix file formats.
     * @param fixVersion The custom FIX version name.
     * @throws Exception Some bad thing happened.
     */
    public DataDictionaryParser parseCustom(String fixVersion) throws Exception {
        if (fixVersion.startsWith("FIX4")) {
            return parseFIX4Custom(fixVersion);
        } else if (fixVersion.startsWith("FIX5")) {
            return parseFIX5Custom(fixVersion);
        } else {
            throw new RuntimeException(
                "Data Dict XML filename must start with either FIX4 or FIX5!");
        }
    }

    /**
     * Parse a custom quick fix data dictionary based on FIX4
     * @param f4Version The custom fix version
     * @throws Exception Some bad thing happened.
     */
    public DataDictionaryParser parseFIX4Custom(String f4Version) throws Exception {
        return parseFIX(f4Version, "FIX.4.4");
    }

    /**
     * Parse a flavor of FIX4
     * @param f4Version the FIX 4 version
     * @throws Exception Some bad thing happened.
     */
    public DataDictionaryParser parseFIX4(String f4Version) throws Exception {
        return parseFIX(f4Version);
    }

    /**
     * Parse FIX 4.0 data dictionary
     * @throws Exception Some bad thing happened.
     */
    public DataDictionaryParser parseFIX40() throws Exception {
        return parseFIX4("FIX.4.0");
    }

    /**
     * Parse FIX 4.1 data dictionary
     * @throws Exception Some bad thing happened.
     */
    public DataDictionaryParser parseFIX41() throws Exception {
        return parseFIX4("FIX.4.1");
    }

    /**
     * Parse FIX 4.2 data dictionary
     * @throws Exception Some bad thing happened.
     */
    public DataDictionaryParser parseFIX42() throws Exception {
        return parseFIX4("FIX.4.2");
    }

    /**
     * Parse FIX 4.3 data dictionary
     * @throws Exception Some bad thing happened.
     */
    public DataDictionaryParser parseFIX43() throws Exception {
        return parseFIX4("FIX.4.3");
    }

    /**
     * Parse FIX 4.4 data dictionary
     * @throws Exception Some bad thing happened.
     */
    public DataDictionaryParser parseFIX44() throws Exception {
        return parseFIX4("FIX.4.4");
    }

    /**
     * Parse FIX 5.0 data dictionary
     * @throws Exception Some bad thing happened.
     */
    public DataDictionaryParser parseFIX50() throws Exception {
        return parseFIX5("FIX.5.0");
    }

    /**
     * Parse FIX 5.0SP1 data dictionary
     * @throws Exception Some bad thing happened.
     */
    public DataDictionaryParser parseFIX50SP1() throws Exception {
        return parseFIX5("FIX.5.0SP1");
    }

    /**
     * Parse FIX 5.0SP2 data dictionary
     * @throws Exception Some bad thing happened.
     */
    public DataDictionaryParser parseFIX50SP2() throws Exception {
        return parseFIX5("FIX.5.0SP2");
    }

    /**
     * Parse a custom quick fix data dictionary based on FIX5
     * @param f5Version The custom fix version
     * @throws Exception Some bad thing happened.
     */
    public DataDictionaryParser parseFIX5Custom(String f5Version) throws Exception {
        return parseFIX(f5Version, "FIX.5.0");
    }

    /**
     * Clean up gracefully.
     */
    public void clear() {
        orderMap.clear();
        groupMap.clear();
    }

    /**
     * Testing only
     * @param args program arguments.
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
