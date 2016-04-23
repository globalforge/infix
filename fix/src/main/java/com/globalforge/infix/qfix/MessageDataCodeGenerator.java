package com.globalforge.infix.qfix;

import java.io.File;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageDataCodeGenerator {
    /** logger */
    private final static Logger logger = LoggerFactory
        .getLogger(MessageDataCodeGenerator.class);
    private String fileNamePrefix = null;
    private final String afixVer;
    private String qfixverLowerCase = null;
    private PrintStream out = null;
    private final ContextOrderMap msgCtxMap;

    public MessageDataCodeGenerator(String fVer, DataGenerator d) {
        this.afixVer = fVer.replace(".", "");
        this.msgCtxMap = d.getContextOrderMap(fVer);
    }

    public void generateClass() throws Exception {
        initOutputStreams("Static");
        handleStartClass();
        handleConstructor();
        handleMessages();
        finish();
        initOutputStreams("Dynamic");
        handleStartClass();
        handleMessages();
        finish();
    }

    private void initOutputStreams(String type) throws Exception {
        String SRC_DIR = System.getenv("SRC_DIR");
        if (SRC_DIR != null) {
            MessageDataCodeGenerator.logger
                .info("SRC_DIR is an ENV variable: {}", SRC_DIR);
        } else {
            SRC_DIR = System.getProperty("SRC_DIR");
            if (SRC_DIR != null) {
                MessageDataCodeGenerator.logger
                    .info("SRC_DIR is a System property: {}", SRC_DIR);
            } else {
                SRC_DIR = null;
            }
        }
        if (SRC_DIR == null) {
            MessageDataCodeGenerator.logger
                .warn("No SRC_DIR provided.  Output stream is CONSOLE");
            out = System.out;
        } else {
            fileNamePrefix = afixVer + type + "MessageData";
            qfixverLowerCase = afixVer.toLowerCase();
            File fOut = new File(SRC_DIR + System.getProperty("file.separator")
                + qfixverLowerCase + System.getProperty("file.separator")
                + "auto" + System.getProperty("file.separator") + fileNamePrefix
                + ".java");
            MessageDataCodeGenerator.logger.info("building java file: {}",
                fOut.getAbsolutePath());
            fOut.getParentFile().mkdirs();
            out = new PrintStream(fOut, "UTF-8");
        }
    }

    /**
     * Begin constructing the java source when this rule is invoked by antlr.
     * @param version The fix version. Not used.
     */
    private void handleStartClass() {
        out.println("package com.globalforge.infix.qfix."
            + this.qfixverLowerCase + ".auto;");
        out.println();
        out.println("import com.globalforge.infix.qfix.MessageData;");
        out.println("import com.globalforge.infix.qfix." + qfixverLowerCase
            + ".auto.field.*;");
        out.println("import com.globalforge.infix.qfix." + qfixverLowerCase
            + ".auto.group.*;");
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
            "public class " + fileNamePrefix + " extends MessageData {");
    }

    private void handleConstructor() {
        out.println("\t{");
        Set<Entry<String, LinkedHashMap<String, String>>> compMems = null;
        Iterator<Entry<String, LinkedHashMap<String, String>>> memSetIterator = null;
        compMems = msgCtxMap.getMessageMap().entrySet();
        memSetIterator = compMems.iterator();
        while (memSetIterator.hasNext()) {
            Entry<String, LinkedHashMap<String, String>> ctxEntry = memSetIterator
                .next();
            String msgType = ctxEntry.getKey();
            String msgHashTag = msgType + "_" + msgType.hashCode();
            out.println("\t\tinitMessageType_" + msgHashTag + "();");
        }
        out.println("\t}");
    }

    private void handleMessages() {
        Set<Entry<String, LinkedHashMap<String, String>>> compMems = null;
        Iterator<Entry<String, LinkedHashMap<String, String>>> memSetIterator = null;
        compMems = msgCtxMap.getMessageMap().entrySet();
        memSetIterator = compMems.iterator();
        while (memSetIterator.hasNext()) {
            out.println();
            Entry<String, LinkedHashMap<String, String>> ctxEntry = memSetIterator
                .next();
            String msgType = ctxEntry.getKey();
            String msgHashTag = msgType + "_" + msgType.hashCode();
            out.println("\tpublic void initMessageType_" + msgHashTag + "() {");
            out.println("\t\tfieldOrderMap.put(\"" + msgType + "\", new "
                + afixVer + "_" + msgHashTag + "_FieldOrderMap());");
            out.println("\t\tgroupMap.put(\"" + msgType + "\", new " + afixVer
                + "_" + msgHashTag + "_GroupMgr());");
            out.println("\t}");
        }
    }

    private void finish() {
        out.println("}");
    }
}
