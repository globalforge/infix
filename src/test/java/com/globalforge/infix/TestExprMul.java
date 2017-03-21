package com.globalforge.infix;

import org.junit.Assert;
import org.junit.Test;
import com.globalforge.infix.api.InfixActions;
import com.google.common.collect.ListMultimap;

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
public class TestExprMul {
    static StaticTestingUtils msgStore = null;
    InfixActions rules = null;
    String sampleRule = null;
    String result = null;
    ListMultimap<Integer, String> resultStore = null;

    @Test
    public void testm1() {
        try {
            sampleRule = "&45=&45*1";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprMul.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(45).get(0), "0");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testm1_cast() {
        try {
            sampleRule = "&45 = (int) (3.143 * 6 * &44)";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprMul.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(45).get(0), "59");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testm2() {
        try {
            sampleRule = "&382=&9*1.5";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprMul.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(382).get(0), "1500.0");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testm3() {
        try {
            sampleRule = "&375=1*2*3*4";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprMul.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(375).get(2), "24");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testm4() {
        try {
            sampleRule = "&382[0]->&375=&382*&382[0]->&375*&9*&382[1]->&375*2"; // 2
                                                                                // *
                                                                                // 1.5
                                                                                // *
                                                                                // 10
                                                                                // *
            // 3 * 2
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprMul.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(375).get(0), "18000.0");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testm5() {
        try {
            sampleRule = "&382[1]->&375=&382*&382[0]->&375*&9*&382[1]->&375*2"; // 2
                                                                                // *
                                                                                // 1.5
                                                                                // *
                                                                                // 10
                                                                                // *
            // 3 * 2
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprMul.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(375).get(1), "18000.0");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    static final String sampleMessage1 = "8=FIX.4.4" + '\u0001' + "9=1000" + '\u0001' + "35=8"
        + '\u0001' + "43=-1" + '\u0001' + "-43=-1" + '\u0001' + "-44=1" + '\u0001' + "44=3.142"
        + '\u0001' + "60=20130412-19:30:00.686" + '\u0001' + "75=20130412" + '\u0001' + "45=0"
        + '\u0001' + "382=2" + '\u0001' + "375=1.5" + '\u0001' + "337=eb8cd" + '\u0001' + "375=3"
        + '\u0001' + "337=8dhosb" + '\u0001' + "10=004";

    @Test
    public void testm6() {
        try {
            sampleRule = "&382[1]->&375=&382*&375[5]*&9*&382[1]->&375*2"; // 2 *
                                                                          // 1.5
                                                                          // *
                                                                          // 10
                                                                          // *
            // 3 * 2
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprMul.sampleMessage1);
            Assert.fail();
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testm7() {
        try {
            sampleRule = "&382=&9*&382"; // 10 * 2
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprMul.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(382).get(0), "2000");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testm8() {
        try {
            sampleRule = "&382=&9*&382[0]->&375"; // 10 * 1.5
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprMul.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(382).get(0), "1500.0");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testm9() {
        try {
            sampleRule = "&382=&382[1]->&375*&382[0]->&375"; // 3 * 1.5
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprMul.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(382).get(0), "4.5");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    // /////////
    @Test
    public void testm10() {
        try {
            sampleRule = "&382=&382 * &43";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprMul.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("-2", resultStore.get(382).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testm11() {
        try {
            sampleRule = "&382=&382 * &-44";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprMul.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("2", resultStore.get(382).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testm12() {
        try {
            sampleRule = "&382=&43 * &-43";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprMul.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("1", resultStore.get(382).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testm13() {
        try {
            sampleRule = "&-15=&43 * &-44";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprMul.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("-1", resultStore.get(-15).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testm14() {
        try {
            sampleRule = "&382[1]->&375=&43 * &-44";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprMul.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("-1", resultStore.get(375).get(1));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
