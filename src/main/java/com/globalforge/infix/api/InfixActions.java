package com.globalforge.infix.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import com.globalforge.infix.FixRulesErrorStrategy;
import com.globalforge.infix.FixRulesLexerErrorListener;
import com.globalforge.infix.FixRulesParserErrorListener;
import com.globalforge.infix.FixRulesTransformVisitor;
import com.globalforge.infix.antlr.FixRulesLexer;
import com.globalforge.infix.antlr.FixRulesParser;

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
 * The main entry point to the engine. This is the class which transforms a fix
 * message given a string in rule syntax.
 * 
 * @author Michael C. Starkie
 */
public class InfixActions {
    // create a CharStream that reads from standard input
    private ANTLRInputStream input = null;
    // create a lexer that feeds off of input CharStream
    private FixRulesLexer lexer = null;
    // create a buffer of tokens pulled from the lexer
    private CommonTokenStream tokens = null;
    // create a parser that feeds off the tokens buffer
    private FixRulesParser parser = null;
    private ParseTree tree = null;

    /**
     * Initialize the engine and runs the engine given a rule or set of rules.
     * 
     * @param ruleInput The list of rules
     * @throws IOException When the rule input can not be read.
     */
    public InfixActions(InputStream ruleInput) throws IOException {
        input = new ANTLRInputStream(ruleInput);
        lexer = new FixRulesLexer(input);
        lexer.removeErrorListeners();
        lexer.addErrorListener(FixRulesLexerErrorListener.INSTANCE);
        tokens = new CommonTokenStream(lexer);
        parser = new FixRulesParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(FixRulesParserErrorListener.INSTANCE);
        parser.setErrorHandler(new FixRulesErrorStrategy());
        parseRules();
    }

    /**
     * Initializes the rule engine with a string in rule syntax.
     * 
     * @param ruleInput The rules to apply in rule syntax.
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public InfixActions(String ruleInput) throws UnsupportedEncodingException,
        IOException {
        this(new ByteArrayInputStream(ruleInput.getBytes("UTF-8")));
    }

    /**
     * An optional method to load classes before parsing rules. This reduces the
     * time it takes to parse the first rule by loading static data into memory
     * before any rule processing.
     * 
     * @param fixVersion The fix version static data to load.
     */
    public static void primeEngine(String fixVersion) {
        if ((fixVersion != null) && !fixVersion.isEmpty()) {
            try {
                final String sampleMessage1 =
                    "8=" + fixVersion + '\u0001' + "9=10" + '\u0001' + "35=8"
                        + '\u0001' + "45=1" + '\u0001' + "10=004";
                InfixActions rules = new InfixActions("&45=2");
                rules.transformFIXMsg(sampleMessage1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Parses the rules into a syntax tree for debugging.
     */
    private void parseRules() {
        tree = parser.parseRules();
    }

    /**
     * Accepts a fix message, applies the rules, and returns a transformed fix
     * message. Used for debugging purposes.
     * 
     * @param fixMessage The fix message to transform
     * @param printDictionary true if you want a dump of internal dictionaries.
     * @return String the transformed fix message.
     */
    public String transformFIXMsg(String fixMessage, boolean printDictionary) {
        FixRulesTransformVisitor visitor =
            new FixRulesTransformVisitor(fixMessage);
        String transform = visitor.visit(tree);
        if (printDictionary) {
            visitor.printDictionary();
        }
        return transform;
    }

    /**
     * Accepts a fix message, applies the rules, and returns a transformed fix
     * message.
     * 
     * @param fixMessage The fix message to transform
     * @return String the transformed fix message.
     */
    public String transformFIXMsg(String fixMessage) {
        FixRulesTransformVisitor visitor =
            new FixRulesTransformVisitor(fixMessage);
        String result = visitor.visit(tree);
        return result;
    }

    /**
     * Accepts a FIX version and MsgType, applies rules, and returns a
     * tranformed FIX message. It's up to the user to add meaningful tag
     * references and values during the rules parse. Currently, the only way to
     * achieve that is using the user defined behavior API which can offer an
     * implementation of the InfixAPI. The InfixAPI provides methods to insert
     * tag references and values as if they were parsed from a FIX message. The
     * only restrictions is that you must know the FIX version and MsgType ahead
     * of time which defines the message you will populate and for which any
     * subsequent rules apply.
     * 
     * @param tag8Value FIX version
     * @param tag35Value Message Type
     * @return String the transformed fix message.
     */
    public String transformFIXMsg(String tag8Value, String tag35Value) {
        FixRulesTransformVisitor visitor =
            new FixRulesTransformVisitor(tag8Value, tag35Value);
        String result = visitor.visit(tree);
        return result;
    }

    /**
     * Returns an antlr rule tree for debugging.
     */
    @Override
    public String toString() {
        return tree.toStringTree(parser); // print LISP-style tree
    }

    public static void main(String[] args) {
        try {
            String sampleRule = "&55>=&65 && &40<=2 -> &60=\"FOO\"";
            InfixActions msgHandler = new InfixActions(sampleRule);
            System.out.println(msgHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
