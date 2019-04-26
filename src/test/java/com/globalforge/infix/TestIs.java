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
public class TestIs {
    static final String sampleMessage1 = "8=FIX.4.4" + '\u0001' + "9=1000" + '\u0001' + "35=8"
        + '\u0001' + "43=-1" + '\u0001' + "-43=-1.25" + '\u0001' + "-44=1" + '\u0001' + "44=3.142"
        + '\u0001' + "60=20130412-19:30:00.686" + '\u0001' + "75=20130412" + '\u0001' + "45=0"
        + '\u0001' + "47=0" + '\u0001' + "48=1.5" + '\u0001' + "49=8dhosb" + '\u0001' + "382=2"
        + '\u0001' + "375=1.5" + '\u0001' + "655=42" + '\u0001' + "375=3" + '\u0001' + "655=42"
        + '\u0001' + "10=004";
    InfixActions rules = null;
    String sampleRule = null;
    String result = null;
    ListMultimap<Integer, String> resultStore = null;

    @Test
    public void i1() {
        try {
            sampleRule = "^&45 ? &50=1";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIs.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("1", resultStore.get(50).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void i2() {
        try {
            sampleRule = "^&45 ? &50=1 : &50=2";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIs.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("1", resultStore.get(50).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void i3() {
        try {
            sampleRule = "^&382 ? &50=1 : &50=2";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIs.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("1", resultStore.get(50).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void i4() {
        try {
            sampleRule = "^&375 ? &50=1 : &50=2";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIs.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("2", resultStore.get(50).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void i5() {
        try {
            sampleRule = "^&382[0]->&375 ? &50=1 : &50=2";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIs.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("1", resultStore.get(50).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void i6() {
        try {
            sampleRule = "^&382[1]->&655 ? &50=1 : &50=2";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIs.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("1", resultStore.get(50).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void i7() {
        try {
            sampleRule = "^&655[2] ? &50=1 : &50=2";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIs.sampleMessage1);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    // /////////
    @Test
    public void testd11() {
        try {
            sampleRule = "^&-43 ? &43=-2.3"; // 43=-1, -43=-1.25
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIs.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("-2.3", resultStore.get(43).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testd12() {
        try {
            sampleRule = "^&-49 ? &-43=-2.3"; // 43=-1, -43=-1.25
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIs.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("-1.25", resultStore.get(-43).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testd13() {
        try {
            sampleRule = "^&-43 ? &43=-2.3 : &-43=\"FOO\""; // 43=-1, -43=-1.25
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIs.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("-2.3", resultStore.get(43).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testd14() {
        try {
            sampleRule = "^&51 ? &43=\"BAR\" : &-43=\"FOO\""; // 43=-1,
                                                              // -43=-1.25
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIs.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("FOO", resultStore.get(-43).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testd15() {
        try {
            sampleRule = "^&51 ? &43=&-43 : &-43=\"FOO\""; // 43=-1, -43=-1.25
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIs.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("FOO", resultStore.get(-43).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testd16() {
        try {
            sampleRule = "^&49 ? &43=&-43 : &-43=&49"; // 43=-1, -43=-1.25
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestIs.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("-1.25", resultStore.get(43).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
