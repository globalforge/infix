package com.globalforge.infix.qfix;

import java.io.File;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupManagerCodeGenerator {
    /** logger */
    private final static Logger logger = LoggerFactory
        .getLogger(GroupManagerCodeGenerator.class);
    private String fileNamePrefix = null;
    private final String afixVer;
    private String qfixverLowerCase = null;
    private PrintStream out = null;
    private final RepeatingGroupBuilderMap repeatingGrpMap;

    public GroupManagerCodeGenerator(String fVer, DataGenerator d) {
        this.afixVer = fVer.replace(".", "");
        this.repeatingGrpMap = d.getRepeatingGroupMap(fVer);
    }

    public void generateClass() throws Exception {
        initOutputStreams();
        handleStartClass();
        handleConstructor();
        handleInitGroups();
        handleDefineGroups();
        finish();
    }

    private void initOutputStreams() throws Exception {
        String SRC_DIR = System.getenv("SRC_DIR");
        if (SRC_DIR != null) {
            logger.info("SRC_DIR is an ENV variable: {}", SRC_DIR);
        } else {
            SRC_DIR = System.getProperty("SRC_DIR");
            if (SRC_DIR != null) {
                logger.info("SRC_DIR is a System property: {}", SRC_DIR);
            } else {
                SRC_DIR = null;
            }
        }
        if (SRC_DIR == null) {
            logger.warn("No SRC_DIR provided.  Output stream is CONSOLE");
            out = System.out;
        } else {
            fileNamePrefix = afixVer + "GroupMgr";
            qfixverLowerCase = afixVer.toLowerCase();
            File fOut = new File(SRC_DIR + System.getProperty("file.separator")
                + qfixverLowerCase + System.getProperty("file.separator")
                + fileNamePrefix + ".java");
            logger.info("building java file: {}", fOut.getAbsolutePath());
            out = new PrintStream(fOut, "UTF-8");
        }
    }

    private void handleConstructor() {
        ////
        Set<Entry<String, Map<String, RepeatingGroupBuilder>>> msgGroups = null;
        Iterator<Entry<String, Map<String, RepeatingGroupBuilder>>> memSetIterator = null;
        msgGroups = repeatingGrpMap.getGroupMap().entrySet();
        memSetIterator = msgGroups.iterator();
        while (memSetIterator.hasNext()) {
            Entry<String, Map<String, RepeatingGroupBuilder>> ctxEntry = memSetIterator
                .next();
            String msgType = ctxEntry.getKey();
            if ("HEADER".equals(msgType)) {
                continue;
            }
            out.println("\t\tinitMessageType_" + msgType + "();");
        }
        ///
        out.println("\t}");
    }

    private void handleInitGroups() {
        Set<Entry<String, Map<String, RepeatingGroupBuilder>>> groupIDEntrySet = null;
        Iterator<Entry<String, Map<String, RepeatingGroupBuilder>>> groupIDEntrySetIter = null;
        groupIDEntrySet = repeatingGrpMap.getGroupMap().entrySet();
        groupIDEntrySetIter = groupIDEntrySet.iterator();
        while (groupIDEntrySetIter.hasNext()) {
            out.println();
            Entry<String, Map<String, RepeatingGroupBuilder>> groupIDEntry = groupIDEntrySetIter
                .next();
            String msgType = groupIDEntry.getKey();
            if ("HEADER".equals(msgType)) {
                continue;
            }
            Iterator<String> groupIDIter = groupIDEntry.getValue().keySet()
                .iterator();
            out.println("\tprivate void initMessageType_" + msgType + "() {");
            handleInitHeader(msgType);
            while (groupIDIter.hasNext()) {
                String groupID = groupIDIter.next();
                RepeatingGroupBuilder group = groupIDEntry.getValue()
                    .get(groupID);
                String gid = group.getGroupId();
                // System.out.println("msgType=" + msgType + "group=" + group);
                String delim = null;
                try {
                    delim = group.getGroupDelim();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("msgType=" + msgType + "group=" + gid);
                    System.out.flush();
                    System.exit(-1);
                }
                String groupClassName = "Msg_" + msgType + "_"
                    + msgType.hashCode() + "_Group_" + gid;
                out.println(
                    "\t\tputGroup(\"" + msgType + "\", " + groupClassName
                        + ".getInstance(\"" + gid + "\", \"" + delim + "\"));");
            }
            out.println("\t}");
        }
    }

    private void handleInitHeader(String msgType) {
        Map<String, RepeatingGroupBuilder> headerMap = repeatingGrpMap
            .getGroupMap().get("HEADER");
        if (headerMap == null) {
            return;
        }
        Iterator<String> groupIDs = headerMap.keySet().iterator();
        while (groupIDs.hasNext()) {
            String groupID = groupIDs.next();
            RepeatingGroupBuilder group = headerMap.get(groupID);
            String gid = group.getGroupId();
            String delim = group.getGroupDelim();
            // String groupClassName = "Msg_" + msgType + "_" +
            // msgType.hashCode() + "_Group_" + gid;
            String groupClassName = "Header_Group_" + gid;
            out.println("\t\tputGroup(\"" + msgType + "\", " + groupClassName
                + ".getInstance(\"" + gid + "\", \"" + delim + "\"));");
            // String groupClassName = "Header_Group_" + gid;
            // out.println("\t\tputGroup(\"" + groupClassName +
            // ".getInstance(\""
            // + gid + "\", \"" + delim + "\"));");
        }
    }

    private void handleHeader(String msgType,
        Map<String, RepeatingGroupBuilder> headerGrps) {
        if (headerGrps == null) {
            return;
        }
        Iterator<String> groupIDs = headerGrps.keySet().iterator();
        while (groupIDs.hasNext()) {
            String tagCtx = groupIDs.next();
            RepeatingGroupBuilder group = headerGrps.get(tagCtx);
            LinkedList<String> members = group.getMemberList();
            Iterator<String> itm = members.iterator();
            while (itm.hasNext()) {
                String em = itm.next();
                out.println("\t\t\tmemberSet.add(\"" + em + "\");");
            }
        }
    }

    private void writeOutGroupClass(String msgType,
        RepeatingGroupBuilder group) {
        String groupId = group.getGroupId();
        String delim = group.getGroupDelim();
        String groupClassName = null;
        if ("HEADER".equals(msgType)) {
            groupClassName = "Header_Group_" + groupId;
        } else {
            groupClassName = "Msg_" + msgType + "_" + msgType.hashCode()
                + "_Group_" + groupId;
        }
        out.println("\tstatic final class " + groupClassName
            + " extends FixRepeatingGroup {");
        out.println(
            "\t\tprivate static " + groupClassName + " instance = null;");
        out.println();
        out.println("\t\tprivate static synchronized " + groupClassName
            + " getInstance(String id, String delim) {");
        out.println("\t\t   if (instance == null) {");
        out.println(
            "\t\t      instance = new " + groupClassName + "(id, delim);");
        out.println("\t\t   }");
        out.println("\t\t   return instance;");
        out.println("\t\t}");
        out.println();
        // do constructors
        out.println(
            "\t\tprivate " + groupClassName + "(String id, String delim) {");
        out.println("\t\t\tsuper(id, delim);");
        LinkedList<String> members = group.getMemberList();
        Iterator<String> itm = members.iterator();
        while (itm.hasNext()) {
            String em = itm.next();
            out.println("\t\t\tmemberSet.add(\"" + em + "\");");
        }
        out.println("\t\t}");
        out.println("\t}");
        out.println();
    }

    private void handleDefineGroups() {
        Set<Entry<String, Map<String, RepeatingGroupBuilder>>> groupIDEntrySet = null;
        Iterator<Entry<String, Map<String, RepeatingGroupBuilder>>> groupIDEntrySetIter = null;
        groupIDEntrySet = repeatingGrpMap.getGroupMap().entrySet();
        groupIDEntrySetIter = groupIDEntrySet.iterator();
        while (groupIDEntrySetIter.hasNext()) {
            out.println();
            Entry<String, Map<String, RepeatingGroupBuilder>> groupIDEntry = groupIDEntrySetIter
                .next();
            String msgType = groupIDEntry.getKey();
            Iterator<String> groupIDIter = null;
            groupIDIter = groupIDEntry.getValue().keySet().iterator();
            while (groupIDIter.hasNext()) {
                String groupID = groupIDIter.next();
                RepeatingGroupBuilder group = groupIDEntry.getValue()
                    .get(groupID);
                writeOutGroupClass(msgType, group);
            }
        }
    }

    /**
     * Begin constructing the java source when this rule is invoked by antlr.
     * @param version The fix version. Not used.
     */
    private void handleStartClass() {
        out.println("package com.globalforge.infix.qfix."
            + this.qfixverLowerCase + ";");
        out.println();
        out.println("import com.globalforge.infix.qfix.FixGroupMgr;");
        out.println("import com.globalforge.infix.qfix.FixRepeatingGroup;");
        out.println();
        out.println("/**");
        out.println(
            "* This class is auto-generated. It should never be coded by hand. If you find");
        out.println(
            "* yourself coding this class then you have failed to understand how to build");
        out.println(
            "* the tool. It would actually be faster to do it the right way.");
        out.println("*/");
        out.println(
            "public class " + fileNamePrefix + " extends FixGroupMgr {");
        out.println("\t{");
    }

    private void finish() {
        out.println("}");
    }
}
