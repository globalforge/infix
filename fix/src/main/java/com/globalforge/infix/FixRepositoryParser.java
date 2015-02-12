package com.globalforge.infix;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * The first stage in the infix application. This class produces xml from the
 * fix repository. It should only be run once for each fix version in which a
 * rule parser is needed. Function: Parses the Fix Repository as defined by the
 * Fix Protocol Committee and produces an xml file of all the repeating groups
 * and the message types that may contain them. This program requires an
 * argument specifying the fix version to parse. You must understand the
 * terminology used in this program to understand the code. A repeating group is
 * defined as having tags that belong to 1 of 3 categories. A unique id that
 * defines the group, A tag designated as the delimiter tag which is officially
 * the first tag in the group and finally a set of member tags. The id is that
 * that preceeds the repating group and used to determine how many repeating
 * groups follow. For example NoContraGrps (382) is the designated id for the
 * ContraGrp repeating group. Below is an example: <code> 
 *  ContraGroup
 *     Id : 382   (NoContraGrps)
 *     delim: 375 (ContraGrp)
 *     member: 655
 *     member: 337
 *     member: 437
 *     member: 438
 * </code> Because of the large amounts of data contained in FixRepository.xml
 * the program must make several pases in order to collect all data. The general
 * workflow is to parse group names, then blocks, followed by groups, and lastly
 * message. Each of these tasks parses the entire file.
 * 
 * @author Michael C. Starkie
 */
public class FixRepositoryParser {
    /** logger */
    final static Logger logger = LoggerFactory
        .getLogger(FixRepositoryParser.class);
    private final XMLInputFactory factory = XMLInputFactory.newInstance();
    private PrintStream out = null;
    private boolean isMyCtx = false;
    private String curComponent = null;
    private String curName = null;
    /** components defined as blocks in the fix spec */
    private Map<String, FixBlock> fixBlocks = null;
    /** components defined as repeating groups in the fix spec */
    private Map<String, FixGroup> fixGroups = null;
    private Set<String> fixGroupNames = new HashSet<String>();
    private Map<String, Set<String>> msgMap =
        new TreeMap<String, Set<String>>();
    private Set<String> groupChecklist = new HashSet<String>();
    private Set<String> noHopVersions = Collections
        .unmodifiableSet(new HashSet<String>(Arrays.asList("FIX.4.0",
            "FIX.4.1", "FIX.4.2")));

    public FixRepositoryParser() throws Exception {
    }

    /**
     * Make sure we know where to find the FixRepository.xml file and the
     * location on the filesystem where the user has specified the location of
     * the output files.
     * 
     * @param ver The fix version we are parsing.
     * @throws Exception When external dependencies are not recognized.
     */
    private void setUp(String ver) throws Exception {
        String CONFIG_DIR = System.getenv("CONFIG_DIR");
        if (CONFIG_DIR != null) {
            FixRepositoryParser.logger.info(
                "CONFIG_DIR is an ENV variable: {}", CONFIG_DIR);
        } else {
            CONFIG_DIR = System.getProperty("CONFIG_DIR");
            if (CONFIG_DIR != null) {
                FixRepositoryParser.logger.info(
                    "CONFIG_DIR is a System property: {}", CONFIG_DIR);
            } else {
                CONFIG_DIR = null;
            }
        }
        if (CONFIG_DIR == null) {
            FixRepositoryParser.logger
                .warn("No CONFIG_DIR provided.  Output stream is CONSOLE");
            out = System.out;
        } else {
            File fixOut =
                new File(CONFIG_DIR + System.getProperty("file.separator")
                    + ver + "Mgr.xml");
            out = new PrintStream(fixOut, "UTF-8");
        }
        fixBlocks = new HashMap<String, FixBlock>();
        fixGroups = new TreeMap<String, FixGroup>();
        groupChecklist.clear();
        /*
         * + This hack is needed because the FixRepository is inconsistent in
         * regards to the definition of a Hop. It's supposed to be part of the
         * Standard Header in all versions of Fix starting with FIX.4.3 but it's
         * defined differently in each version of the xml file. So just ignore
         * it altogether and add it explicityly at the end when the xml is
         * generated.
         */
        groupChecklist.add("HopGrp");
    }

    /**
     * Collects the names of all repeating groups.
     * 
     * @param v The fix version to parse.
     * @throws XMLStreamException A corrupted FixRepository.xml file is found.
     */
    public void parseGroupNames(String v) throws XMLStreamException {
        XMLStreamReader reader =
            factory.createXMLStreamReader(ClassLoader
                .getSystemResourceAsStream("FixRepository.xml"));
        String fixVersion = v;
        while (reader.hasNext()) {
            int event = reader.next();
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    String elementName = reader.getLocalName();
                    if ("fix".equals(elementName)) {
                        fixVersion = reader.getAttributeValue(0);
                        if (fixVersion.equals(v)) {
                            FixRepositoryParser.logger.info(
                                "parsing group names for fix version: {}",
                                fixVersion);
                        }
                    }
                    if (!fixVersion.equals(v)) {
                        continue;
                    }
                    if ("component".equals(elementName)) {
                        curComponent = reader.getAttributeValue(0);
                        String repeating =
                            reader.getAttributeValue(null, "repeating");
                        if ((repeating != null) && repeating.equals("1")) {
                            isMyCtx = true;
                        }
                    } else if ("repeatingGroup".equals(elementName)
                        && (curComponent != null) && isMyCtx) {
                        fixGroupNames.add(curComponent);
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    elementName = reader.getLocalName();
                    if ("component".equals(elementName)) {
                        curComponent = null;
                        isMyCtx = false;
                    }
                    break;
                default:
            }
        }
    }

    /**
     * Collect all the block data. Remove any references to repeating groups.
     * The set of members should only contain tag numbers that belong to the
     * block and not to any nested repeating group. Collect the repeating groups
     * later and add in members collected here for any non-repeating block
     * references.
     * 
     * @param v The fix version we are parsing.
     * @throws XMLStreamException
     */
    public void parseBlocks(String v) throws XMLStreamException {
        XMLStreamReader reader =
            factory.createXMLStreamReader(ClassLoader
                .getSystemResourceAsStream("FixRepository.xml"));
        String fixVersion = v;
        while (reader.hasNext()) {
            int event = reader.next();
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    String elementName = reader.getLocalName();
                    if ("fix".equals(elementName)) {
                        fixVersion = reader.getAttributeValue(0);
                        if (fixVersion.equals(v)) {
                            FixRepositoryParser.logger.info(
                                "parsing blocks fix version: {}", fixVersion);
                        }
                    }
                    if (!fixVersion.equals(v)) {
                        continue;
                    }
                    if ("component".equals(elementName)) {
                        String type = reader.getAttributeValue(null, "type");
                        if (type.endsWith("Block")) {
                            curName = reader.getAttributeValue(0);
                            FixBlock b = null;
                            if (fixGroupNames.contains(curName)) {
                                FixRepositoryParser.logger
                                    .error(
                                        "Block already defined as a repeating group: {}"
                                            + ". This is an error in the fix repository.",
                                        curName);
                                continue;
                            }
                            // System.out.println("found block: " + curName);
                            b = new FixBlock();
                            b.name = curName;
                            fixBlocks.put(curName, b);
                            isMyCtx = true;
                        }
                    } else if ("fieldRef".equals(elementName) && isMyCtx) {
                        String id = reader.getAttributeValue(0);
                        FixBlock b = fixBlocks.get(curName);
                        b.addMember(id);
                    } else if ("componentRef".equals(elementName) && isMyCtx) {
                        String refName = reader.getAttributeValue(null, "name");
                        // replace reference with member fields if already
                        // parsed.
                        if (fixBlocks.containsKey(refName)) {
                            FixBlock g = fixBlocks.get(refName);
                            FixBlock b = fixBlocks.get(curName);
                            b.addMembers(g.getMembers());
                        } else {
                            // There is a repeating group nested in the block
                            // Store the group name and fill in the members
                            // later.
                            // Only keep track of tags that can repeat.
                            if (fixGroupNames.contains(refName)) {
                                FixBlock b = fixBlocks.get(curName);
                                b.addGrpReference(refName);
                                // System.out.println("adding group ref: "
                                // + refName + " to block " + curName);
                            } else {
                                // If the current block has a pointer to another
                                // block
                                // that we haven't parsed yet. Store the name of
                                // the block.
                                // in the member set instead of the id and
                                // replace it later.
                                // After parsing blocks, we can check to see if
                                // any members are also
                                // keys in fixBlocks. That is how we can replace
                                // names with fields.
                                FixBlock b = fixBlocks.get(curName);
                                b.addMember(refName);
                            }
                        }
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    elementName = reader.getLocalName();
                    if ("fix".equals(elementName)) {
                        if (!fixVersion.equals(v)) {
                            continue;
                        }
                        checkBlockData();
                    } else if ("component".equals(elementName)) {
                        curName = null;
                        isMyCtx = false;
                    }
                    break;
                default:
            }
        }
    }

    /**
     * Runs through the block data collected and checks it's integrtity.
     */
    public void checkBlockData() {
        Iterator<Entry<String, FixBlock>> it = fixBlocks.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, FixBlock> e = it.next();
            FixBlock b = e.getValue();
            String[] members =
                b.getMembers().toArray(new String[b.getMembers().size()]);
            for (String mem : members) {
                if (fixBlocks.containsKey(mem)) {
                    // remove any placeholders and replace with fields.
                    b.removeMember(mem);
                    b.getMembers().addAll(fixBlocks.get(mem).getMembers());
                }
            }
            members = b.getMembers().toArray(new String[b.getMembers().size()]);
            for (String mem : members) {
                try {
                    // make sure members contain only numbers
                    Integer.parseInt(mem);
                } catch (NumberFormatException ex) {
                    String m =
                        "ABORT: Unresolved block name.  Need a thrid pass to resolve: "
                            + mem;
                    throw new RuntimeException(m, ex);
                }
            }
        }
        it = fixBlocks.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, FixBlock> e = it.next();
            FixBlock b = e.getValue();
            String[] members =
                b.getMembers().toArray(new String[b.getMembers().size()]);
            // remove any blocks that are empty (contained only repeating group
            // references)
            if (members.length == 0) {
                it.remove();
                continue;
            }
            // System.out.println("[blocks] key: " + e.getKey() + ", value: " +
            // b);
        }
    }

    /**
     * debug helper
     */
    public void printGroupNames() {
        Iterator<String> it = fixGroupNames.iterator();
        while (it.hasNext()) {
            String e = it.next();
            FixRepositoryParser.logger.info("found repeating group: {}", e);
        }
    }

    /**
     * Parse the fix repository and collect all the repeating groups.
     * Consolidates all tags that may be found in a repeating group including
     * references to blocks and other repeating groups. Does not include tags in
     * nested repeating groups as those tags will be consolidated under the
     * group they belong to.
     * 
     * @param v The fix version we are to parse.
     * @throws XMLStreamException XML file is corrupt.
     */
    public void parseGroups(String v) throws XMLStreamException {
        XMLStreamReader reader =
            factory.createXMLStreamReader(ClassLoader
                .getSystemResourceAsStream("FixRepository.xml"));
        String fixVersion = v;
        while (reader.hasNext()) {
            int event = reader.next();
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    String elementName = reader.getLocalName();
                    if ("fix".equals(elementName)) {
                        fixVersion = reader.getAttributeValue(0);
                        if (fixVersion.equals(v)) {
                            FixRepositoryParser.logger.info(
                                "parsing groups fix version: {}", fixVersion);
                        }
                    }
                    if (!fixVersion.equals(v)) {
                        continue;
                    }
                    if ("component".equals(elementName)) {
                        String type = reader.getAttributeValue(null, "type");
                        if (type.endsWith("Repeating")) {
                            curName = reader.getAttributeValue(0);
                            if (!fixGroupNames.contains(curName)) {
                                FixRepositoryParser.logger
                                    .error(
                                        "Group has no repeatingGroup element.  Error in Fix repository.  Ignoring: {}",
                                        curName);
                                continue;
                            }
                            // System.out.println("found block: " + curBlock);
                            FixGroup g = new FixGroup();
                            g.name = curName;
                            fixGroups.put(curName, g);
                            isMyCtx = true;
                        }
                    } else if ("repeatingGroup".equals(elementName) && isMyCtx) {
                        String id = reader.getAttributeValue(0);
                        FixGroup g = fixGroups.get(curName);
                        g.setId(id);
                    } else if ("fieldRef".equals(elementName) && isMyCtx) {
                        String id = reader.getAttributeValue(0);
                        FixGroup g = fixGroups.get(curName);
                        g.addMember(id);
                    } else if ("componentRef".equals(elementName) && isMyCtx) {
                        String refName = reader.getAttributeValue(null, "name");
                        // replace reference with member fields if already
                        // parsed.
                        if (fixBlocks.containsKey(refName)) {
                            FixBlock block = fixBlocks.get(refName);
                            FixGroup curGroup = fixGroups.get(curName);
                            // if repeating group contain references to blocks
                            // we
                            // need to store the block fields as repeating
                            // members.
                            curGroup.addMembers(block.getMembers());
                            List<String> refs = block.getGroupReferences();
                            if (!refs.isEmpty()) {
                                // if references to a repeating group were found
                                // when parsing a block found within a repeating
                                // group
                                // we need to add those references to the
                                // current group.
                                // we do this because when we list which groups
                                // belong
                                // in a msgType we can find all of them in
                                // fixGroups.
                                curGroup.addGrpReferences(block
                                    .getGroupReferences());
                            }
                        } else {
                            if (fixGroupNames.contains(refName)) {
                                FixGroup curGroup = fixGroups.get(curName);
                                curGroup.addGrpReference(refName);
                            }
                        }
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    elementName = reader.getLocalName();
                    if ("fix".equals(elementName)) {
                        if (!fixVersion.equals(v)) {
                            continue;
                        }
                        checkGroupData();
                    } else if ("component".equals(elementName)) {
                        curName = null;
                        isMyCtx = false;
                    }
                    break;
                default:
            }
        }
    }

    /*-
    public void parseNestedGroups(String v) throws XMLStreamException {
        Iterator<FixGroup> grpIt = fixGroups.values().iterator();
        while (grpIt.hasNext()) {
            FixGroup grp = grpIt.next();
            List<String> grpRefs = grp.getGroupReferences();
            Iterator<String> refIt = grpRefs.iterator();
            while (refIt.hasNext()) {
                String grpRef = refIt.next();
                FixGroup g = fixGroups.get(grpRef);
                grp.addMember(g.getId());
            }
        }
    }
     */
    /**
     * Checks the integrety of the groups after they are parsed.
     */
    public void checkGroupData() {
        Iterator<Entry<String, FixGroup>> it = fixGroups.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, FixGroup> e = it.next();
            FixGroup b = e.getValue();
            String[] members =
                b.getMembers().toArray(new String[b.getMembers().size()]);
            for (String mem : members) {
                try {
                    // make sure members contain only numbers
                    Integer.parseInt(mem);
                } catch (NumberFormatException ex) {
                    String m =
                        "ABORT: Unresolved group name.  Need a thrid pass to resolve: "
                            + mem;
                    throw new RuntimeException(m, ex);
                }
            }
        }
        it = fixGroups.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, FixGroup> e = it.next();
            FixGroup b = e.getValue();
            String[] members =
                b.getMembers().toArray(new String[b.getMembers().size()]);
            // remove any blocks that are empty (contained only repeating group
            // references)
            if (members.length == 0) {
                it.remove();
                continue;
            }
            // System.out.println("[groups] key: " + e.getKey() + ", value: " +
            // b);
        }
    }

    /**
     * Parses the fix repository and associates all possible repeating groups
     * that may legally found in each Message Type. Associations include
     * repeating groups, references to repeating groups within other repeating
     * groups and block.
     * 
     * @param v The fix version we should parse
     * @throws XMLStreamException XML file is corrupted.
     */
    public void parseMessages(String v) throws XMLStreamException {
        XMLStreamReader reader =
            factory.createXMLStreamReader(ClassLoader
                .getSystemResourceAsStream("FixRepository.xml"));
        String fixVersion = v;
        while (reader.hasNext()) {
            int event = reader.next();
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    String elementName = reader.getLocalName();
                    if ("fix".equals(elementName)) {
                        fixVersion = reader.getAttributeValue(0);
                        if (fixVersion.equals(v)) {
                            FixRepositoryParser.logger.info(
                                "parsing messages for fix version: {}",
                                fixVersion);
                        }
                    }
                    if (!fixVersion.equals(v)) {
                        continue;
                    }
                    if ("message".equals(elementName)) {
                        curName = reader.getAttributeValue(null, "msgType");
                        isMyCtx = true;
                    } else if ("componentRef".equals(elementName) && isMyCtx) {
                        String refName = reader.getAttributeValue(null, "name");
                        if (fixGroups.containsKey(refName)) {
                            Set<String> grps = msgMap.get(curName);
                            if (grps == null) {
                                grps = new HashSet<String>();
                                msgMap.put(curName, grps);
                            }
                            grps.add(refName);
                            List<String> refs =
                                fixGroups.get(refName).getGroupReferences();
                            if ((refs != null) && (refs.size() > 0)) {
                                grps.addAll(refs);
                            }
                        } else if (fixBlocks.containsKey(refName)) {
                            Set<String> grps = msgMap.get(curName);
                            if (grps == null) {
                                grps = new HashSet<String>();
                                msgMap.put(curName, grps);
                            }
                            FixBlock b = fixBlocks.get(refName);
                            List<String> refs = b.getGroupReferences();
                            Iterator<String> it = refs.iterator();
                            while (it.hasNext()) {
                                String ref = it.next();
                                FixGroup g = fixGroups.get(ref);
                                // This hack is needed because the fix 4.4
                                // repository
                                // defines Hop as both a block and a repeating
                                // group.
                                // This is an XML bug in the repository.
                                // In Fix 5.0 HopGrp is defined as ImplicitBlock
                                // yet is has an elementName "repeating"
                                if ((g == null) && !ref.equals("Hop")
                                    && !ref.equals("HopGrp")) {
                                    String m =
                                        "ABORT.  Missing group in parseMessages: "
                                            + ref;
                                    throw new RuntimeException(m);
                                }
                                // this can only be false if ref = Hop
                                if (g != null) {
                                    grps.add(g.name);
                                }
                            }
                        }
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    elementName = reader.getLocalName();
                    if ("fix".equals(elementName)) {
                        if (!fixVersion.equals(v)) {
                            continue;
                        }
                        // printMsgInfo();
                        printDictionary(fixVersion);
                    } else if ("message".equals(elementName)) {
                        curName = null;
                        isMyCtx = false;
                    }
                    break;
                default:
            }
        }
    }

    /**
     * print info on a group for debug purposes.
     * 
     * @param group The group to print.
     */
    public void printGroupInfo(FixGroup group) {
        FixRepositoryParser.logger.info("\t" + group);
        List<String> refs = group.getGroupReferences();
        Iterator<String> it = refs.iterator();
        while (it.hasNext()) {
            String grpName = it.next();
            if (fixGroups.containsKey(grpName)) {
                FixGroup g = fixGroups.get(grpName);
                printGroupInfo(g);
            }
        }
    }

    public void printHops() {
        out.println("\t<Group name=\"HopGrp\">");
        out.println("\t\t<Id>627</Id>");
        out.println("\t\t<Delim>628</Delim>");
        out.println("\t\t<Member>629</Member>");
        out.println("\t\t<Member>630</Member>");
        out.println("\t</Group>");
    }

    /**
     * Prints out the group information for a repating group in XML format. List
     * all repeating groups by name and include the id which defines the group,
     * it's delimiter and all member tags.
     * 
     * @param group The group to print.
     */
    public void printGroupXMLInfo(FixGroup group) {
        if (groupChecklist.contains(group.name)) { return; }
        groupChecklist.add(group.name);
        out.println("\t<Group name=\"" + group.name + "\">");
        String[] members =
            group.getMembers().toArray(new String[group.getMembers().size()]);
        for (int i = 0; i < members.length; i++) {
            String val = members[i];
            if (i == 0) {
                out.println("\t\t<Id>" + val + "</Id>");
                continue;
            }
            if (i == 1) {
                out.println("\t\t<Delim>" + val + "</Delim>");
                continue;
            }
            out.println("\t\t<Member>" + val + "</Member>");
        }
        String[] ref_arr =
            group.getGroupReferences().toArray(
                new String[group.getGroupReferences().size()]);
        for (String val : ref_arr) {
            FixGroup g = fixGroups.get(val);
            out.println("\t\t<GrpRefID>" + g.getId() + "</GrpRefID>");
        }
        out.println("\t</Group>");
        List<String> refs = group.getGroupReferences();
        Iterator<String> it = refs.iterator();
        while (it.hasNext()) {
            String grpName = it.next();
            if (fixGroups.containsKey(grpName)) {
                FixGroup g = fixGroups.get(grpName);
                printGroupXMLInfo(g);
            } else {
                String m = "ABORT. Unknown group reference. group=" + grpName;
                throw new RuntimeException(m);
            }
        }
    }

    /**
     * Dump message info for debug purposes
     */
    public void printMsgInfo() {
        Iterator<Entry<String, Set<String>>> it = msgMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Set<String>> e = it.next();
            String msgType = e.getKey();
            if (!msgType.equals("8")) {
                continue;
            }
            FixRepositoryParser.logger.info("### MsgType: {} ###", msgType);
            Set<String> g = e.getValue();
            Iterator<String> git = g.iterator();
            while (git.hasNext()) {
                String ge = git.next();
                FixRepositoryParser.logger.info("\t" + ge);
            }
        }
    }

    private void collectGrpReferences(Set<String> grps, String refName) {
        List<String> refs = fixGroups.get(refName).getGroupReferences();
        Iterator<String> git = refs.iterator();
        while (git.hasNext()) {
            String ge = git.next();
            if (grps.contains(ge)) {
                continue;
            }
            grps.add(ge);
            collectGrpReferences(grps, ge);
        }
    }

    /**
     * Print the MsgType format of the output xml. Lists each message type along
     * with the names of all the repeating groups that may be found within that
     * message type.
     * 
     * @param msgType The message type to print.
     * @param fixVersion The current fix version we are printing msg info for.
     */
    public void printMsgXMLInfo(String msgType, String fixVersion) {
        if (msgType.equals("8")) {
            System.out.println("ExecutionReport");
        }
        out.println("\t<MsgType id=\"" + msgType + "\">");
        out.println("\t\t<Groups>");
        if (!noHopVersions.contains(fixVersion)) {
            out.println("\t\t\t<Name>HopGrp</Name>");
        }
        Set<String> grpNames = msgMap.get(msgType);
        Set<String> grpRefs = new HashSet<String>();
        Iterator<String> it = grpNames.iterator();
        while (it.hasNext()) {
            String grpName = it.next();
            grpRefs.add(grpName);
            collectGrpReferences(grpRefs, grpName);
        }
        Iterator<String> refIt = grpRefs.iterator();
        while (refIt.hasNext()) {
            String grpName = refIt.next();
            if (!grpName.equals("HopGrp")) {
                out.println("\t\t\t<Name>" + grpName + "</Name>");
            }
        }
        out.println("\t\t</Groups>");
        out.println("\t</MsgType>");
    }

    /**
     * Begins the process of generating the programs XML output.
     * 
     * @param curFixVersion The fix version being printed.
     */
    public void printDictionary(String curFixVersion) {
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<!-- " + curFixVersion + " data dictionary -->");
        out.println("<Body version=\"" + curFixVersion + "\">");
        Iterator<Entry<String, FixGroup>> it = fixGroups.entrySet().iterator();
        if (!noHopVersions.contains(curFixVersion)) {
            printHops();
        }
        while (it.hasNext()) {
            Entry<String, FixGroup> e = it.next();
            FixGroup g = e.getValue();
            printGroupXMLInfo(g);
        }
        Iterator<Entry<String, Set<String>>> msgIt =
            msgMap.entrySet().iterator();
        while (msgIt.hasNext()) {
            Entry<String, Set<String>> e = msgIt.next();
            String msgId = e.getKey();
            printMsgXMLInfo(msgId, curFixVersion);
        }
        out.println("</Body>");
    }

    /**
     * It all begins here.
     * 
     * @param args The fix version must be in args[0].
     */
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                FixRepositoryParser.logger
                    .error("Must provide an args[0] from the following set:");
                FixRepositoryParser.logger.error("FIX.4.0");
                FixRepositoryParser.logger.error("FIX.4.1");
                FixRepositoryParser.logger.error("FIX.4.2");
                FixRepositoryParser.logger.error("FIX.4.3");
                FixRepositoryParser.logger.error("FIX.4.4");
                FixRepositoryParser.logger.error("FIX.5.0");
                FixRepositoryParser.logger.error("FIX.5.0SP1");
                FixRepositoryParser.logger.error("FIX.5.0SP2");
                FixRepositoryParser.logger.error("FIXT.1.1");
            } else {
                String fixVersion = args[0];
                FixRepositoryParser eng = new FixRepositoryParser();
                eng.setUp(fixVersion);
                eng.parseGroupNames(fixVersion);
                eng.parseBlocks(fixVersion);
                eng.parseGroups(fixVersion);
                // eng.parseNestedGroups(fixVersion);
                eng.parseMessages(fixVersion);
                FixRepositoryParser.logger.info(
                    "\nParsing of fix version {} completed successfully.",
                    fixVersion);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            e.getCause().printStackTrace();
        }
    }

    public static class FixBlock {
        protected String name = null;
        protected final LinkedList<String> members = new LinkedList<String>();
        protected final LinkedList<String> grpRef = new LinkedList<String>();

        public void addMember(String member) {
            members.add(member);
        }

        public void addMembers(List<String> mems) {
            members.addAll(mems);
        }

        public void addGrpReference(String grpName) {
            grpRef.add(grpName);
        }

        public void addGrpReferences(List<String> refs) {
            grpRef.addAll(refs);
        }

        public List<String> getMembers() {
            return members;
        }

        public List<String> getGroupReferences() {
            return grpRef;
        }

        public void removeMember(String member) {
            members.remove(member);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("name = [" + name + "], ");
            sb.append("members = [");
            Iterator<String> it = members.iterator();
            while (it.hasNext()) {
                String m = it.next();
                sb.append(m);
                if (it.hasNext()) {
                    sb.append("|");
                }
            }
            sb.append("], ");
            sb.append("groupRefs = [");
            it = grpRef.iterator();
            while (it.hasNext()) {
                String m = it.next();
                sb.append(m);
                if (it.hasNext()) {
                    sb.append("|");
                }
            }
            sb.append("]");
            return sb.toString();
        }
    }

    public static class FixGroup extends FixBlock {
        protected String id = null;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
            addMember(id);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("name = [" + name + "], ");
            sb.append("id = [" + id + "], ");
            sb.append("members = [");
            Iterator<String> it = members.iterator();
            while (it.hasNext()) {
                String m = it.next();
                sb.append(m);
                if (it.hasNext()) {
                    sb.append("|");
                }
            }
            sb.append("], ");
            sb.append("groups = [");
            it = grpRef.iterator();
            while (it.hasNext()) {
                String m = it.next();
                sb.append(m);
                if (it.hasNext()) {
                    sb.append("|");
                }
            }
            sb.append("]");
            return sb.toString();
        }
    }
}
