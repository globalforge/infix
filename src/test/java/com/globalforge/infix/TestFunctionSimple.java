package com.globalforge.infix;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import com.globalforge.infix.api.InfixSimpleActions;
import com.google.common.collect.ListMultimap;

/*-
The MIT License (MIT)

Copyright (c) 2019-2022 Global Forge LLC

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
public class TestFunctionSimple {
    static final String sampleMessage =
        "8=FIX.4.4" + '\u0001' + "9=1000" + '\u0001' + "35=D" + '\u0001' + "55=BRK/A" + '\u0001'
            + "56=BRK A" + '\u0001' + "57=BRK/A/B" + '\u0001' + "10=004";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Test
    public void testSplit0() {
        try {
            String sampleRule = "split(&55, \"/\", &55, &65)";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage);
            ListMultimap<String, String> resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get("55").get(0), "BRK");
            Assert.assertEquals(resultStore.get("65").get(0), "A");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testSplit1() {
        try {
            String sampleRule = "split(&56, \" \", &55, &65)";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage);
            ListMultimap<String, String> resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get("55").get(0), "BRK");
            Assert.assertEquals(resultStore.get("65").get(0), "A");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testSplit2() {
        try {
            String sampleRule = "split(&57, \"/\", &58, &59, &61)";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage);
            ListMultimap<String, String> resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get("58").get(0), "BRK");
            Assert.assertEquals(resultStore.get("59").get(0), "A");
            Assert.assertEquals(resultStore.get("61").get(0), "B");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testSplit3() {
        try {
            String sampleRule = "split(&57, \"/\", &58, &59)";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage);
            ListMultimap<String, String> resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get("58").get(0), "BRK");
            Assert.assertEquals(resultStore.get("59").get(0), "A");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testSplit4() {
        try {
            String sampleRule = "split(&56, \" \", &55, &65, &85)";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage);
            ListMultimap<String, String> resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get("55").get(0), "BRK");
            Assert.assertEquals(resultStore.get("65").get(0), "A");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testSplit5() {
        try {
            String sampleRule = "split(&56, \" \", &55)";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage);
            ListMultimap<String, String> resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get("55").get(0), "BRK");
            Assert.assertTrue(resultStore.get("65").isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testSplit6() {
        try {
            String sampleRule = "&42=split(&56, \" \", &55)";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage);
            ListMultimap<String, String> resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get("55").get(0), "BRK");
            Assert.assertTrue(resultStore.get("65").isEmpty());
            // split() doesn't return anything
            Assert.assertTrue(resultStore.get("42").isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
