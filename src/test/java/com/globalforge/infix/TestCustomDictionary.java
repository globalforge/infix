package com.globalforge.infix;

import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;
import com.globalforge.infix.api.InfixActions;
import com.globalforge.infix.qfix.FixGroupMgr;
import com.globalforge.infix.qfix.MessageData;
import com.globalforge.infix.qfix.fix42aqua.auto.group.FIX42Aqua_D_68_GroupMgr;
import com.google.common.collect.ListMultimap;

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
public class TestCustomDictionary {
    static StaticTestingUtils msgStore = null;
    InfixActions rules = null;
    String sampleRule = null;
    String result = null;
    ListMultimap<Integer, String> resultStore = null;

    @Test
    public void t1() throws Exception {
        MessageData msgData = FixContextMgr.getInstance().getMessageData("FIX.4.2Aqua");
        FixFieldOrderHash orderHash = new FixFieldOrderHash(msgData);
        BigDecimal pos = orderHash.getFieldPosition("D", "9000");
        org.junit.Assert.assertEquals("99", pos.toString());
        pos = orderHash.getFieldPosition("D", "9000[0]->9001");
        org.junit.Assert.assertEquals("99.000000125", pos.toString());
    }

    @Test
    public void t2() {
        FixGroupMgr grpMgr = new FIX42Aqua_D_68_GroupMgr();
        String ctx = grpMgr.getContext("9000");
        Assert.assertEquals(ctx, "9000");
        ctx = grpMgr.getContext("9001");
        Assert.assertEquals(ctx, "9000[0]->9001");
        ctx = grpMgr.getContext("9002");
        Assert.assertEquals(ctx, "9000[0]->9002");
        ctx = grpMgr.getContext("9003");
        Assert.assertEquals(ctx, "9000[0]->9003");
        ctx = grpMgr.getContext("9004");
        Assert.assertEquals(ctx, "9000[0]->9004");
        ctx = grpMgr.getContext("9005");
        Assert.assertEquals(ctx, "9000[0]->9005");
        ctx = grpMgr.getContext("9006");
        Assert.assertEquals(ctx, "9000[0]->9006");
    }
    static final String customMsg42Aqua = "8=FIX.4.2Aqua" + '\u0001' + "9=52" + '\u0001' + "35=D"
        + '\u0001' + "44=3.142" + '\u0001' + "45=0" + '\u0001' + "9000=2" + '\u0001'
        + "9001=ClOrdID1" + '\u0001' + "9002=1" + '\u0001' + "9003=10000" + '\u0001' + "9004=-0.04"
        + '\u0001' + "9005=5000" + '\u0001' + "9006=40000" + '\u0001' + "9001=ClOrdID2" + '\u0001'
        + "9002=2" + '\u0001' + "9003=10500" + '\u0001' + "9004==0.03" + '\u0001' + "9005=6000"
        + '\u0001' + "9006=45000" + '\u0001' + "10=004";

    @Test
    public void t3() {
        try {
            sampleRule = "&9000[0]->&9002=\"1\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestCustomDictionary.customMsg42Aqua, true); // System.out.println(result);
            resultStore = StaticTestingUtils.parseMessage(result);
            String r = resultStore.get(9002).get(0);
            Assert.assertEquals(r, "1");
        } catch (Exception e) {
            Assert.fail();
        }
    }
    static final String customMsg42 = "8=FIX.4.2" + '\u0001' + "9=52" + '\u0001' + "35=D" + '\u0001'
        + "44=3.142" + '\u0001' + "45=0" + '\u0001' + "9000=2" + '\u0001' + "9001=ClOrdID1"
        + '\u0001' + "9002=1" + '\u0001' + "9003=10000" + '\u0001' + "9004=-0.04" + '\u0001'
        + "9005=5000" + '\u0001' + "9006=40000" + '\u0001' + "9001=ClOrdID2" + '\u0001' + "9002=2"
        + '\u0001' + "9003=10500" + '\u0001' + "9004==0.03" + '\u0001' + "9005=6000" + '\u0001'
        + "9006=45000" + '\u0001' + "10=004";

    @Test
    public void t5() {
        try {
            sampleRule = "&9000[1]->&9001=\"ClOrdID2\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestCustomDictionary.customMsg42Aqua, true); // System.out.println(result);
            resultStore = StaticTestingUtils.parseMessage(result);
            String r = resultStore.get(9001).get(0);
            Assert.assertEquals(r, "ClOrdID1");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void t6() {
        try {
            sampleRule = "&9000[1]->&9001=\"ClOrdID2\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestCustomDictionary.customMsg42Aqua, true); // System.out.println(result);
            resultStore = StaticTestingUtils.parseMessage(result);
            String r = resultStore.get(9001).get(0);
            Assert.assertEquals(r, "ClOrdID1");
        } catch (Exception e) {
        }
    }

    @Test
    public void t7() {
        try {
            // sampleRule = ";&382[0]->&375=\"D\";&8=\"FIX.4.2\"";
            sampleRule = "&9000[0]->&9001=\"D\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestCustomDictionary.customMsg42Aqua, "FIX.4.2Aqua"); // System.out.println(result);
            resultStore = StaticTestingUtils.parseMessage(result);
            String r = resultStore.get(9001).get(0);
            Assert.assertEquals(r, "D");
            r = resultStore.get(8).get(0);
            Assert.assertEquals(r, "FIX.4.2Aqua");
            System.out.println(StaticTestingUtils.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t8() {
        try {
            sampleRule = "&9000[0]->&9001=\"D\";&8=\"FIX.4.2\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestCustomDictionary.customMsg42Aqua, "FIX.4.2Aqua"); // System.out.println(result);
            resultStore = StaticTestingUtils.parseMessage(result);
            String r = resultStore.get(9001).get(0);
            Assert.assertEquals(r, "D");
            r = resultStore.get(8).get(0);
            Assert.assertEquals(r, "FIX.4.2");
            System.out.println(StaticTestingUtils.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t9() {
        try {
            sampleRule = "&9000[0]->&9001=\"D\";&8=\"FIX.4.2Aqua\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestCustomDictionary.customMsg42Aqua, "FIX.4.2Aqua"); // System.out.println(result);
            resultStore = StaticTestingUtils.parseMessage(result);
            String r = resultStore.get(9001).get(0);
            Assert.assertEquals(r, "D");
            r = resultStore.get(8).get(0);
            Assert.assertEquals(r, "FIX.4.2Aqua");
            System.out.println(StaticTestingUtils.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    /**
     * User can't send 8=FIX.4.2Aqua so set it for him, do the transform on the
     * custom fields, obtain the custom fields, and then set it back to FIX.4.2
     */
    @Test
    public void t10() {
        try {
            sampleRule = "&8=\"FIX.4.2Aqua\";&9000[0]->&9001=\"D\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestCustomDictionary.customMsg42, "FIX.4.2Aqua"); // System.out.println(result);
            resultStore = StaticTestingUtils.parseMessage(result);
            String r = resultStore.get(9001).get(0);
            Assert.assertEquals(r, "D");
            r = resultStore.get(8).get(0);
            Assert.assertEquals(r, "FIX.4.2Aqua");
            System.out.println(StaticTestingUtils.rs(result));
            sampleRule = "&8=\"FIX.4.2\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(result, "FIX.4.2Aqua");
            resultStore = StaticTestingUtils.parseMessage(result);
            r = resultStore.get(8).get(0);
            Assert.assertEquals(r, "FIX.4.2");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
