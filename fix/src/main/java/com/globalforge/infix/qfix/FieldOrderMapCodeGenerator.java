package com.globalforge.infix.qfix;

import java.io.File;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.globalforge.infix.FixDataDictListener;

public class FieldOrderMapCodeGenerator {
    /** logger */
    private final static Logger logger = LoggerFactory.getLogger(FixDataDictListener.class);
    private String fileNamePrefix = null;
    private final String afixVer;
    private String qfixverLowerCase = null;
    private PrintStream out = null;
    private final ContextOrderMap msgCtxMap;

    public FieldOrderMapCodeGenerator(String fVer, DataGenerator d) {
        this.afixVer = fVer.replace(".", "");
        this.msgCtxMap = d.getContextOrderMap(fVer);
    }

    public void generateClass() throws Exception {
        initOutputStreams();
        handleStartClass();
        handleConstructor();
        handleMessages();
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
            fileNamePrefix = afixVer + "FieldOrderMap";
            qfixverLowerCase = afixVer.toLowerCase();
            File fOut = new File(SRC_DIR + System.getProperty("file.separator") + qfixverLowerCase
                + System.getProperty("file.separator") + fileNamePrefix + ".java");
            logger.info("building java file: {}", fOut.getAbsolutePath());
            out = new PrintStream(fOut, "UTF-8");
        }
    }

    private void handleConstructor() {
        Set<Entry<String, LinkedHashMap<String, String>>> compMems = null;
        Iterator<Entry<String, LinkedHashMap<String, String>>> memSetIterator = null;
        compMems = msgCtxMap.getMessageMap().entrySet();
        memSetIterator = compMems.iterator();
        while (memSetIterator.hasNext()) {
            Entry<String, LinkedHashMap<String, String>> ctxEntry = memSetIterator.next();
            String msgType = ctxEntry.getKey();
            out.println("\t\tinitMessageType_" + msgType + "();");
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
            Entry<String, LinkedHashMap<String, String>> ctxEntry =
                (Entry<String, LinkedHashMap<String, String>>) memSetIterator.next();
            String msgType = ctxEntry.getKey();
            Iterator<String> ctxSet = ctxEntry.getValue().keySet().iterator();
            out.println("\tprivate void initMessageType_" + msgType + "() {");
            // handleHeader(msgType);
            while (ctxSet.hasNext()) {
                String tagCtx = ctxSet.next();
                String orderCtx = ctxEntry.getValue().get(tagCtx);
                int refIdx = tagCtx.indexOf("->");
                out.println("\t\tputFieldOrder(\"" + msgType + "\", \"" + tagCtx + "\", \""
                    + orderCtx + "\");");
            }
            out.println("\t}");
        }
    }

    /**
     * Begin constructing the java source when this rule is invoked by antlr.
     * 
     * @param version The fix version. Not used.
     */
    private void handleStartClass() {
        out.println("package com.globalforge.infix.qfix." + this.qfixverLowerCase + ";");
        out.println();
        out.println("import com.globalforge.infix.qfix.FieldOrderMap;");
        out.println();
        out.println("/**");
        out.println(
            "* This class is auto-generated. It should never be coded by hand. If you find");
        out.println("* yourself coding this class then you have failed to understand how to build");
        out.println("* the tool. It would acutally be faster to do it the right way.");
        out.println("*/");
        out.println("class " + fileNamePrefix + " extends FieldOrderMap {");
        out.println("\t{");
    }

    private void finish() {
        out.println("}");
    }
}
