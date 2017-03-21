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
/**
 * Test changing the values between tags.
 * @author Michael
 */
public class TestExchange {
    static final String sampleMessage = "8=FIX.4.4" + '\u0001' + "9=52" + '\u0001' + "35=8"
        + '\u0001' + "44=3.142" + '\u0001' + "45=0" + '\u0001' + "49=DTTX" + '\u0001' + "56=AQUA"
        + '\u0001' + "382=2" + '\u0001' + "375=FOO" + '\u0001' + "337=eb8cd" + '\u0001' + "375=BAR"
        + '\u0001' + "337=8dhosb" + '\u0001' + "10=004";

    @Test
    public void test1() {
        try {
            String action = "&49<->&56";
            InfixActions rules = new InfixActions(action);
            String result = rules.transformFIXMsg(TestExchange.sampleMessage, true); // System.out.println(result);
            ListMultimap<Integer, String> resultStore = StaticTestingUtils.parseMessage(result);
            String r = resultStore.get(56).get(0);
            Assert.assertEquals(r, "DTTX");
            r = resultStore.get(49).get(0);
            Assert.assertEquals(r, "AQUA");
            // System.out.println(sampleMessage);
            // System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void test2() {
        try {
            String action = "&49=&100";
            InfixActions rules = new InfixActions(action);
            String result = rules.transformFIXMsg(TestExchange.sampleMessage, true); // System.out.println(result);
            ListMultimap<Integer, String> resultStore = StaticTestingUtils.parseMessage(result);
            String r = resultStore.get(49).get(0);
            Assert.assertEquals(r, "DTTX");
            r = resultStore.get(56).get(0);
            Assert.assertEquals(r, "AQUA");
            // System.out.println(sampleMessage);
            // System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void test3() {
        try {
            String action = "&49<->&382[0]->&375";
            InfixActions rules = new InfixActions(action);
            String result = rules.transformFIXMsg(TestExchange.sampleMessage, true); // System.out.println(result);
            // System.out.println(sampleMessage);
            // System.out.println(result);
            ListMultimap<Integer, String> resultStore = StaticTestingUtils.parseMessage(result);
            String r = resultStore.get(49).get(0);
            Assert.assertEquals(r, "FOO");
            r = resultStore.get(375).get(0);
            Assert.assertEquals(r, "DTTX");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void test5() {
        try {
            String action = "&382[0]->&375<->&382[1]->&375";
            InfixActions rules = new InfixActions(action);
            String result = rules.transformFIXMsg(TestExchange.sampleMessage, true); // System.out.println(result);
            // System.out.println(sampleMessage);
            // System.out.println(result);
            ListMultimap<Integer, String> resultStore = StaticTestingUtils.parseMessage(result);
            String r = resultStore.get(375).get(1);
            Assert.assertEquals(r, "FOO");
            r = resultStore.get(375).get(0);
            Assert.assertEquals(r, "BAR");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void test6() {
        try {
            String action = "&382[1]->&375<->&382[0]->&375";
            InfixActions rules = new InfixActions(action);
            String result = rules.transformFIXMsg(TestExchange.sampleMessage, true); // System.out.println(result);
            // System.out.println(sampleMessage);
            // System.out.println(result);
            ListMultimap<Integer, String> resultStore = StaticTestingUtils.parseMessage(result);
            String r = resultStore.get(375).get(1);
            Assert.assertEquals(r, "FOO");
            r = resultStore.get(375).get(0);
            Assert.assertEquals(r, "BAR");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
