package com.globalforge.infix;

import org.junit.Assert;
import org.junit.Test;
import com.globalforge.infix.qfix.FixGroupMgr;
import com.globalforge.infix.qfix.FixRepeatingGroup;
import com.globalforge.infix.qfix.fix50.auto.group.FIX50_3_51_GroupMgr;
import com.globalforge.infix.qfix.fix50.auto.group.FIX50_8_56_GroupMgr;
import com.globalforge.infix.qfix.fix50.auto.group.FIX50_8_56_GroupMgr.Msg_8_56_Group_382;
import com.globalforge.infix.qfix.fix50.auto.group.FIX50_8_56_GroupMgr.Msg_8_56_Group_711;
import com.globalforge.infix.qfix.fix50.auto.group.FIX50_AS_2098_GroupMgr;
import com.globalforge.infix.qfix.fix50.auto.group.FIX50_D_68_GroupMgr;
import com.globalforge.infix.qfix.fix50.auto.group.FIX50_E_69_GroupMgr;

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
public class TestGrp50Mgr {
    /**
     * Test to make sure that a given message type contains expected group.
     */
    @Test
    public void testContainsGrpId() {
        FixGroupMgr grpMgr = new FIX50_3_51_GroupMgr();
        boolean containsGrpId = grpMgr.containsGrpId("382");
        Assert.assertFalse(containsGrpId);
        grpMgr = new FIX50_8_56_GroupMgr();
        containsGrpId = grpMgr.containsGrpId("382");
        Assert.assertTrue(containsGrpId);
        containsGrpId = grpMgr.containsGrpId("539");
        Assert.assertTrue(containsGrpId);
        // nested group
        containsGrpId = grpMgr.containsGrpId("804");
        Assert.assertTrue(containsGrpId);
    }

    /**
     * test to make sure an expected group is of the right type.
     */
    @Test
    public void testGetGroup() {
        FixGroupMgr grpMgr = new FIX50_8_56_GroupMgr();
        FixRepeatingGroup grp = grpMgr.getGroup("382");
        Assert.assertTrue(grp instanceof Msg_8_56_Group_382);
        grpMgr = new FIX50_D_68_GroupMgr();
        grp = grpMgr.getGroup("382");
        Assert.assertNull(grp);
    }

    /**
     * Test a repeating group where the members are entirely defined by a
     * seperate component. In this case, the group UndInstrmtGrp contains
     * NoUnderlyings (711) field followed by repeats of the UnderlyingInstrument
     * component.
     */
    @Test
    public void testGetComponenentMemberFromGroup() {
        FixGroupMgr grpMgr = new FIX50_8_56_GroupMgr();
        FixRepeatingGroup grp = grpMgr.getGroup("711");
        Assert.assertTrue(grp instanceof Msg_8_56_Group_711);
        Assert.assertTrue(grp.containsMember("311"));
    }

    /**
     * non repeating field.
     */
    @Test
    public void testGetContext1() {
        FixGroupMgr grpMgr = new FIX50_D_68_GroupMgr();
        String ctx = grpMgr.getContext("42");
        Assert.assertEquals(ctx, "42");
    }

    /**
     * Context stack is reset after non-member field.
     */
    @Test
    public void testGetContext2() {
        FixGroupMgr grpMgr = new FIX50_AS_2098_GroupMgr();
        String ctx = grpMgr.getContext("555");
        Assert.assertEquals(ctx, "555");
        ctx = grpMgr.getContext("38");
        Assert.assertEquals(ctx, "38");
    }

    @Test
    public void testGetContext3() {
        FixGroupMgr grpMgr = new FIX50_AS_2098_GroupMgr();
        String ctx = grpMgr.getContext("555");
        Assert.assertEquals(ctx, "555");
        ctx = grpMgr.getContext("600");
        Assert.assertEquals(ctx, "555[0]->600");
        ctx = grpMgr.getContext("601");
        Assert.assertEquals(ctx, "555[0]->601");
        ctx = grpMgr.getContext("604");
        Assert.assertEquals(ctx, "555[0]->604");
        ctx = grpMgr.getContext("605");
        Assert.assertEquals(ctx, "555[0]->604[0]->605");
        ctx = grpMgr.getContext("606");
        Assert.assertEquals(ctx, "555[0]->604[0]->606");
        ctx = grpMgr.getContext("605");
        Assert.assertEquals(ctx, "555[0]->604[1]->605");
        ctx = grpMgr.getContext("606");
        Assert.assertEquals(ctx, "555[0]->604[1]->606");
        ctx = grpMgr.getContext("942");
        Assert.assertEquals(ctx, "555[0]->942");
        ctx = grpMgr.getContext("600");
        Assert.assertEquals(ctx, "555[1]->600");
        ctx = grpMgr.getContext("604");
        Assert.assertEquals(ctx, "555[1]->604");
        ctx = grpMgr.getContext("605");
        Assert.assertEquals(ctx, "555[1]->604[0]->605");
        ctx = grpMgr.getContext("606");
        Assert.assertEquals(ctx, "555[1]->604[0]->606");
        ctx = grpMgr.getContext("605");
        Assert.assertEquals(ctx, "555[1]->604[1]->605");
        ctx = grpMgr.getContext("606");
        Assert.assertEquals(ctx, "555[1]->604[1]->606");
        ctx = grpMgr.getContext("942");
        Assert.assertEquals(ctx, "555[1]->942");
        ctx = grpMgr.getContext("38");
        Assert.assertEquals(ctx, "38");
    }

    @Test
    public void testGetContext5() {
        FixGroupMgr grpMgr = new FIX50_E_69_GroupMgr();
        String ctx = grpMgr.getContext("35");
        Assert.assertEquals(ctx, "35");
        ctx = grpMgr.getContext("66");
        Assert.assertEquals(ctx, "66");
        ctx = grpMgr.getContext("394");
        Assert.assertEquals(ctx, "394");
        ctx = grpMgr.getContext("73");
        Assert.assertEquals(ctx, "73");
        ctx = grpMgr.getContext("11");
        Assert.assertEquals(ctx, "73[0]->11");
        ctx = grpMgr.getContext("67");
        Assert.assertEquals(ctx, "73[0]->67");
        ctx = grpMgr.getContext("78");
        Assert.assertEquals(ctx, "73[0]->78");
        ctx = grpMgr.getContext("79");
        Assert.assertEquals(ctx, "73[0]->78[0]->79");
        //
        ctx = grpMgr.getContext("661");
        Assert.assertEquals(ctx, "73[0]->78[0]->661");
        ctx = grpMgr.getContext("736");
        Assert.assertEquals(ctx, "73[0]->78[0]->736");
        ctx = grpMgr.getContext("467");
        Assert.assertEquals(ctx, "73[0]->78[0]->467");
        ctx = grpMgr.getContext("539");
        Assert.assertEquals(ctx, "73[0]->78[0]->539");
        ctx = grpMgr.getContext("524");
        Assert.assertEquals(ctx, "73[0]->78[0]->539[0]->524");
        ctx = grpMgr.getContext("538");
        Assert.assertEquals(ctx, "73[0]->78[0]->539[0]->538");
        ctx = grpMgr.getContext("524");
        Assert.assertEquals(ctx, "73[0]->78[0]->539[1]->524");
        ctx = grpMgr.getContext("525");
        Assert.assertEquals(ctx, "73[0]->78[0]->539[1]->525");
        ctx = grpMgr.getContext("79");
        Assert.assertEquals(ctx, "73[0]->78[1]->79");
        ctx = grpMgr.getContext("661");
        Assert.assertEquals(ctx, "73[0]->78[1]->661");
        ctx = grpMgr.getContext("736");
        Assert.assertEquals(ctx, "73[0]->78[1]->736");
        ctx = grpMgr.getContext("467");
        Assert.assertEquals(ctx, "73[0]->78[1]->467");
        ctx = grpMgr.getContext("539");
        Assert.assertEquals(ctx, "73[0]->78[1]->539");
        ctx = grpMgr.getContext("524");
        Assert.assertEquals(ctx, "73[0]->78[1]->539[0]->524");
        ctx = grpMgr.getContext("538");
        Assert.assertEquals(ctx, "73[0]->78[1]->539[0]->538");
        ctx = grpMgr.getContext("11");
        Assert.assertEquals(ctx, "73[1]->11");
        ctx = grpMgr.getContext("67");
        Assert.assertEquals(ctx, "73[1]->67");
        ctx = grpMgr.getContext("78");
        Assert.assertEquals(ctx, "73[1]->78");
        ctx = grpMgr.getContext("79");
        Assert.assertEquals(ctx, "73[1]->78[0]->79");
        ctx = grpMgr.getContext("661");
        Assert.assertEquals(ctx, "73[1]->78[0]->661");
        ctx = grpMgr.getContext("736");
        Assert.assertEquals(ctx, "73[1]->78[0]->736");
        ctx = grpMgr.getContext("467");
        Assert.assertEquals(ctx, "73[1]->78[0]->467");
        ctx = grpMgr.getContext("539");
        Assert.assertEquals(ctx, "73[1]->78[0]->539");
        ctx = grpMgr.getContext("524");
        Assert.assertEquals(ctx, "73[1]->78[0]->539[0]->524");
        ctx = grpMgr.getContext("538");
        Assert.assertEquals(ctx, "73[1]->78[0]->539[0]->538");
        ctx = grpMgr.getContext("524");
        Assert.assertEquals(ctx, "73[1]->78[0]->539[1]->524");
        ctx = grpMgr.getContext("525");
        Assert.assertEquals(ctx, "73[1]->78[0]->539[1]->525");
        ctx = grpMgr.getContext("79");
        Assert.assertEquals(ctx, "73[1]->78[1]->79");
        ctx = grpMgr.getContext("661");
        Assert.assertEquals(ctx, "73[1]->78[1]->661");
        ctx = grpMgr.getContext("736");
        Assert.assertEquals(ctx, "73[1]->78[1]->736");
        ctx = grpMgr.getContext("467");
        Assert.assertEquals(ctx, "73[1]->78[1]->467");
        ctx = grpMgr.getContext("539");
        Assert.assertEquals(ctx, "73[1]->78[1]->539");
        ctx = grpMgr.getContext("524");
        Assert.assertEquals(ctx, "73[1]->78[1]->539[0]->524");
        ctx = grpMgr.getContext("538");
        Assert.assertEquals(ctx, "73[1]->78[1]->539[0]->538");
        ctx = grpMgr.getContext("524");
        Assert.assertEquals(ctx, "73[1]->78[1]->539[1]->524");
        ctx = grpMgr.getContext("525");
        Assert.assertEquals(ctx, "73[1]->78[1]->539[1]->525");
        System.out.println(ctx);
        ctx = grpMgr.getContext("10");
        Assert.assertEquals(ctx, "10");
        System.out.println(ctx);
    }

    // + "382=1" + '\u0001' + "375=1" + '\u0001'
    // + "437=444" + '\u0001' + "438=20121105-23:24:42" + '\u0001' + "655=2" +
    // '\u0001' + "555=2"
    // + '\u0001' + "600=FOO" + '\u0001' + "601=2 " + '\u0001' + "539=1" +
    // '\u0001' + "524=STR"
    // + '\u0001' + "538=-33" + '\u0001' + "600=FOO1" + '\u0001' + "601=3" +
    // '\u0001' + "602=4"
    // + "10=085" + '\u0001';
    @Test
    public void testGetContext4() {
        FixGroupMgr grpMgr = new FIX50_8_56_GroupMgr();
        String ctx = grpMgr.getContext("382");
        Assert.assertEquals(ctx, "382");
        ctx = grpMgr.getContext("375");
        Assert.assertEquals(ctx, "382[0]->375");
        ctx = grpMgr.getContext("437");
        Assert.assertEquals(ctx, "382[0]->437");
        ctx = grpMgr.getContext("438");
        Assert.assertEquals(ctx, "382[0]->438");
        ctx = grpMgr.getContext("655");
        Assert.assertEquals(ctx, "382[0]->655");
        ctx = grpMgr.getContext("555");
        Assert.assertEquals(ctx, "555");
        ctx = grpMgr.getContext("600");
        Assert.assertEquals(ctx, "555[0]->600");
    }
}
