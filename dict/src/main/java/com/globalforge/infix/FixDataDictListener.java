package com.globalforge.infix;

import java.io.File;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.globalforge.infix.antlr.XMLParser;
import com.globalforge.infix.antlr.XMLParserBaseListener;

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
 * Responds to antlr is it walks it's parse tree generating java source code
 * from xml.
 */
public class FixDataDictListener extends XMLParserBaseListener {
    /** logger */
    final static Logger logger = LoggerFactory
        .getLogger(FixDataDictListener.class);
    // private XMLParser parser;
    private PrintStream out = null;
    private String curGrpName = null;
    private String curMsgType = null;
    private String fixVersion = null;
    private Map<String, Set<String>> msgMap =
        new HashMap<String, Set<String>>();
    private Map<String, GroupHolder> groupMap =
        new HashMap<String, GroupHolder>();
    private String fileNamePrefix = null;

    public FixDataDictListener(String ver) throws Exception {
        this.fixVersion = ver;
        initOutputStreams();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * The default implementation does nothing.
     */
    @Override
    public void enterContent(@NotNull XMLParser.ContentContext ctx) {
    }

    /**
     * {@inheritDoc}
     * <p/>
     * The default implementation does nothing.
     */
    @Override
    public void exitContent(@NotNull XMLParser.ContentContext ctx) {
    }

    /**
     * The program needs to know where to find the xml and where to write the
     * java source code. Both locations must be provided by the user of this
     * class.
     * 
     * @throws Exception When input or ouput resources can not be located.
     */
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
            fileNamePrefix = fixVersion.replaceAll("[\",.]", "") + "Mgr";
            File fOut =
                new File(SRC_DIR + System.getProperty("file.separator")
                    + fileNamePrefix + ".java");
            logger.info("building java file: {}", fOut.getAbsolutePath());
            out = new PrintStream(fOut, "UTF-8");
        }
    }

    /**
     * Begin constructing the java source when this rule is invoked by antlr.
     * 
     * @param version The fix version. Not used.
     */
    private void handleStartClass(String version) {
        out.println("package com.globalforge.infix;");
        out.println();
        out.println("/**");
        out.println("* This class is auto-generated. It should never be coded by hand. If you find");
        out.println("* yourself coding this class then you have failed to understand how to build");
        out.println("* the tool. It would acutally be faster to do it the right way.");
        out.println("*/");
        // out.println("public class " + fileNamePrefix +
        // " extends FixGroupMgr {");
        out.println("class " + fileNamePrefix + " extends FixGroupMgr {");
        out.println("\t{");
    }

    /**
     * The beginning of a new message type
     * 
     * @param msgType The new message type
     */
    private void handleStartNewMsg(String msgType) {
        if (msgMap.containsKey(msgType)) {
            String msg =
                "ERROR: Duplicate msgType found.  Fix before continuing. msgType="
                    + msgType;
            throw new RuntimeException(msg);
        }
        curMsgType = msgType;
    }

    /**
     * Strip quotes off of a string.
     * 
     * @param value The string to strip
     * @return the parameter minus any quotes.
     */
    private String removeQuotes(String value) {
        return value.substring(1, value.length() - 1); // remove the quotes
    }

    /**
     * The start of a new group
     * 
     * @param grpName The name of the group
     */
    private void handleStartNewGroup(String grpName) {
        grpName = removeQuotes(grpName);
        if (groupMap.containsKey(grpName)) {
            String msg =
                "ERROR: Duplicate groupName found.  Fix before continuing. grpName="
                    + grpName;
            throw new RuntimeException(msg);
        }
        GroupHolder gh = new GroupHolder(grpName);
        groupMap.put(grpName, gh);
        curGrpName = grpName;
    }

    /**
     * Adds a group name to a message type
     * 
     * @param grpName The group name.
     */
    private void handleAddGroup(String grpName) {
        Set<String> grps = msgMap.get(curMsgType);
        if (grps == null) {
            grps = new HashSet<String>();
            grps.add(grpName);
            msgMap.put(curMsgType, grps);
        } else if (grps.contains(grpName)) {
            String msg =
                "ERROR: Duplicate grpName found.  Fix before continuing. msgType="
                    + curMsgType + ", grpName=" + grpName;
            throw new RuntimeException(msg);
        }
        grps.add(grpName);
    }

    private void handleAddId(String id) {
        GroupHolder g = groupMap.get(curGrpName);
        g.setGroupId(id);
    }

    /**
     * Adds the delimiter field to the group
     * 
     * @param delim The delimiter field.
     */
    private void handleAddDelim(String delim) {
        GroupHolder g = groupMap.get(curGrpName);
        g.setGroupDelim(delim);
    }

    /**
     * Adds a member field to the group.
     * 
     * @param mem The member field.
     */
    private void handleAddMember(String mem) {
        GroupHolder g = groupMap.get(curGrpName);
        g.addMember(mem);
    }

    /**
     * Adds a groupId reference to the group.
     * 
     * @param mem The GroupID field.
     */
    private void handleAddGroupReference(String grpId) {
        GroupHolder g = groupMap.get(curGrpName);
        g.addGrpReference(grpId);
    }

    /**
     * Finishes up the xml file
     */
    private void finish() {
        Iterator<Entry<String, Set<String>>> it = msgMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Set<String>> e = it.next();
            String mType = e.getKey();
            Iterator<String> gt = e.getValue().iterator();
            while (gt.hasNext()) {
                String gName = gt.next();
                GroupHolder ghold = groupMap.get(gName);
                logger.info("Group: {}", gName);
                String id = ghold.groupId;
                String delim = ghold.groupDelim;
                // out.println("\t\tputGroup(" + mType + ", new " + gName +
                // "(\""
                // + id + "\", \"" + delim + "\"));");
                out.println("\t\tputGroup(" + mType + ", " + gName
                    + ".getInstance(\"" + id + "\", \"" + delim + "\"));");
            }
        }
        out.println("\t}");
        it = msgMap.entrySet().iterator();
        Set<String> defined = new HashSet<String>();
        while (it.hasNext()) {
            Entry<String, Set<String>> e = it.next();
            Iterator<String> gt = e.getValue().iterator();
            while (gt.hasNext()) {
                String gName = gt.next();
                if (defined.contains(gName)) {
                    continue;
                }
                GroupHolder ghold = groupMap.get(gName);
                // String id = ghold.groupId;
                String delim = ghold.groupDelim;
                // out.println("\tpublic static final class " + gName
                // + " extends FixRepeatingGroup {");
                out.println("\tstatic final class " + gName
                    + " extends FixRepeatingGroup {");
                out.println("\t   private static " + gName
                    + " instance = null;");
                out.println();
                out.println("\t\tprivate static synchronized " + gName
                    + " getInstance(String id, String delim) {");
                out.println("\t\t   if (instance == null) {");
                out.println("\t\t      instance = new " + gName
                    + "(id, delim);");
                out.println("\t\t   }");
                out.println("\t\t   return instance;");
                out.println("\t\t}");
                out.println();
                // do constructors
                out.println("\t\tprivate " + gName
                    + "(String id, String delim) {");
                // out.println("\t\t\tsuper(\"" + id + "\",\"" + delim +
                // "\");");
                out.println("\t\t\tsuper(id, delim);");
                // out.write("\t\t\tSet<String> f = new HashSet<String>();\n");
                Set<String> members = ghold.getMemberSet();
                Iterator<String> itm = members.iterator();
                out.println("\t\t\tmemberSet.add(\"" + delim + "\");");
                while (itm.hasNext()) {
                    String em = itm.next();
                    out.println("\t\t\tmemberSet.add(\"" + em + "\");");
                }
                Set<String> grpIds = ghold.getGrpReferenceSet();
                Iterator<String> itg = grpIds.iterator();
                while (itg.hasNext()) {
                    String em = itg.next();
                    out.println("\t\t\tgrpReferenceSet.add(\"" + em + "\");");
                }
                out.println("\t\t}");
                out.println("\t}");
                defined.add(gName);
            }
        }
        out.println("}");
    }

    /**
     * The entry point for each element in the xml input file
     */
    @Override
    public void enterElement(@NotNull XMLParser.ElementContext ctx) {
        String elemName = ctx.Name(0).toString();
        if (elemName.equals("Body")) {
            String version = ctx.attribute(0).STRING().getText();
            handleStartClass(version);
        } else if (elemName.equals("Group")) {
            String grpName = ctx.attribute(0).STRING().getText();
            handleStartNewGroup(grpName);
        } else if (elemName.equals("MsgType")) {
            String msgType = ctx.attribute(0).STRING().getText();
            handleStartNewMsg(msgType);
        } else if (elemName.equals("Name")) {
            String grpName = ctx.content().getText();
            handleAddGroup(grpName);
        } else if (elemName.equals("Id")) {
            String id = ctx.content().getText();
            handleAddId(id);
        } else if (elemName.equals("Delim")) {
            String delim = ctx.content().getText();
            handleAddDelim(delim);
        } else if (elemName.equals("Member")) {
            String mem = ctx.content().getText();
            handleAddMember(mem);
        } else if (elemName.equals("GrpRefID")) {
            String mem = ctx.content().getText();
            handleAddGroupReference(mem);
        }
    }

    /**
     * clean up each element. Only Body needs to close the xml file
     */
    @Override
    public void exitElement(@NotNull XMLParser.ElementContext ctx) {
        String elemName = ctx.Name(0).toString();
        if (elemName.equals("Body")) {
            try {
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    /**
     * {@inheritDoc}
     * <p/>
     * The default implementation does nothing.
     */
    @Override
    public void enterProlog(@NotNull XMLParser.PrologContext ctx) {
    }

    /**
     * {@inheritDoc}
     * <p/>
     * The default implementation does nothing.
     */
    @Override
    public void exitProlog(@NotNull XMLParser.PrologContext ctx) {
    }

    /**
     * {@inheritDoc}
     * <p/>
     * The default implementation does nothing.
     */
    @Override
    public void enterDocument(@NotNull XMLParser.DocumentContext ctx) {
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Closes the xml file
     */
    @Override
    public void exitDocument(@NotNull XMLParser.DocumentContext ctx) {
        try {
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     * <p/>
     * The default implementation does nothing.
     */
    @Override
    public void enterAttribute(@NotNull XMLParser.AttributeContext ctx) {
    }

    /**
     * {@inheritDoc}
     * <p/>
     * The default implementation does nothing.
     */
    @Override
    public void exitAttribute(@NotNull XMLParser.AttributeContext ctx) {
    }

    /**
     * {@inheritDoc}
     * <p/>
     * The default implementation does nothing.
     */
    @Override
    public void enterChardata(@NotNull XMLParser.ChardataContext ctx) {
    }

    /**
     * {@inheritDoc}
     * <p/>
     * The default implementation does nothing.
     */
    @Override
    public void exitChardata(@NotNull XMLParser.ChardataContext ctx) {
    }

    /**
     * {@inheritDoc}
     * <p/>
     * The default implementation does nothing.
     */
    @Override
    public void enterReference(@NotNull XMLParser.ReferenceContext ctx) {
    }

    /**
     * {@inheritDoc}
     * <p/>
     * The default implementation does nothing.
     */
    @Override
    public void exitReference(@NotNull XMLParser.ReferenceContext ctx) {
    }

    /**
     * {@inheritDoc}
     * <p/>
     * The default implementation does nothing.
     */
    @Override
    public void enterMisc(@NotNull XMLParser.MiscContext ctx) {
    }

    /**
     * {@inheritDoc}
     * <p/>
     * The default implementation does nothing.
     */
    @Override
    public void exitMisc(@NotNull XMLParser.MiscContext ctx) {
    }

    /**
     * {@inheritDoc}
     * <p/>
     * The default implementation does nothing.
     */
    @Override
    public void enterEveryRule(@NotNull ParserRuleContext ctx) {
    }

    /**
     * {@inheritDoc}
     * <p/>
     * The default implementation does nothing.
     */
    @Override
    public void exitEveryRule(@NotNull ParserRuleContext ctx) {
    }

    /**
     * {@inheritDoc}
     * <p/>
     * The default implementation does nothing.
     */
    @Override
    public void visitTerminal(@NotNull TerminalNode node) {
    }

    /**
     * {@inheritDoc}
     * <p/>
     * The default implementation does nothing.
     */
    @Override
    public void visitErrorNode(@NotNull ErrorNode node) {
    }

    /**
     * A temporary class holding xml data as it parsed from xml and before the
     * data is transformed into a java class.
     * 
     * @author Michael Starkie
     */
    public static class GroupHolder {
        private final Set<String> memberSet = new HashSet<String>();
        private final Set<String> grpRefSet = new HashSet<String>();
        private String groupId = null;
        private String groupDelim = null;
        private String groupName = null;

        public GroupHolder(String name) {
            groupName = name;
        }

        public void setGroupId(String gid) {
            groupId = gid;
        }

        public void setGroupDelim(String del) {
            groupDelim = del;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getGroupDelim() {
            return groupDelim;
        }

        public String getGroupName() {
            return groupName;
        }

        public Set<String> getMemberSet() {
            return Collections.unmodifiableSet(memberSet);
        }

        public void addMember(String mem) {
            memberSet.add(mem);
        }

        public Set<String> getGrpReferenceSet() {
            return Collections.unmodifiableSet(grpRefSet);
        }

        public void addGrpReference(String grpId) {
            grpRefSet.add(grpId);
        }

        @Override
        public int hashCode() {
            return groupName.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof GroupHolder) {
                GroupHolder that = (GroupHolder) obj;
                return this.groupName.equals(that.groupName);
            }
            return false;
        }
    }
}
