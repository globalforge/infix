package com.globalforge.infix;

import org.junit.Assert;
import org.junit.Test;
import com.globalforge.infix.api.InfixSimpleActions;
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
public class TestExprCatSimple {
    static final String sampleMessage1 = "8=FIX.4.4" + '\u0001' + "9=1000" + '\u0001' + "35=8"
        + '\u0001' + "43=-1" + '\u0001' + "-43=-1" + '\u0001' + "-44=1" + '\u0001' + "44=3.142"
        + '\u0001' + "60=20130412-19:30:00.686" + '\u0001' + "75=20130412" + '\u0001' + "45=0"
        + '\u0001' + "382=2" + '\u0001' + "375=1.5" + '\u0001' + "337=eb8cd" + '\u0001' + "375=3"
        + '\u0001' + "337=8dhosb" + '\u0001' + "55=BRK" + '\u0001' + "65=B" + '\u0001' + "10=004";
    static StaticTestingUtils msgStore = null;
    InfixSimpleActions rules = null;
    String sampleRule = null;
    String result = null;
    ListMultimap<String, String> resultStore = null;

    @Test
    public void testc1() {
        try {
            sampleRule = "&45=&45|1";
            rules = new InfixSimpleActions(sampleRule);
            result = rules.transformFIXMsg(TestExprCatSimple.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get("45").get(0), "01");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testc2() {
        try {
            sampleRule = "&45=&45|0";
            rules = new InfixSimpleActions(sampleRule);
            result = rules.transformFIXMsg(TestExprCatSimple.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get("45").get(0), "00");
        } catch (Exception e) {
        }
    }

    @Test
    public void testc3() {
        try {
            sampleRule = "&382=&9|\"FOO\"";
            rules = new InfixSimpleActions(sampleRule);
            result = rules.transformFIXMsg(TestExprCatSimple.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get("382").get(0), "1000FOO");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    // /////////
    @Test
    public void testd10() {
        try {
            sampleRule = "&382=&382 | &43"; // 2 | -1
            rules = new InfixSimpleActions(sampleRule);
            result = rules.transformFIXMsg(TestExprCatSimple.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("2-1", resultStore.get("382").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testd11() {
        try {
            sampleRule = "&382=&382 | &-44"; // 2 | 1
            rules = new InfixSimpleActions(sampleRule);
            result = rules.transformFIXMsg(TestExprCatSimple.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("21", resultStore.get("382").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testd12() {
        try {
            sampleRule = "&382=&43 | &-43"; // -1 | -1
            rules = new InfixSimpleActions(sampleRule);
            result = rules.transformFIXMsg(TestExprCatSimple.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("-1-1", resultStore.get("382").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testd13() {
        try {
            sampleRule = "&-15=&43 | &-44"; // -1 | 1
            rules = new InfixSimpleActions(sampleRule);
            result = rules.transformFIXMsg(TestExprCatSimple.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("-11", resultStore.get("-15").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void test14() {
        try {
            sampleRule = "&55=&55|\"/\"|&65";
            rules = new InfixSimpleActions(sampleRule);
            result = rules.transformFIXMsg(TestExprCatSimple.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("BRK/B", resultStore.get("55").get(0));
            Assert.assertEquals("B", resultStore.get("65").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void test15() {
        try {
            sampleRule = "&55=&55|\"/\"|&65;~&65";
            rules = new InfixSimpleActions(sampleRule);
            result = rules.transformFIXMsg(TestExprCatSimple.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("BRK/B", resultStore.get("55").get(0));
            Assert.assertTrue(resultStore.get("65").isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
