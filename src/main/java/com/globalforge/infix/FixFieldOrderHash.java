package com.globalforge.infix;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.globalforge.infix.qfix.MessageData;

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

    public BigDecimal getFieldPosition(String msgType, String ctxString) {
        BigDecimal ctxOrder = null;
        if (msgType == null) {
            // tested
            String fldOrder = msgData.getFieldOrderMap("0").getFieldOrder(ctxString);
            return new BigDecimal(fldOrder, MathContext.DECIMAL32);
        }
        boolean isGroupRef = containsRef(ctxString);
        if (isGroupRef) {
            // not tested
            String genRef = ctxString.replaceAll("\\[\\d+\\]", "[*]");
            String fldOrder = msgData.getFieldOrderMap(msgType).getFieldOrder(genRef);
            if (fldOrder == null) {
                throw new RuntimeException("No repeating group found in data dict for ctx="
                    + ctxString + ", msgType=" + msgType);
            }
            fldOrder = replaceWildcards(fldOrder, ctxString);
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

    public static boolean containsRef(String ctxString) {
        int tagIdx = ctxString.indexOf(REF);
        if (tagIdx < 0) {
            return false;
        }
        return true;
    }

    /**
     * Number after last '&'
     * 
     * @param ctxString Full context of field reference.
     * @return String a tag number
     */
    public static String getTagNumber(String ctxString) {
        int tagIdx = ctxString.lastIndexOf("&");
        String tagNum = ctxString.substring(tagIdx + 1);
        return tagNum;
    }

    public static String getRootTagNumber(String ctxString) {
        int bracketIdx = ctxString.indexOf("[");
        if (bracketIdx < 0) {
            return ctxString.substring(1);
        }
        String groupIdCtx = ctxString.substring(1, bracketIdx);
        return groupIdCtx;
    }

    public static String getRootTagCtx(String ctxString) {
        int bracketIdx = ctxString.indexOf("[");
        if (bracketIdx < 0) {
            return ctxString.substring(1);
        }
        String groupIdCtx = ctxString.substring(0, bracketIdx);
        return groupIdCtx;
    }

    public static String getGroupIdCtx(String ctxString) {
        int bracketIdx = ctxString.lastIndexOf("[");
        if (bracketIdx < 0) {
            return ctxString;
        }
        String groupIdCtx = ctxString.substring(0, bracketIdx);
        return groupIdCtx;
    }

    public static int getNestingLevel(String ctxString) {
        int bracketIdx = ctxString.lastIndexOf("[");
        if (bracketIdx < 0) {
            return -1;
        }
        char c = ctxString.charAt(bracketIdx + 1);
        int nestLevel = Character.getNumericValue(c);
        return nestLevel;
    }

    public static int getFirstNestingLevel(String grpCtx) {
        int lBracket = grpCtx.indexOf("[");
        int rBracket = grpCtx.indexOf("]");
        String fistNestLevel = grpCtx.substring(lBracket + 1, rBracket);
        return Integer.parseInt(fistNestLevel);
    }

    public static String replaceWildcards(String orderCtx, String grpCtx) {
        if (!orderCtx.contains("*")) {
            return orderCtx;
        }
        int nextLevel = getFirstNestingLevel(grpCtx);
        // convert nextLevel into a 6 digit number.
        String mantissaPart = String.format("%06d", nextLevel);
        orderCtx = orderCtx.replaceFirst("(\\*)", mantissaPart + "");
        return replaceWildcards(orderCtx, grpCtx.substring(grpCtx.indexOf(REF) + 1));
    }

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
        String orderCtx = replaceWildcards("175.*864*875*25", grpRef);
        BigDecimal val = new BigDecimal(orderCtx, MathContext.UNLIMITED);
        sortList.add(0, val);
        System.out.println(val);
        grpRef = "&1310[0]->&1309[0]->&1141[0]->&1022";
        orderCtx = replaceWildcards("175.*864*875*25", grpRef);
        val = new BigDecimal(orderCtx, MathContext.UNLIMITED);
        sortList.add(0, val);
        System.out.println(val);
        grpRef = "&1310[0]->&1309[0]->&1141[1]->&1022";
        orderCtx = replaceWildcards("175.*864*875*25", grpRef);
        val = new BigDecimal(orderCtx, MathContext.UNLIMITED);
        sortList.add(0, val);
        System.out.println(val);
        grpRef = "&1310[0]->&1309[0]->&1141[2]->&1022";
        orderCtx = replaceWildcards("175.*864*875*25", grpRef);
        val = new BigDecimal(orderCtx, MathContext.UNLIMITED);
        sortList.add(0, val);
        System.out.println(val);
        grpRef = "&1310[0]->&1309[1]->&1141[0]->&1022";
        orderCtx = replaceWildcards("175.*864*875*25", grpRef);
        val = new BigDecimal(orderCtx, MathContext.UNLIMITED);
        sortList.add(0, val);
        System.out.println(val);
        grpRef = "&1310[1]->&1309[0]->&1141[0]->&1022";
        orderCtx = replaceWildcards("175.*864*875*25", grpRef);
        val = new BigDecimal(orderCtx, MathContext.UNLIMITED);
        sortList.add(0, val);
        System.out.println(val);
        grpRef = "&1310[1]->&1309[0]->&1141[1]->&1022";
        orderCtx = replaceWildcards("175.*864*875*25", grpRef);
        val = new BigDecimal(orderCtx, MathContext.UNLIMITED);
        sortList.add(0, val);
        System.out.println(val);
        grpRef = "&1310[1]->&1309[0]->&1141[10]->&1022";
        orderCtx = replaceWildcards("175.*864*875*25", grpRef);
        val = new BigDecimal(orderCtx, MathContext.UNLIMITED);
        sortList.add(0, val);
        System.out.println(val);
        grpRef = "&1310[10013]->&1309[0]->&1141[00]->&1022";
        orderCtx = replaceWildcards("175.*864*875*25", grpRef);
        val = new BigDecimal(orderCtx, MathContext.UNLIMITED);
        sortList.add(0, val);
        System.out.println(val);
        grpRef = "&1310[999999]->&1309[999999]->&1141[999999]->&1022";
        orderCtx = replaceWildcards("175.*864*875*25", grpRef);
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
