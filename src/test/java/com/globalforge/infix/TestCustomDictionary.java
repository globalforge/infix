package com.globalforge.infix;

import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;
import com.globalforge.infix.api.InfixActions;
import com.globalforge.infix.qfix.FixGroupMgr;
import com.globalforge.infix.qfix.MessageData;
import com.globalforge.infix.qfix.fix42gc.auto.group.FIX42GC_D_68_GroupMgr;
import com.google.common.collect.ListMultimap;

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
public class TestCustomDictionary {
    static StaticTestingUtils msgStore = null;
    InfixActions rules = null;
    String sampleRule = null;
    String result = null;
    ListMultimap<Integer, String> resultStore = null;

    @Test
    public void t1() throws Exception {
        MessageData msgData = FixContextMgr.getInstance().getMessageData("FIX.4.2GC");
        FixFieldOrderHash orderHash = new FixFieldOrderHash(msgData);
        BigDecimal pos = orderHash.getFieldPosition("D", "382");
        org.junit.Assert.assertEquals("47", pos.toString());
        pos = orderHash.getFieldPosition("D", "382[0]->655");
        org.junit.Assert.assertEquals("47.000000833", pos.toString());
    }

    @Test
    public void t2() {
        FixGroupMgr grpMgr = new FIX42GC_D_68_GroupMgr();
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
    }
    static final String customMsg1 =
        "8=FIX.4.2GC" + '\u0001' + "9=52" + '\u0001' + "35=8" + '\u0001' + "44=3.142" + '\u0001'
            + "45=0" + '\u0001' + "382=2" + '\u0001' + "375=FOO" + '\u0001' + "337=eb8cd" + '\u0001'
            + "375=BAR" + '\u0001' + "337=8dhosb" + '\u0001' + "10=004";

    @Test
    public void t3() {
        try {
            sampleRule = "&382[0]->&375=\"D\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestCustomDictionary.customMsg1, true); // System.out.println(result);
            resultStore = StaticTestingUtils.parseMessage(result);
            String r = resultStore.get(375).get(0);
            Assert.assertEquals(r, "D");
        } catch (Exception e) {
            Assert.fail();
        }
    }
    static final String customMsg2 = "8=FIX.4.2" + '\u0001' + "9=52" + '\u0001' + "35=8" + '\u0001'
        + "44=3.142" + '\u0001' + "45=0" + '\u0001' + "382=2" + '\u0001' + "375=FOO" + '\u0001'
        + "337=eb8cd" + '\u0001' + "375=BAR" + '\u0001' + "337=8dhosb" + '\u0001' + "10=004";

    @Test
    public void t4() {
        try {
            sampleRule = "&382[0]->&375=\"D\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestCustomDictionary.customMsg2, true); // System.out.println(result);
            resultStore = StaticTestingUtils.parseMessage(result);
            String r = resultStore.get(375).get(0);
            Assert.assertEquals(r, "D");
            System.out.println(StaticTestingUtils.rs(result));
        } catch (Exception e) {
            Assert.fail();
        }
    }
    static final String customMsg3 =
        "8=FIX.4.2GC" + '\u0001' + "9=52" + '\u0001' + "35=D" + '\u0001' + "44=3.142" + '\u0001'
            + "45=0" + '\u0001' + "382=2" + '\u0001' + "375=FOO" + '\u0001' + "337=eb8cd" + '\u0001'
            + "375=BAR" + '\u0001' + "337=8dhosb" + '\u0001' + "10=004";

    @Test
    public void t5() {
        try {
            sampleRule = "&382[0]->&375=\"D\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestCustomDictionary.customMsg3, true); // System.out.println(result);
            resultStore = StaticTestingUtils.parseMessage(result);
            String r = resultStore.get(375).get(0);
            Assert.assertEquals(r, "D");
            System.out.println(StaticTestingUtils.rs(result));
        } catch (Exception e) {
            Assert.fail();
        }
    }
    static final String customMsg4 = "8=FIX.4.2" + '\u0001' + "9=52" + '\u0001' + "35=D" + '\u0001'
        + "44=3.142" + '\u0001' + "45=0" + '\u0001' + "382=2" + '\u0001' + "375=FOO" + '\u0001'
        + "337=eb8cd" + '\u0001' + "375=BAR" + '\u0001' + "337=8dhosb" + '\u0001' + "10=004";

    @Test
    public void t6() {
        try {
            sampleRule = "&382[0]->&375=\"D\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestCustomDictionary.customMsg4, true);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void t7() {
        try {
            // sampleRule = ";&382[0]->&375=\"D\";&8=\"FIX.4.2\"";
            sampleRule = "&382[0]->&375=\"D\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestCustomDictionary.customMsg4, "FIX.4.2GC"); // System.out.println(result);
            resultStore = StaticTestingUtils.parseMessage(result);
            String r = resultStore.get(375).get(0);
            Assert.assertEquals(r, "D");
            r = resultStore.get(8).get(0);
            Assert.assertEquals(r, "FIX.4.2GC");
            System.out.println(StaticTestingUtils.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t8() {
        try {
            sampleRule = "&382[0]->&375=\"D\";&8=\"FIX.4.2\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestCustomDictionary.customMsg4, "FIX.4.2GC"); // System.out.println(result);
            resultStore = StaticTestingUtils.parseMessage(result);
            String r = resultStore.get(375).get(0);
            Assert.assertEquals(r, "D");
            r = resultStore.get(8).get(0);
            Assert.assertEquals(r, "FIX.4.2");
            System.out.println(StaticTestingUtils.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
