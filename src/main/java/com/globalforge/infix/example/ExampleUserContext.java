package com.globalforge.infix.example;

import java.util.regex.Pattern;
import com.globalforge.infix.api.InfixAPI;
import com.globalforge.infix.api.InfixUserContext;

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
/**
 * An example implementation showing how to use user defined behavior
 * 
 * @author Michael C. Starkie
 */
public class ExampleUserContext implements InfixUserContext {
    @Override
    /**
     * User defined but the message returned must be legal for the fix version
     * otherwise the behavior is undefined.
     * 
     * @param fixMessage The message to manipulate with java code
     * @return String The new message to return to the parser.
     */
    public String visitMessage(String fixMessage) {
        return calculatePrice(fixMessage);
    }

    /**
     * Demonstrate how to change the state of a fix message as it resides in
     * memory before final transformation.
     * 
     * @param InfixMappingAPI A handle into the infix internals.
     * @see InfixAPI
     */
    @Override
    public void visitInfixAPI(InfixAPI infixApi) {
        infixApi.putContext("&44", Double.toString(Math.E));
    }

    /**
     * Demonstrate that you can write java to be called during a rule parse.
     * 
     * @param baseMsg The message to manipulate with java code
     * @return String The new message to return to the parser.
     */
    private String calculatePrice(String baseMsg) {
        double price = Math.PI;
        String[] msgArray =
            Pattern.compile(Character.toString((char) 0x01), Pattern.LITERAL)
                .split(baseMsg);
        StringBuilder newMsg = new StringBuilder();
        for (String field : msgArray) {
            int tagNum =
                Integer.parseInt(field.substring(0, field.indexOf("=")));
            if (tagNum == 44) {
                newMsg.append("44=" + price);
            } else {
                newMsg.append(field);
            }
            newMsg.append((char) 0x01);
        }
        return newMsg.toString();
    }
}
