package com.globalforge.infix.qfix;

import java.io.File;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*-
The MIT License (MIT)

Copyright (c) 2016 Global Forge LLC

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
public class GroupManagerCodeGenerator {
    /** logger */
    private final static Logger logger = LoggerFactory.getLogger(GroupManagerCodeGenerator.class);
    private String fileNamePrefix = null;
    private final String afixVer;
    private String qfixverLowerCase = null;
    private PrintStream out = null;
    private final RepeatingGroupBuilderMap repeatingGrpMap;
    private final ContextOrderMap msgCtxMap;

    public GroupManagerCodeGenerator(String fVer, DataGenerator d) {
        this.afixVer = fVer.replace(".", "");
        this.repeatingGrpMap = d.getRepeatingGroupMap(fVer);
        this.msgCtxMap = d.getContextOrderMap(fVer);
    }

    public void generateClass() throws Exception {
        generateCode();
        finish();
    }

    private void initOutputStreams(String msgType) throws Exception {
        String SRC_DIR = System.getenv("SRC_DIR");
        if (SRC_DIR != null) {
            GroupManagerCodeGenerator.logger.info("SRC_DIR is an ENV variable: {}", SRC_DIR);
        } else {
            SRC_DIR = System.getProperty("SRC_DIR");
            if (SRC_DIR != null) {
                GroupManagerCodeGenerator.logger.info("SRC_DIR is a System property: {}", SRC_DIR);
            } else {
                SRC_DIR = null;
            }
        }
        if (SRC_DIR == null) {
            GroupManagerCodeGenerator.logger.warn("No SRC_DIR provided.  Output stream is CONSOLE");
            out = System.out;
        } else {
            fileNamePrefix = afixVer + "_" + msgType + "_" + "GroupMgr";
            qfixverLowerCase = afixVer.toLowerCase();
            File fOut = new File(SRC_DIR + System.getProperty("file.separator") + qfixverLowerCase
                + System.getProperty("file.separator") + "auto"
                + System.getProperty("file.separator") + "group"
                + System.getProperty("file.separator") + fileNamePrefix + ".java");
            GroupManagerCodeGenerator.logger.info("building java file: {}", fOut.getAbsolutePath());
            fOut.getParentFile().mkdirs();
            out = new PrintStream(fOut, "UTF-8");
        }
    }

    private void handleInitHeader() {
        Map<String, RepeatingGroupBuilder> headerMap = repeatingGrpMap.getGroupMap().get("HEADER");
        if (headerMap == null) { return; }
        Iterator<String> groupIDs = headerMap.keySet().iterator();
        while (groupIDs.hasNext()) {
            String groupID = groupIDs.next();
            RepeatingGroupBuilder group = headerMap.get(groupID);
            String gid = group.getGroupId();
            String delim = group.getGroupDelim();
            String groupClassName = "Header_Group_" + gid;
            out.println("\t\tputGroup(\"" + gid + "\", " + groupClassName + ".getInstance(\"" + gid
                + "\", \"" + delim + "\"));");
        }
    }

    private void writeOutGroupClass(String msgType, RepeatingGroupBuilder group) {
        String groupId = group.getGroupId();
        group.getGroupDelim();
        String groupClassName = null;
        String msgHashTag = msgType + "_" + msgType.hashCode();
        if ("HEADER".equals(msgType)) {
            groupClassName = "Header_Group_" + groupId;
        } else {
            groupClassName = "Msg_" + msgHashTag + "_Group_" + groupId;
        }
        out.println(
            "\tstatic final public class " + groupClassName + " extends FixRepeatingGroup {");
        out.println("\t\tprivate static " + groupClassName + " instance = null;");
        out.println();
        out.println(
            "\t\tprivate static " + groupClassName + " getInstance(String id, String delim) {");
        out.println("\t\t   if (instance == null) {");
        out.println("\t\t      instance = new " + groupClassName + "(id, delim);");
        out.println("\t\t   }");
        out.println("\t\t   return instance;");
        out.println("\t\t}");
        out.println();
        // do constructors
        out.println("\t\tprivate " + groupClassName + "(String id, String delim) {");
        out.println("\t\t\tsuper(id, delim);");
        LinkedList<String> members = group.getMemberList();
        Iterator<String> itm = members.iterator();
        while (itm.hasNext()) {
            String em = itm.next();
            out.println("\t\t\tmemberSet.add(\"" + em + "\");");
        }
        LinkedList<String> references = group.getReferenceList();
        itm = references.iterator();
        while (itm.hasNext()) {
            String em = itm.next();
            out.println("\t\t\treferenceSet.add(\"" + em + "\");");
        }
        out.println("\t\t}");
        out.println("\t}");
        out.println();
    }

    private void handleDefineGroups(String msgType) {
        Map<String, RepeatingGroupBuilder> msgGroupMap = repeatingGrpMap.getGroupMap().get(msgType);
        if (msgGroupMap == null) {
            GroupManagerCodeGenerator.logger
                .warn("NO GROUP FOX MSG TYPE: " + msgType + ", IN FIX: " + qfixverLowerCase);
            return;
        }
        Iterator<String> groupIDIter = msgGroupMap.keySet().iterator();
        out.println();
        while (groupIDIter.hasNext()) {
            String groupID = groupIDIter.next();
            RepeatingGroupBuilder group = msgGroupMap.get(groupID);
            writeOutGroupClass(msgType, group);
        }
    }

    /**
     * Begin constructing the java source when this rule is invoked by antlr.
     * @param version The fix version. Not used.
     */
    private void handleStartClass() {
        out.println("package com.globalforge.infix.qfix." + this.qfixverLowerCase + ".auto.group;");
        out.println();
        out.println("import com.globalforge.infix.qfix.FixGroupMgr;");
        out.println("import com.globalforge.infix.qfix.FixRepeatingGroup;");
        doCopyright();
        out.println();
        out.println("/**");
        out.println(
            "* This class is auto-generated. It should never be coded by hand. If you find");
        out.println("* yourself coding this class then you have failed to understand how to build");
        out.println("* the tool. It would actually be faster to do it the right way.");
        out.println("*/");
        out.println("public class " + fileNamePrefix + " extends FixGroupMgr {");
        out.println("\t{");
    }

    private void doCopyright() {
        out.println();
        out.println("/*-");
        out.println("The MIT License (MIT)");
        out.println();
        out.println("Copyright (c) 2016 Global Forge LLC");
        out.println();
        out.println("Permission is hereby granted, free of charge, to any person obtaining a copy");
        out.println(
            "of this software and associated documentation files (the \"Software\"), to deal");
        out.println("in the Software without restriction, including without limitation the rights");
        out.println("to use, copy, modify, merge, publish, distribute, sublicense, and/or sell");
        out.println("copies of the Software, and to permit persons to whom the Software is");
        out.println("furnished to do so, subject to the following conditions:");
        out.println();
        out.println(
            "The above copyright notice and this permission notice shall be included in all");
        out.println("copies or substantial portions of the Software.");
        out.println();
        out.println("THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR");
        out.println("IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,");
        out.println("FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE");
        out.println("AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER");
        out.println(
            "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,");
        out.println(
            "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE");
        out.println("SOFTWARE.");
        out.println("*/");
    }

    private void finish() {
        // out.println("}");
        out.flush();
        boolean isError = out.checkError();
        if (isError) { throw new RuntimeException("IO Error during Group Code Generation!"); }
    }

    private void generateCode() throws Exception {
        Set<Entry<String, LinkedHashMap<String, String>>> compMems = null;
        Iterator<Entry<String, LinkedHashMap<String, String>>> memSetIterator = null;
        compMems = msgCtxMap.getMessageMap().entrySet();
        memSetIterator = compMems.iterator();
        while (memSetIterator.hasNext()) {
            Entry<String, LinkedHashMap<String, String>> ctxEntry = memSetIterator.next();
            String msgType = ctxEntry.getKey();
            String msgHashTag = msgType + "_" + msgType.hashCode();
            // Each message type get's it's own class file.
            initOutputStreams(msgHashTag);
            handleStartClass();
            // Each class file needs the common header groups.
            handleInitHeader();
            Map<String, RepeatingGroupBuilder> grpMap = repeatingGrpMap.getGroupMap().get(msgType);
            if (grpMap != null) {
                Iterator<String> groupIDIter = grpMap.keySet().iterator();
                // Loop through all the groups found in a message type.
                // This loop also completes the constructor for the message
                // class.
                while (groupIDIter.hasNext()) {
                    String groupID = groupIDIter.next();
                    RepeatingGroupBuilder group = grpMap.get(groupID);
                    /*- do not remove this commented block for all time.
                    if (group.isNested()) {
                        continue;
                    }
                    */
                    String gid = group.getGroupId();
                    String delim = group.getGroupDelim();
                    String groupClassName = "Msg_" + msgHashTag + "_Group_" + gid;
                    out.println("\t\tputGroup(\"" + gid + "\", " + groupClassName
                        + ".getInstance(\"" + gid + "\", \"" + delim + "\"));");
                }
            }
            out.println("\t}");
            // Define the static classes represented by each group.
            handleDefineGroups("HEADER");
            if (grpMap != null) {
                handleDefineGroups(msgType);
            }
            out.println("}");
        }
    }
}
