package com.globalforge.infix.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import com.globalforge.infix.FixRulesTransformVisitor;

/*-
 The MIT License (MIT)

 Copyright (c) 2019-2020 Global Forge LLC

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
public class PrefixActions extends InfixActions {
    /**
     * Initialize the engine and runs the engine given a rule or set of rules.
     *
     * @param ruleInput The list of rules
     * @throws IOException When the rule input can not be read.
     */
    public PrefixActions(InputStream ruleInput) throws IOException {
        super(ruleInput);
    }

    /**
     * Initializes the rule engine with a string in rule syntax.
     *
     * @param ruleInput The rules to apply in rule syntax.
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public PrefixActions(String ruleInput) throws UnsupportedEncodingException, IOException {
        this(new ByteArrayInputStream(ruleInput.getBytes("UTF-8")));
    }

    /**
     * Accepts a fix message, applies the rules, and returns a transformed fix
     * message. Used for debugging purposes.
     *
     * @param fixMessage The fix message to transform
     * @param printDictionary true if you want a dump of internal dictionaries.
     * @return String the transformed fix message.
     */
    @Override
    public String transformFIXMsg(String fixMessage, boolean printDictionary) {
        FixRulesTransformVisitor visitor = new FixRulesTransformVisitor(fixMessage);
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
    @Override
    public String transformFIXMsg(String fixMessage) {
        FixRulesTransformVisitor visitor = new FixRulesTransformVisitor(fixMessage);
        String result = visitor.visit(tree);
        return result;
    }

    /**
     * @param tag8Value FIX version
     * @return String the transformed fix message.
     */
    @Override
    public String transformFIXMsg(String fixMessage, String tag8Value) {
        FixRulesTransformVisitor visitor = new FixRulesTransformVisitor(fixMessage, tag8Value);
        String result = visitor.visit(tree);
        return result;
    }
}
