package com.globalforge.infix.qfix;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*-
 The MIT License (MIT)

 Copyright (c) 2017 Global Forge LLC

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
 * Parses a quick fix data dictionary.
 * @author Michael C. Starkie
 */
public class DataDictionaryParser {
    /** logger */
    final static Logger logger = LoggerFactory.getLogger(DataDictionaryParser.class);
    protected FieldParser fieldParser = null;
    protected HeaderParser hdrParser = null;
    protected ComponentParser componentParser = null;
    protected MessageParser messageParser = null;
    protected PrintStream out = null;
    protected String fixFileName = null;
    protected String qFixVersion = null;

    /**
     * Standard dictionary constructor
     * @param ver a FIX version. The name of a FIX data dictionary.
     * @throws Exception
     */
    public DataDictionaryParser(String ver) throws Exception {
        setUp(ver, null);
    }

    /**
     * Custom dictionary constructor
     * @param ver a custom FIX version. The name of a custom FIX data
     * dictionary.
     * @param basedOnVer Must supply a standard FIX version that the custom
     * dictionary is based upon. For example FIX.4.4 or FIX.5.0. All FIX version
     * specific parsings in this tool are based upon one of those two.
     * @throws Exception
     */
    public DataDictionaryParser(String ver, String basedOnVer) throws Exception {
        setUp(ver, basedOnVer);
    }

    /**
     * @return HeaderParser the component which parses the header section
     */
    public HeaderParser getHeaderParser() {
        return hdrParser;
    }

    /**
     * @return MessageParser the component which parses the messages section
     */
    public MessageParser getMessageParser() {
        return messageParser;
    }

    /**
     * @return ComponentParser the component which parses the components section
     */
    public ComponentParser getComponentParser() {
        return componentParser;
    }

    /**
     * Make sure we know where to find the xml data dictionary file.
     * @param ver The fix version we are parsing.
     * @param basedOnVer for custom dictionaries, the standard version the
     * custom dictionary is based upon.
     * @throws Exception When external dependencies are not recognized.
     */
    protected void setUp(String ver, String basedOnVer) throws Exception {
        String CONFIG_DIR = System.getenv("CONFIG_DIR");
        if (CONFIG_DIR != null) {
            DataDictionaryParser.logger.info("CONFIG_DIR is an ENV variable: {}", CONFIG_DIR);
        } else {
            CONFIG_DIR = System.getProperty("CONFIG_DIR");
            if (CONFIG_DIR != null) {
                DataDictionaryParser.logger.info("CONFIG_DIR is a System property: {}", CONFIG_DIR);
            } else {
                CONFIG_DIR = null;
            }
        }
        if (CONFIG_DIR == null) {
            DataDictionaryParser.logger.warn("No CONFIG_DIR provided.  Output stream is CONSOLE");
            out = System.out;
        } else {
            File fixOut =
                new File(CONFIG_DIR + System.getProperty("file.separator") + ver + "Mgr.xml.tmp");
            out = new PrintStream(fixOut, "UTF-8");
        }
        String tmpName = null;
        if (basedOnVer == null) {
            qFixVersion = ver.replace(".", "");
            tmpName = qFixVersion + ".xml";
        } else {
            qFixVersion = basedOnVer.replace(".", "");
            String actualVersion = ver.replace(".", "");
            tmpName = actualVersion + ".xml";
        }
        InputStream is = null;
        is = ClassLoader.getSystemResourceAsStream(tmpName);
        if (is != null) {
            DataDictionaryParser.logger.info("Parsing fix data dictionary file: " + tmpName);
            fixFileName = tmpName;
            is.close();
        } else {
            DataDictionaryParser.logger
                .error("Could not find data dictionary xml file for fix version: " + ver);
            throw new RuntimeException("no data dictionary found: " + tmpName);
        }
    }

    /**
     * Parses the fields section of a fix xml data dictionary file.
     * @throws Exception
     */
    protected void parseFields() throws Exception {
        fieldParser = new FieldParser(fixFileName);
        fieldParser.parse();
    }

    /**
     * Parses the components section of a fix xml data dictionary file
     * @throws Exception
     */
    protected void parseComponents() throws Exception {
        Class<?> FIXParserDefinition;
        Class<?>[] parserArgsClass = new Class[] {
            String.class, FieldParser.class };
        Object[] parserArgs = new Object[] {
            fixFileName, fieldParser };
        Constructor<?> parserArgsConstructor;
        FIXParserDefinition = Class.forName("com.globalforge.infix.qfix."
            + qFixVersion.toLowerCase() + "." + qFixVersion + "ComponentParser");
        parserArgsConstructor = FIXParserDefinition.getConstructor(parserArgsClass);
        componentParser = (ComponentParser) parserArgsConstructor.newInstance(parserArgs);
        componentParser.parse();
    }

    /**
     * Parses the messages section of a fix xml data dictionary file
     * @throws Exception
     */
    protected void parseMessages() throws Exception {
        Class<?> FIXParserDefinition;
        Class<?>[] parserArgsClass = new Class[] {
            String.class, FieldParser.class, HeaderParser.class, ComponentParser.class };
        Object[] parserArgs = new Object[] {
            fixFileName, fieldParser, hdrParser, componentParser };
        Constructor<?> parserArgsConstructor;
        FIXParserDefinition = Class.forName("com.globalforge.infix.qfix."
            + qFixVersion.toLowerCase() + "." + qFixVersion + "MessageParser");
        parserArgsConstructor = FIXParserDefinition.getConstructor(parserArgsClass);
        messageParser = (MessageParser) parserArgsConstructor.newInstance(parserArgs);
        messageParser.parse();
    }

    /**
     * Parses the header section of a fix xml data dictionary file
     * @throws Exception
     */
    protected void parseHeader() throws Exception {
        Class<?> FIXParserDefinition;
        Class<?>[] parserArgsClass = new Class[] {
            String.class, FieldParser.class, DataStore.class };
        Object[] parserArgs = new Object[] {
            fixFileName, fieldParser, componentParser.getContextStore() };
        Constructor<?> parserArgsConstructor;
        int v = qFixVersion.indexOf('5');
        if (v < 0) {
            FIXParserDefinition = Class.forName("com.globalforge.infix.qfix."
                + qFixVersion.toLowerCase() + "." + qFixVersion + "HeaderParser");
            parserArgsConstructor = FIXParserDefinition.getConstructor(parserArgsClass);
            hdrParser = (HeaderParser) parserArgsConstructor.newInstance(parserArgs);
        } else {
            // versions about 5.0 get their header and trailer from FIXT11
            FieldParser fp = new FieldParser("FIXT11.xml");
            fp.parse();
            hdrParser = new HeaderParser("FIXT11.xml", fp, componentParser.getContextStore());
        }
        hdrParser.parse();
    }
}
