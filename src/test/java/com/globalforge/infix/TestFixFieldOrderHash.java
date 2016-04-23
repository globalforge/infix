package com.globalforge.infix;

import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;
import com.globalforge.infix.qfix.MessageData;

public class TestFixFieldOrderHash {
    // FIX44Mgr grpMgr = new FIX44Mgr();
    @Test
    public void t1() throws Exception {
        MessageData msgData = FixContextMgr.getInstance().getMessageData("FIX.4.4");
        FixFieldOrderHash orderHash = new FixFieldOrderHash(msgData);
        BigDecimal pos = orderHash.getFieldPosition("AS", "&42");
        org.junit.Assert.assertEquals("1500000", pos.toString());
        pos = orderHash.getFieldPosition("AS", "&44");
        org.junit.Assert.assertEquals("1500001", pos.toString());
    }

    @Test
    public void t1_A() throws Exception {
        MessageData msgData = FixContextMgr.getInstance().getMessageData("FIX.4.4");
        FixFieldOrderHash orderHash = new FixFieldOrderHash(msgData);
        BigDecimal pos = orderHash.getFieldPosition("AS", "&8");
        org.junit.Assert.assertEquals("1", pos.toString());
        pos = orderHash.getFieldPosition("AS", "&9");
        org.junit.Assert.assertEquals("2", pos.toString());
        pos = orderHash.getFieldPosition("AS", "&35");
        org.junit.Assert.assertEquals("3", pos.toString());
    }

    @Test
    public void t1_B() throws Exception {
        MessageData msgData = FixContextMgr.getInstance().getMessageData("FIX.4.4");
        FixFieldOrderHash orderHash = new FixFieldOrderHash(msgData);
        BigDecimal pos = orderHash.getFieldPosition(null, "&8");
        org.junit.Assert.assertEquals("1", pos.toString());
        pos = orderHash.getFieldPosition(null, "&9");
        org.junit.Assert.assertEquals("2", pos.toString());
        pos = orderHash.getFieldPosition(null, "&35");
        org.junit.Assert.assertEquals("3", pos.toString());
    }

    // &555[0]->&539[0]->&804[0]->&545
    @Test
    public void t2() throws Exception {
        MessageData msgData = FixContextMgr.getInstance().getMessageData("FIX.4.4");
        FixFieldOrderHash orderHash = new FixFieldOrderHash(msgData);
        BigDecimal pos = orderHash.getFieldPosition("AS", "&555");
        double d1 = 0d;
        double d2 = pos.doubleValue();
        org.junit.Assert.assertTrue(d1 < d2);
        System.out.println(pos);
        org.junit.Assert.assertEquals("90", pos.toString());
        pos = orderHash.getFieldPosition("AS", "&555[0]->&600");
        d1 = pos.doubleValue();
        System.out.println(pos);
        Assert.assertTrue(d1 > d2);
        pos = orderHash.getFieldPosition("AS", "&555[0]->&251");
        d2 = pos.doubleValue();
        System.out.println(pos);
        Assert.assertTrue(d2 > d1);
        pos = orderHash.getFieldPosition("AS", "&555[0]->&250");
        d1 = pos.doubleValue();
        Assert.assertTrue(d1 < d2);
        System.out.println(pos);
        pos = orderHash.getFieldPosition("AS", "&555[0]->&252");
        d2 = pos.doubleValue();
        Assert.assertTrue(d2 > d1);
        System.out.println(pos);
        pos = orderHash.getFieldPosition("AS", "&555[0]->&609");
        d1 = pos.doubleValue();
        Assert.assertTrue(d1 < d2);
        System.out.println(pos);
        pos = orderHash.getFieldPosition("AS", "&555[0]->&608");
        d2 = pos.doubleValue();
        Assert.assertTrue(d2 < d1);
        System.out.println(pos);
        pos = orderHash.getFieldPosition("AS", "&555[1]->&600");
        d1 = pos.doubleValue();
        System.out.println(pos);
        Assert.assertTrue(d1 > d2);
        pos = orderHash.getFieldPosition("AS", "&555[1]->&604");
        d2 = pos.doubleValue();
        System.out.println(pos);
        Assert.assertTrue(d1 < d2);
        BigDecimal pos2 = orderHash.getFieldPosition("AS", "&555[1]->&604[0]->&605");
        System.out.println(pos2.toEngineeringString());
        Assert.assertTrue(pos2.compareTo(pos) == 1);
    }

    // New Order List, Vol. 4, page 182
    // Vol. 1, page 20
    @Test
    public void t3() throws Exception {
        String msgType = "E";
        MessageData msgData = FixContextMgr.getInstance().getMessageData("FIX.4.4");
        FixFieldOrderHash orderHash = new FixFieldOrderHash(msgData);
        // &390
        BigDecimal ord = orderHash.getFieldPosition(msgType, "&390");
        System.out.println("&390=" + ord.toPlainString());
        // &66
        ord = orderHash.getFieldPosition(msgType, "&66");
        System.out.println("&66=" + ord.toPlainString());
        // &73 = 2 (NoOrders)
        BigDecimal prev = orderHash.getFieldPosition(msgType, "&73");
        // &73[1]->&67
        System.out.println("&73=" + prev.toPlainString());
        BigDecimal next = orderHash.getFieldPosition(msgType, "&73[1]->&67");
        BigDecimal m73$167 = next;
        System.out.println("&73[1]->&67= " + next.toPlainString());
        Assert.assertTrue(next.compareTo(prev) == 1);
        // &73[1]->&11
        prev = orderHash.getFieldPosition(msgType, "&73[1]->&11");
        BigDecimal m73$111 = prev;
        System.out.println("&73[1]->&11= " + prev.toPlainString());
        Assert.assertTrue(prev.compareTo(next) == -1);
        // &73[1]->&526
        next = orderHash.getFieldPosition(msgType, "&73[1]->&526");
        BigDecimal m73$526 = next;
        System.out.println("&73[1]->&526=" + next.toPlainString());
        Assert.assertTrue(next.compareTo(prev) == 1);
        // &73[1]->&78 = 2 (NoAllocs)
        prev = orderHash.getFieldPosition(msgType, "&73[1]->&78");
        BigDecimal m73$78 = prev;
        System.out.println("&73[1]->&78= " + prev.toPlainString());
        Assert.assertTrue(prev.compareTo(next) == 1);
        // &73[1]->&78[0]->&467
        next = orderHash.getFieldPosition(msgType, "&73[1]->&78[0]->&467");
        System.out.println("&73[1]->&78[0]->&467=" + next.toPlainString());
        Assert.assertTrue(next.compareTo(prev) == 1);
        // &73[1]->&78[0]->&79
        prev = orderHash.getFieldPosition(msgType, "&73[1]->&78[0]->&79");
        System.out.println("&73[1]->&78[0]->&79= " + prev.toPlainString());
        Assert.assertTrue(prev.compareTo(next) == -1);
        // &73[1]->&78[0]->&539 = 2
        next = orderHash.getFieldPosition(msgType, "&73[1]->&78[0]->&539");
        BigDecimal m73$78$539 = next;
        System.out.println("&73[1]->&78[0]->&539=" + next.toPlainString());
        Assert.assertTrue(next.compareTo(prev) == 1);
        // &73[1]->&78[0]->&467
        prev = orderHash.getFieldPosition(msgType, "&73[1]->&78[0]->&467");
        BigDecimal m73$1$78$467 = prev;
        System.out.println("&73[1]->&78[0]->&467=" + prev.toPlainString());
        Assert.assertTrue(prev.compareTo(next) == -1);
        // &73[1]->&78[0]->&539[1]->&538
        next = orderHash.getFieldPosition(msgType, "&73[1]->&78[0]->&539[1]->&538");
        BigDecimal m73$78$539$538 = next;
        System.out.println("&73[1]->&78[0]->&539[1]->&538=" + next.toPlainString());
        Assert.assertTrue(next.compareTo(prev) == 1);
        // &73[1]->&78[0]->&539[1]->&524
        prev = orderHash.getFieldPosition(msgType, "&73[1]->&78[0]->&539[1]->&524");
        BigDecimal m73$78$539$524 = prev;
        System.out.println("&73[1]->&78[0]->&539[1]->&524=" + prev.toPlainString());
        Assert.assertTrue(prev.compareTo(next) == -1);
        // &73[1]->&78[0]->&539[1]->&804 = 1
        next = orderHash.getFieldPosition(msgType, "&73[1]->&78[0]->&539[1]->&804");
        BigDecimal m73$78$539$804 = next;
        System.out.println("&73[1]->&78[0]->&539[1]->&804=" + next.toPlainString());
        Assert.assertTrue(next.compareTo(prev) == 1);
        // &73[1]->&78[0]->&539[1]->&804[0]->&545
        prev = orderHash.getFieldPosition(msgType, "&73[1]->&78[0]->&539[1]->&804[0]->&545");
        BigDecimal m73$78$539$804$545 = prev;
        BigDecimal m1010 = prev;
        System.out.println("&73[1]->&78[0]->&539[1]->&804[0]->&545=" + prev.toPlainString());
        Assert.assertTrue(prev.compareTo(next) == 1);
        // &73[1]->&78[0]->&539[1]->&804[0]->&805
        next = orderHash.getFieldPosition(msgType, "&73[1]->&78[0]->&539[1]->&804[0]->&805");
        BigDecimal m73$78$539$804$805 = next;
        System.out.println("&73[1]->&78[0]->&539[1]->&804[0]->&805=" + next.toPlainString());
        Assert.assertTrue(next.compareTo(prev) == 1);
        // &73[0]->&67
        prev = orderHash.getFieldPosition(msgType, "&73[0]->&67");
        System.out.println("&73[0]->&67= " + prev.toPlainString());
        Assert.assertTrue(prev.compareTo(m73$167) == -1);
        Assert.assertTrue(prev.compareTo(next) == -1);
        // &73[0]->&11
        next = orderHash.getFieldPosition(msgType, "&73[0]->&11");
        System.out.println("&73[0]->&11= " + next.toPlainString());
        Assert.assertTrue(next.compareTo(m73$111) == -1);
        Assert.assertTrue(next.compareTo(prev) == -1);
        // &73[0]->&526
        prev = orderHash.getFieldPosition(msgType, "&73[0]->&526");
        System.out.println("&73[0]->&526=" + prev.toPlainString());
        Assert.assertTrue(prev.compareTo(m73$526) == -1);
        Assert.assertTrue(prev.compareTo(next) == 1);
        // &73[0]->&78 = 1 (NoAllocs)
        next = orderHash.getFieldPosition(msgType, "&73[0]->&78");
        System.out.println("&73[0]->&78= " + next.toPlainString());
        Assert.assertTrue(next.compareTo(m73$78) == -1);
        Assert.assertTrue(next.compareTo(prev) == 1);
        // &73[0]->&78[0]->&467
        prev = orderHash.getFieldPosition(msgType, "&73[0]->&78[0]->&467");
        System.out.println("&73[0]->&78[0]->&467=" + prev.toPlainString());
        Assert.assertTrue(prev.compareTo(m73$1$78$467) == -1);
        Assert.assertTrue(prev.compareTo(next) == 1);
        // &73[0]->&78[0]->&79
        next = orderHash.getFieldPosition(msgType, "&73[0]->&78[0]->&79");
        System.out.println("&73[0]->&78[0]->&79= " + next.toPlainString());
        Assert.assertTrue(next.compareTo(prev) == -1);
        // &73[0]->&78[0]->&539
        prev = orderHash.getFieldPosition(msgType, "&73[0]->&78[0]->&539");
        System.out.println("&73[0]->&78[0]->&539=" + prev.toPlainString());
        Assert.assertTrue(prev.compareTo(m73$78$539) == -1);
        Assert.assertTrue(prev.compareTo(next) == 1);
        // &73[0]->&78[0]->&539[0]->&538
        next = orderHash.getFieldPosition(msgType, "&73[0]->&78[0]->&539[0]->&538");
        System.out.println("&73[0]->&78[0]->&539[0]->&538= " + next.toPlainString());
        Assert.assertTrue(next.compareTo(m73$78$539$538) == -1);
        Assert.assertTrue(next.compareTo(prev) == 1);
        // &73[0]->&78[0]->&539[0]->&524
        prev = orderHash.getFieldPosition(msgType, "&73[0]->&78[0]->&539[0]->&524");
        System.out.println("&73[0]->&78[0]->&539[0]->&524= " + prev.toPlainString());
        Assert.assertTrue(prev.compareTo(m73$78$539$524) == -1);
        Assert.assertTrue(prev.compareTo(next) == -1);
        // &73[0]->&78[0]->&539[0]->&804 = 2
        next = orderHash.getFieldPosition(msgType, "&73[0]->&78[0]->&539[0]->&804");
        System.out.println("&73[0]->&78[0]->&539[0]->&804= " + next.toPlainString());
        Assert.assertTrue(next.compareTo(m73$78$539$804) == -1);
        Assert.assertTrue(next.compareTo(prev) == 1);
        // &73[0]->&78[0]->&539[0]->&804[0]->&545
        prev = orderHash.getFieldPosition(msgType, "&73[0]->&78[0]->&539[0]->&804[0]->&545");
        BigDecimal m0000 = prev;
        System.out.println("&73[0]->&78[0]->&539[0]->&804[0]->&545=" + prev.toPlainString());
        Assert.assertTrue(prev.compareTo(m73$78$539$804$545) == -1);
        Assert.assertTrue(prev.compareTo(next) == 1);
        // &73[0]->&78[0]->&539[0]->&804[0]->&805
        next = orderHash.getFieldPosition(msgType, "&73[0]->&78[0]->&539[0]->&804[0]->&805");
        System.out.println("&73[0]->&78[0]->&539[0]->&804[0]->&805=" + next.toPlainString());
        Assert.assertTrue(next.compareTo(prev) == 1);
        // &73[0]->&78[0]->&539[0]->&804[1]->&545
        prev = orderHash.getFieldPosition(msgType, "&73[0]->&78[0]->&539[0]->&804[1]->&545");
        BigDecimal m0001 = prev;
        System.out.println("&73[0]->&78[0]->&539[0]->&804[1]->&545=" + prev.toPlainString());
        Assert.assertTrue(prev.compareTo(next) == 1);
        // &73[0]->&78[0]->&539[0]->&804[1]->&805
        next = orderHash.getFieldPosition(msgType, "&73[0]->&78[0]->&539[0]->&804[1]->&805");
        System.out.println("&73[0]->&78[0]->&539[0]->&804[1]->&805=" + next.toPlainString());
        Assert.assertTrue(next.compareTo(m73$78$539$804$805) == -1);
        Assert.assertTrue(next.compareTo(prev) == 1);
        // &73[1]->&78[1]->&467
        prev = orderHash.getFieldPosition(msgType, "&73[1]->&78[1]->&467");
        System.out.println("&73[1]->&78[1]->&467=" + prev.toPlainString());
        Assert.assertTrue(prev.compareTo(next) == 1);
        Assert.assertTrue(prev.compareTo(m73$1$78$467) == 1);
        // &73[1]->&78[1]->&79
        next = orderHash.getFieldPosition(msgType, "&73[1]->&78[1]->&79");
        System.out.println("&73[1]->&78[1]->&79= " + next.toPlainString());
        Assert.assertTrue(next.compareTo(prev) == -1);
        // &73[1]->&78[1]->&539 = 1
        prev = orderHash.getFieldPosition(msgType, "&73[1]->&78[1]->&539");
        System.out.println("&73[1]->&78[1]->&539=" + prev.toPlainString());
        Assert.assertTrue(prev.compareTo(next) == 1);
        // &73[1]->&78[1]->&539[0]->&538
        next = orderHash.getFieldPosition(msgType, "&73[1]->&78[1]->&539[0]->&538");
        System.out.println("&73[1]->&78[1]->&539[0]->&538= " + next.toPlainString());
        Assert.assertTrue(next.compareTo(prev) == 1);
        // &73[1]->&78[1]->&539[0]->&524
        prev = orderHash.getFieldPosition(msgType, "&73[1]->&78[1]->&539[0]->&524");
        System.out.println("&73[1]->&78[1]->&539[0]->&524= " + prev.toPlainString());
        Assert.assertTrue(prev.compareTo(next) == -1);
        // &73[1]->&78[1]->&539[0]->&804 = 2
        next = orderHash.getFieldPosition(msgType, "&73[1]->&78[1]->&539[0]->&804");
        System.out.println("&73[1]->&78[1]->&539[0]->&804= " + next.toPlainString());
        Assert.assertTrue(next.compareTo(prev) == 1);
        // &73[1]->&78[1]->&539[0]->&804[0]->&545
        prev = orderHash.getFieldPosition(msgType, "&73[1]->&78[1]->&539[0]->&804[0]->&545");
        BigDecimal m1100 = prev;
        System.out.println("&73[1]->&78[1]->&539[0]->&804[0]->&545= " + prev.toPlainString());
        Assert.assertTrue(prev.compareTo(next) == 1);
        // &73[1]->&78[1]->&539[0]->&804[0]->&805
        next = orderHash.getFieldPosition(msgType, "&73[1]->&78[1]->&539[0]->&804[0]->&805");
        System.out.println("&73[1]->&78[1]->&539[0]->&804[0]->&805= " + next.toPlainString());
        Assert.assertTrue(next.compareTo(prev) == 1);
        // &73[1]->&78[1]->&539[0]->&804[1]->&805
        prev = orderHash.getFieldPosition(msgType, "&73[1]->&78[1]->&539[0]->&804[1]->&805");
        System.out.println("&73[1]->&78[1]->&539[0]->&804[1]->&805= " + prev.toPlainString());
        Assert.assertTrue(prev.compareTo(next) == 1);
        // &73[1]->&78[1]->&539[0]->&804[1]->&545
        next = orderHash.getFieldPosition(msgType, "&73[1]->&78[1]->&539[0]->&804[1]->&545");
        BigDecimal m1101 = next;
        System.out.println("&73[1]->&78[1]->&539[0]->&804[1]->&545= " + next.toPlainString());
        Assert.assertTrue(next.compareTo(prev) == -1);
        // &73[0]->&78[0]->&539[0]->&804[0]->&545
        // &73[0]->&78[0]->&539[0]->&804[1]->&545
        Assert.assertTrue(m0001.compareTo(m0000) == 1);
        // &73[1]->&78[0]->&539[1]->&804[0]->&545
        Assert.assertTrue(m1010.compareTo(m0001) == 1);
        // &73[1]->&78[1]->&539[0]->&804[0]->&545
        Assert.assertTrue(m1100.compareTo(m1010) == 1);
        // &73[1]->&78[1]->&539[0]->&804[1]->&545
        Assert.assertTrue(m1101.compareTo(m1100) == 1);
    }
}
