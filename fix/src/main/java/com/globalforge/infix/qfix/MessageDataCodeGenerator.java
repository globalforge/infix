package com.globalforge.infix.qfix;

import java.io.File;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Generates the sub-classes of MessageData for each version of FIX.
 * @author Michael C. Starkie
 */
public class MessageDataCodeGenerator {
    /** logger */
    private final static Logger logger = LoggerFactory.getLogger(MessageDataCodeGenerator.class);
    private String fileNamePrefix = null;
    private final String afixVer;
    private String qfixverLowerCase = null;
    private PrintStream out = null;
    private final ContextOrderMap msgCtxMap;

    /**
     * @param fVer FIX Version
     * @param d all the data parsed from the FIX Version
     */
    public MessageDataCodeGenerator(String fVer, DataGenerator d) {
        this.afixVer = fVer.replace(".", "");
        this.msgCtxMap = d.getContextOrderMap(fVer);
    }

    /**
     * Does the deed. Generates all the java classes.
     * @throws Exception Error generating class.
     */
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

    /**
     * Determines where on the file system to write the code. Each message type
     * has it's own package location. Must set SRC_DIR environment variable to
     * absolute path of com.globalforge.infix.qfix package <br>
     * Example: <br>
     * SRC_DIR=/projects/infix/fix/src/main/java/com/globalforge/infix/qfix
     * @param msgType
     * @throws Exception
     */
    private void initOutputStreams(String type) throws Exception {
        String SRC_DIR = System.getenv("SRC_DIR");
        if (SRC_DIR != null) {
            MessageDataCodeGenerator.logger.info("SRC_DIR is an ENV variable: {}", SRC_DIR);
        } else {
            SRC_DIR = System.getProperty("SRC_DIR");
            if (SRC_DIR != null) {
                MessageDataCodeGenerator.logger.info("SRC_DIR is a System property: {}", SRC_DIR);
            } else {
                SRC_DIR = null;
            }
        }
        if (SRC_DIR == null) {
            MessageDataCodeGenerator.logger.warn("No SRC_DIR provided.  Output stream is CONSOLE");
            out = System.out;
        } else {
            fileNamePrefix = afixVer + type + "MessageData";
            qfixverLowerCase = afixVer.toLowerCase();
            File fOut = new File(SRC_DIR + System.getProperty("file.separator") + qfixverLowerCase
                + System.getProperty("file.separator") + "auto"
                + System.getProperty("file.separator") + fileNamePrefix + ".java");
            MessageDataCodeGenerator.logger.info("building java file: {}", fOut.getAbsolutePath());
            fOut.getParentFile().mkdirs();
            out = new PrintStream(fOut, "UTF-8");
        }
    }

    /**
     * Begin constructing the java source when this rule is invoked by antlr.
     * @param version The fix version. Not used.
     */
    private void handleStartClass() {
        out.println("package com.globalforge.infix.qfix." + this.qfixverLowerCase + ".auto;");
        out.println();
        out.println("import com.globalforge.infix.qfix.MessageData;");
        out.println("import com.globalforge.infix.qfix." + qfixverLowerCase + ".auto.field.*;");
        out.println("import com.globalforge.infix.qfix." + qfixverLowerCase + ".auto.group.*;");
        doCopyright();
        out.println();
        out.println("/**");
        out.println(
            "* This class is auto-generated. It should never be coded by hand. If you find");
        out.println("* yourself coding this class then you have failed to understand how to build");
        out.println("* the tool. It would actually be faster to do it the right way.");
        out.println("*/");
        out.println("public class " + fileNamePrefix + " extends MessageData {");
    }

    /**
     * mucho importante
     */
    private void doCopyright() {
        out.println();
        out.println("/*-");
        out.println("The MIT License (MIT)");
        out.println();
        out.println("Copyright (c) 2019-2022 Global Forge LLC");
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

    /**
     * Build the constructors which initializes each message type
     */
    private void handleConstructor() {
        out.println("\t{");
        Set<Entry<String, LinkedHashMap<String, String>>> compMems = null;
        Iterator<Entry<String, LinkedHashMap<String, String>>> memSetIterator = null;
        compMems = msgCtxMap.getMessageMap().entrySet();
        memSetIterator = compMems.iterator();
        while (memSetIterator.hasNext()) {
            Entry<String, LinkedHashMap<String, String>> ctxEntry = memSetIterator.next();
            String msgType = ctxEntry.getKey();
            String msgHashTag = msgType + "_" + msgType.hashCode();
            out.println("\t\tinitMessageType_" + msgHashTag + "();");
        }
        out.println("\t}");
    }

    /**
     * build the methods which populate the runtim data.
     */
    private void handleMessages() {
        Set<Entry<String, LinkedHashMap<String, String>>> compMems = null;
        Iterator<Entry<String, LinkedHashMap<String, String>>> memSetIterator = null;
        compMems = msgCtxMap.getMessageMap().entrySet();
        memSetIterator = compMems.iterator();
        while (memSetIterator.hasNext()) {
            out.println();
            Entry<String, LinkedHashMap<String, String>> ctxEntry = memSetIterator.next();
            String msgType = ctxEntry.getKey();
            String msgHashTag = msgType + "_" + msgType.hashCode();
            out.println("\tpublic void initMessageType_" + msgHashTag + "() {");
            out.println("\t\tfieldOrderMap.put(\"" + msgType + "\", new " + afixVer + "_"
                + msgHashTag + "_FieldOrderMap());");
            out.println("\t\tgroupMap.put(\"" + msgType + "\", new " + afixVer + "_" + msgHashTag
                + "_GroupMgr());");
            out.println("\t}");
        }
    }

    /**
     * It's a good thing to finish
     */
    private void finish() {
        out.println("}");
    }
}
