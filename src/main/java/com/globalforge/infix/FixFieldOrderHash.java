package com.globalforge.infix;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.globalforge.infix.qfix.MessageData;

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
 * Determines the runtime order of a FIX field within a FIX message
 * @author Michael C. Starkie
 */
public class FixFieldOrderHash {
    /** logger */
    final static Logger logger = LoggerFactory.getLogger(FixFieldOrderHash.class);
    public static final String INIT_REF = "[0]->";
    public static final String REF = "->";
    /**
     * Assigns an increasing number to tag contexts as they appear in a fix
     * message thus preserving their order.
     */
    private int customTagPos = 1500000;
    // the precision is based on the largest repeating group. there should be no
    // group with more than 1000 members (000-999).
    // public static MathContext mathCtx = new MathContext(3,
    // RoundingMode.HALF_EVEN);
    private final MessageData msgData;

    public FixFieldOrderHash(MessageData m) {
        msgData = m;
    }

    /**
     * Given a message type and a FIX field in Infix syntax, determine it's
     * order in a FIX message.
     * @param msgType The message type
     * @param ctxString The field in Infix syntax.
     * @return BigDecimal the order of the field in the message
     */
    public BigDecimal getFieldPosition(String msgType, String ctxString) {
        BigDecimal ctxOrder = null;
        if (msgType == null) {
            // tested
            String fldOrder = msgData.getFieldOrderMap("0").getFieldOrder(ctxString);
            return new BigDecimal(fldOrder, MathContext.DECIMAL32);
        }
        boolean isGroupRef = FixFieldOrderHash.containsRef(ctxString);
        if (isGroupRef) {
            // not tested
            String genRef = ctxString.replaceAll("\\[\\d+\\]", "[*]");
            String fldOrder = msgData.getFieldOrderMap(msgType).getFieldOrder(genRef);
            if (fldOrder == null) { throw new RuntimeException(
                "No repeating group found in data dict for ctx=" + ctxString + ", msgType="
                    + msgType); }
            fldOrder = FixFieldOrderHash.replaceWildcards(fldOrder, ctxString);
            ctxOrder = new BigDecimal(fldOrder, MathContext.UNLIMITED);
            return ctxOrder;
        }
        String fldOrder = msgData.getFieldOrderMap(msgType).getFieldOrder(ctxString);
        if (fldOrder != null) {
            // tested
            return new BigDecimal(fldOrder, MathContext.DECIMAL32);
        }
        // tested
        return new BigDecimal(customTagPos++, MathContext.DECIMAL32);
    }

    /**
     * Determines if the context string contains a reference to repeating group
     * members.
     * @param ctxString The context
     * @return boolean
     */
    public static boolean containsRef(String ctxString) {
        int tagIdx = ctxString.indexOf(FixFieldOrderHash.REF);
        if (tagIdx < 0) { return false; }
        return true;
    }

    /**
     * Number after last '&'
     * @param ctxString Full context of field reference.
     * @return String a tag number
     */
    public static String getTagNumber(String ctxString) {
        int tagIdx = ctxString.lastIndexOf("&");
        String tagNum = ctxString.substring(tagIdx + 1);
        return tagNum;
    }

    /**
     * Get the raw tag number of a group identifier from an Infix context
     * @param ctxString the infix context
     * @return String a FIX field number pertaining to a group identifier
     */
    public static String getRootTagNumber(String ctxString) {
        int bracketIdx = ctxString.indexOf("[");
        if (bracketIdx < 0) { return ctxString.substring(1); }
        String groupIdCtx = ctxString.substring(1, bracketIdx);
        return groupIdCtx;
    }

    /**
     * Get the raw tag number of a group identifier as an Infix context from an
     * Infix context
     * @param ctxString the infix context
     * @return String a FIX field number pertaining to a group identifier in
     * Infix syntax.
     */
    public static String getRootTagCtx(String ctxString) {
        int bracketIdx = ctxString.indexOf("[");
        if (bracketIdx < 0) { return ctxString.substring(1); }
        String groupIdCtx = ctxString.substring(0, bracketIdx);
        return groupIdCtx;
    }

    /**
     * Returns the inner most nested group identifier in Infix syntax from an
     * Infix context.
     * @param ctxString Infix context
     * @return String
     */
    public static String getGroupIdCtx(String ctxString) {
        int bracketIdx = ctxString.lastIndexOf("[");
        if (bracketIdx < 0) { return ctxString; }
        String groupIdCtx = ctxString.substring(0, bracketIdx);
        return groupIdCtx;
    }

    /**
     * Returns the inner most nesting level of a repeating group in Infix
     * context
     * @param ctxString Infix context
     * @return int the inner most nesting level of a group member.
     */
    public static int getNestingLevel(String ctxString) {
        int bracketIdx = ctxString.lastIndexOf("[");
        if (bracketIdx < 0) { return -1; }
        char c = ctxString.charAt(bracketIdx + 1);
        int nestLevel = Character.getNumericValue(c);
        return nestLevel;
    }

    /**
     * Returns the outer most nesting level of a repeating group in Infix
     * context
     * @param ctxString Infix context
     * @return int the outer most nesting level of a group member.
     */
    public static int getFirstNestingLevel(String grpCtx) {
        int lBracket = grpCtx.indexOf("[");
        int rBracket = grpCtx.indexOf("]");
        String fistNestLevel = grpCtx.substring(lBracket + 1, rBracket);
        return Integer.parseInt(fistNestLevel);
    }

    /**
     * Replaces the order places holders from the data dictionary with actual
     * runtime nesting levels
     * @param orderCtx The compile time order from the data dictionary
     * @param grpCtx The runtime Infix context
     * @return String A runtime order for the field referenced by the group
     * context.
     */
    public static String replaceWildcards(String orderCtx, String grpCtx) {
        if (!orderCtx.contains("*")) { return orderCtx; }
        int nextLevel = FixFieldOrderHash.getFirstNestingLevel(grpCtx);
        // convert nextLevel into a 6 digit number.
        String mantissaPart = String.format("%06d", nextLevel);
        orderCtx = orderCtx.replaceFirst("(\\*)", mantissaPart + "");
        return FixFieldOrderHash.replaceWildcards(orderCtx,
            grpCtx.substring(grpCtx.indexOf(FixFieldOrderHash.REF) + 1));
    }

    /**
     * Testing only
     * @param args
     */
    public static void main(String[] args) {
        List<BigDecimal> sortList = new LinkedList<BigDecimal>();
        String grpRef = "&627[0]->&628";
        String genRef = grpRef.replaceAll("[0..9]", "*");
        System.out.println(genRef);
        grpRef = "&1310[3]->&1309[0]->&1141[1]->&1022";
        // &1310[3]->&1309[0]->&1141[1]->&1022
        genRef = grpRef.replaceAll("\\[\\d+\\]", "[*]");
        // "175.*864*875*25"
        System.out.println(genRef);
        String orderCtx = FixFieldOrderHash.replaceWildcards("175.*864*875*25", grpRef);
        BigDecimal val = new BigDecimal(orderCtx, MathContext.UNLIMITED);
        sortList.add(0, val);
        System.out.println(val);
        grpRef = "&1310[0]->&1309[0]->&1141[0]->&1022";
        orderCtx = FixFieldOrderHash.replaceWildcards("175.*864*875*25", grpRef);
        val = new BigDecimal(orderCtx, MathContext.UNLIMITED);
        sortList.add(0, val);
        System.out.println(val);
        grpRef = "&1310[0]->&1309[0]->&1141[1]->&1022";
        orderCtx = FixFieldOrderHash.replaceWildcards("175.*864*875*25", grpRef);
        val = new BigDecimal(orderCtx, MathContext.UNLIMITED);
        sortList.add(0, val);
        System.out.println(val);
        grpRef = "&1310[0]->&1309[0]->&1141[2]->&1022";
        orderCtx = FixFieldOrderHash.replaceWildcards("175.*864*875*25", grpRef);
        val = new BigDecimal(orderCtx, MathContext.UNLIMITED);
        sortList.add(0, val);
        System.out.println(val);
        grpRef = "&1310[0]->&1309[1]->&1141[0]->&1022";
        orderCtx = FixFieldOrderHash.replaceWildcards("175.*864*875*25", grpRef);
        val = new BigDecimal(orderCtx, MathContext.UNLIMITED);
        sortList.add(0, val);
        System.out.println(val);
        grpRef = "&1310[1]->&1309[0]->&1141[0]->&1022";
        orderCtx = FixFieldOrderHash.replaceWildcards("175.*864*875*25", grpRef);
        val = new BigDecimal(orderCtx, MathContext.UNLIMITED);
        sortList.add(0, val);
        System.out.println(val);
        grpRef = "&1310[1]->&1309[0]->&1141[1]->&1022";
        orderCtx = FixFieldOrderHash.replaceWildcards("175.*864*875*25", grpRef);
        val = new BigDecimal(orderCtx, MathContext.UNLIMITED);
        sortList.add(0, val);
        System.out.println(val);
        grpRef = "&1310[1]->&1309[0]->&1141[10]->&1022";
        orderCtx = FixFieldOrderHash.replaceWildcards("175.*864*875*25", grpRef);
        val = new BigDecimal(orderCtx, MathContext.UNLIMITED);
        sortList.add(0, val);
        System.out.println(val);
        grpRef = "&1310[10013]->&1309[0]->&1141[00]->&1022";
        orderCtx = FixFieldOrderHash.replaceWildcards("175.*864*875*25", grpRef);
        val = new BigDecimal(orderCtx, MathContext.UNLIMITED);
        sortList.add(0, val);
        System.out.println(val);
        grpRef = "&1310[999999]->&1309[999999]->&1141[999999]->&1022";
        orderCtx = FixFieldOrderHash.replaceWildcards("175.*864*875*25", grpRef);
        val = new BigDecimal(orderCtx, MathContext.UNLIMITED);
        sortList.add(0, val);
        System.out.println(val);
        System.out.println(sortList);
        Collections.sort(sortList);
        System.out.println(sortList);
        grpRef = "&555[0]->&600";
        // &1310[3]->&1309[0]->&1141[1]->&1022
        genRef = grpRef.replaceAll("\\[^(0..9)$\\]", "*");
        System.out.println(genRef);
    }
}
