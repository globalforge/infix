package com.globalforge.infix;

import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.globalforge.infix.qfix.FixGroupMgr;

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
 * A class used to assist the implementation of InfixAPI.
 * @see FixAPIImpl
 * @author Michael C. Starkie
 */
final class FixContextBuilder {
    /** logger */
    final static Logger logger = LoggerFactory.getLogger(FixContextBuilder.class);
    public static final String REF = "->";
    private LinkedBlockingDeque<String> ctxs = null;
    private String tagNum = null;

    public FixContextBuilder(String ctx) {
        parseContext(ctx);
    }

    /**
     * Converts a tag reference in rule sytax to a stack of context strings to
     * assist the group manager in locating the proper insertion order.
     * @see FixMessageMgr#putContext(Deque, String, String)
     * @see FixGroupMgr#getContext(String, String, String, boolean)
     * @see FixAPIImpl#putContext(String, String)
     * @param ctx A tag reference in rule syntax.
     */
    private void parseContext(String ctx) {
        LinkedBlockingDeque<String> stack = new LinkedBlockingDeque<String>();
        tagNum = ctx.substring(ctx.lastIndexOf("&") + 1);
        stack.push(ctx);
        buildContext(stack);
    }

    /**
     * Takes a tag reference in rule syntax and builds a nesting hierarchy if
     * the tag is a member of a repeating group. At least one of the items on
     * the stack below will be associated with an index representing the
     * position of the last memeber of a repeating group at the most recent
     * nesting leve.<br>
     * Example: &555[0]->&539[3]->&525 <br>
     * <code>
     * &555[0]->&539[3]->&525
     * &555[0]->&539[3]->
     * &555[0]->&539[2]->
     * &555[0]->&539[1]->
     * &555[0]->&539[0]->
     * &555[0]->&539
     * &555[0]->
     * &555
     * </code>
     * @see FixMessageMgr#putContext(Deque, String, String)
     * @see FixGroupMgr#getContext(String, String, String, boolean)
     * @see FixAPIImpl#putContext(String, String)
     */
    private void buildContext(LinkedBlockingDeque<String> contexts) {
        String top = contexts.peek();
        if (!top.contains(FixContextBuilder.REF)) {
            ctxs = this.reverseStack(contexts);
            return;
        }
        if (top.endsWith(FixContextBuilder.REF)) {
            // Isolate the tag index. If > 0, decrement and return else strip
            // off the tag index and ref.
            int firstBrk = top.lastIndexOf("[");
            int secondBrk = top.lastIndexOf("]");
            String beforeLastIdx = top.substring(0, firstBrk + 1);
            String afterLastIdx = top.substring(secondBrk);
            char idxChar = top.charAt(firstBrk + 1);
            int idx = Character.getNumericValue(idxChar);
            String nextTop = null;
            while (idx-- > 0) {
                nextTop = beforeLastIdx + idx + afterLastIdx;
                contexts.push(nextTop);
            }
            nextTop = top.substring(0, firstBrk);
            contexts.push(nextTop);
        } else {
            // strip off the tag ref, push what is lef onto the stack and
            // return;
            int refIdx = top.lastIndexOf(FixContextBuilder.REF);
            String nextTop = top.substring(0, refIdx + FixContextBuilder.REF.length());
            contexts.push(nextTop);
        }
        buildContext(contexts);
    }

    /**
     * Return the build context.
     * @see FixContextBuilder#buildContext
     * @return LinkedBlockingDeque<String>
     */
    LinkedBlockingDeque<String> getContexts() {
        return ctxs;
    }

    /**
     * Return the tag number associated with this context.
     * @return String the tag number associated with the context.
     */
    String getTagNum() {
        return this.tagNum;
    }

    /**
     * Reverses the stack of the context hierarchy in the order needed by the
     * FixFieldMgr.
     * @see FixMessageMgr#putContext(Deque, String, String)
     * @param stack The context of the context indicators.
     * @return LinkedBlockingDeque<String> A reversal of the parameter.
     */
    private LinkedBlockingDeque<String> reverseStack(LinkedBlockingDeque<String> stack) {
        LinkedBlockingDeque<String> reversedStack = new LinkedBlockingDeque<String>();
        Iterator<String> it = stack.iterator();
        while (it.hasNext()) {
            String ctx = it.next();
            reversedStack.push(ctx);
        }
        return reversedStack;
    }

    /**
     * Prints the context stack for debugging purposes.
     * @param ctxs The context indicators for the tag.
     */
    static void printCtx(Deque<String> ctxs) {
        FixContextBuilder.logger.info("----- begin ctxs -----");
        Iterator<String> it = ctxs.iterator();
        while (it.hasNext()) {
            String ctx = it.next();
            FixContextBuilder.logger.info(ctx);
        }
        FixContextBuilder.logger.info("------ end ctxs ------");
    }

    /**
     * For debugging purposes.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Contexts: \n");
        Iterator<String> it = getContexts().iterator();
        while (it.hasNext()) {
            String ctx = it.next();
            sb.append(ctx).append("\n");
        }
        sb.append("Tag Number: ").append(tagNum);
        return sb.toString();
    }

    /**
     * Test it out.
     * @param args
     */
    public static void main(String[] args) {
        String ctx = "&555[0]->&539[3]->&525";
        FixContextBuilder ctxBldr = new FixContextBuilder(ctx);
        System.out.println("-------------");
        String out = ctxBldr.toString();
        System.out.println(out);
        ctx = "&555";
        ctxBldr = new FixContextBuilder(ctx);
        System.out.println("-------------");
        out = ctxBldr.toString();
        System.out.println(out);
        ctx = "&555[0]->&539";
        ctxBldr = new FixContextBuilder(ctx);
        System.out.println("-------------");
        out = ctxBldr.toString();
        System.out.println(out);
        ctx = "&555[1]->&539";
        ctxBldr = new FixContextBuilder(ctx);
        System.out.println("-------------");
        out = ctxBldr.toString();
        System.out.println(out);
        ctx = "&555[2]->&539[3]->&525";
        ctxBldr = new FixContextBuilder(ctx);
        System.out.println("-------------");
        out = ctxBldr.toString();
        System.out.println(out);
        ctx = "&555[3]->&444";
        ctxBldr = new FixContextBuilder(ctx);
        System.out.println("-------------");
        out = ctxBldr.toString();
        System.out.println(out);
    }
}
