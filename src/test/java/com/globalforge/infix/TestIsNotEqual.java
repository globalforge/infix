package com.globalforge.infix;

import org.junit.Assert;
import org.junit.Test;
import com.globalforge.infix.api.InfixActions;
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
public class TestIsNotEqual {
    static final String sampleMessage1 = "8=FIX.4.4" + '\u0001' + "9=1000" + '\u0001' + "35=8"
        + '\u0001' + "43=-1" + '\u0001' + "-43=-1.25" + '\u0001' + "-44=1" + '\u0001' + "44=3.142"
        + '\u0001' + "60=20130412-19:30:00.686" + '\u0001' + "75=20130412" + '\u0001' + "45=0"
        + '\u0001' + "47=0" + '\u0001' + "48=1.5" + '\u0001' + "49=8dhosb" + '\u0001' + "382=2"
        + '\u0001' + "375=1.5" + '\u0001' + "655=eb8cd" + '\u0001' + "375=3" + '\u0001'
        + "655=8dhosb" + '\u0001' + "10=004";
    InfixActions rules = null;
    String sampleRule = null;
    String result = null;
    ListMultimap<Integer, String> resultStore = null;

    @Test
    public void testIsNotEqual1() {
        try {
            sampleRule = "&45 != 1 ? &45=1";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("1", resultStore.get(45).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testIsNotEqual2() {
        try {
            sampleRule = "&45 != 1 ? &45 = 2";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("2", resultStore.get(45).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testIsNotEqualElse() {
        try {
            sampleRule = "&45!=1 ? &45=2 : &45=3";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("2", resultStore.get(45).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testIsNotEqualElse2() {
        try {
            sampleRule = "&45!=0 ? &45=3 : &45=2";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("2", resultStore.get(45).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testIsNotEqualElse3() {
        try {
            sampleRule = "&45!=&47 ? &45=\"FOO\" : &45=2";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("2", resultStore.get(45).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testIsNotEqualElse4() {
        try {
            sampleRule = "&45!=&382 ? &45=\"FOO\" : &45=\"BAR\"";
            System.out.println(sampleRule);
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("FOO", resultStore.get(45).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testIsNotEqualElse5() {
        try {
            sampleRule = "&49!=&382[1]->&655 ? &45=\"FOO\" : &45=\"BAR\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("BAR", resultStore.get(45).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testIsNotEqualElse6() {
        try {
            sampleRule = "&49!=&382[1]->&655 ? &655=\"FOO\" : &45=\"BAR\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("BAR", resultStore.get(45).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testIsNotEqualElse7() {
        try {
            sampleRule = "&49!=&382[0]->&655 ? &655=\"FOO\" : &382[0]->&655=\"BAR\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("FOO", resultStore.get(655).get(2));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testIsNotEqualElse8() {
        try {
            sampleRule = "&49!=\"8dhosb\" ? &382[0]->&655=\"FOO\" : &382[0]->&655=\"BAR\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("BAR", resultStore.get(655).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    /* FAIL with parser error but no way to test */
    @Test
    public void testIsNotEqualElse10() {
        try {
            sampleRule = "&49 != ? &382[0]->&655=\"FOO\" : &382[0]->&655=\"BAR\"";
            System.out.println(sampleRule);
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            // Assert.fail();
            // resultStore = StaticTestingUtils.parseMessage(result);
            // Assert.assertEquals("FOO", resultStore.get(655).get(0));
        } catch (Exception e) {
        }
    }

    @Test
    public void testIsNotEqualElse11() {
        try {
            sampleRule = "&49!=\"\" ? &382[0]->&655=\"FOO\" : &382[0]->&655=\"BAR\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("FOO", resultStore.get(655).get(0));
        } catch (Exception e) {
        }
    }

    /* FAIL with parser error but no way to test */
    @Test
    public void testIsNotEqualElse12() {
        try {
            sampleRule = "&49!=\"8dhosb\" ? &382[0]->&655= : &382[0]->&655=\"BAR\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            // Assert.fail();
        } catch (Exception e) {
        }
    }

    /* FAIL with parser error but no way to test */
    @Test
    public void testIsNotEqualElse13() {
        try {
            sampleRule = "&49!=\"8dhosc\" ? &382[0]->&655=111 : &382[0]->&655=";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            // Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testIsNotEqual14() {
        try {
            sampleRule = "&45!=0 ? &45=1.005";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("0", resultStore.get(45).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testIsNotEqual3() {
        try {
            sampleRule = "&45!=1?&45=2.0";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("2.0", resultStore.get(45).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testIsNotEqualElseF1() {
        try {
            sampleRule = "&45!=1 ? &45=2 : &45=3.13";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("2", resultStore.get(45).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testIsNotEqualElseF2() {
        try {
            sampleRule = "&49!=&382[1]->&655 ? &382[1]->&655=4.3 : &45=\"BAR\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("BAR", resultStore.get(45).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testIsNotEqualElseF3() {
        try {
            sampleRule = "&49!=&382[0]->&655 ? &382[1]->&655=4.3 : &382[0]->&655=6.6";
            rules = new InfixActions(sampleRule);
            long start = System.currentTimeMillis();
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            long end = System.currentTimeMillis();
            long delay = end - start;
            System.out.println("transform time: " + delay);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("4.3", resultStore.get(655).get(1));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testIsNotEqualFloat1() {
        try {
            sampleRule = "&45!=0.0 ? &45=1";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("1", resultStore.get(45).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testIsNotEqualFloat2() {
        try {
            sampleRule = "&44!=3.142 ? &45=1";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("0", resultStore.get(45).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testIsNotEqualFloat3() {
        try {
            sampleRule = "&47!=0.0 ? &45=1";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("1", resultStore.get(45).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    // /////////
    @Test
    public void testd10() {
        try {
            sampleRule = "&43 != -1 ? &-43=1"; // 43=-1, -43=-1.25
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("-1.25", resultStore.get(-43).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testd11() {
        try {
            sampleRule = "&-43!=-1.25 ? &43=-2.3"; // 43=-1, -43=-1.25
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("-1", resultStore.get(43).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testd12() {
        try {
            sampleRule = "&-43 != -1.26 ? &43=-2.3 : &43=2"; // 43=-1, -43=-1.25
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("-2.3", resultStore.get(43).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testd13() {
        try {
            sampleRule = "&-43 != -1.25 ? &43=-2.3 : &43=2.56"; // 43=-1,
                                                                // -43=-1.25
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("2.56", resultStore.get(43).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testd14() {
        try {
            sampleRule = "&-43!=-1.26 ? &43=-2.3 : &43=-2"; // 43=-1, -43=-1.25
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("-2.3", resultStore.get(43).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testd15() {
        try {
            sampleRule = "&-43!=-1.26 ? &43=-2.3 : &43=-2.56"; // 43=-1,
                                                               // -43=-1.25
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("-2.3", resultStore.get(43).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testd16() {
        try {
            sampleRule = "&43!=-1.26 ? &43=-2.3 : &43=2.56"; // 43=-1, -43=-1.25
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("-2.3", resultStore.get(43).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testd17() {
        try {
            sampleRule = "&43!=-1 ? &43=-2.3 : &-43=-2.56"; // 43=-1, -43=-1.25
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIsNotEqual.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("-2.56", resultStore.get(-43).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
