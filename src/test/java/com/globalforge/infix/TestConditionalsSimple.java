package com.globalforge.infix;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import com.globalforge.infix.api.InfixAPI;
import com.globalforge.infix.api.InfixSimpleActions;
import com.globalforge.infix.api.InfixUserTerminal;
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
public class TestConditionalsSimple {
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    private ListMultimap<String, String> getResults(String sampleRule) throws Exception {
        InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
        String result = rules.transformFIXMsg(TestConditionalsSimple.sampleMessage);
        return StaticTestingUtils.parseMessage(result);
    }

    @Test
    public void t6() {
        try {
            String sampleRule =
                "!&48 ? [&75=222; &627[{com.globalforge.infix.TestConditionals$Assign1}]->&628=\"MIKEY\"]; &47=\"STARK\"";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("1", resultStore.get("627").get(0));
            Assert.assertEquals("COMPID", resultStore.get("628").get(0));
            Assert.assertEquals("20130412", resultStore.get("75").get(0));
            Assert.assertEquals("STARK", resultStore.get("47").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    static final String sampleMessage = "8=FIX.5.0SP2" + '\u0001' + "9=1000" + '\u0001' + "34=8"
        + '\u0001' + "35=8" + '\u0001' + "627=1" + '\u0001' + "628=COMPID" + '\u0001'
        + "629=20130412-19:30:00.686" + '\u0001' + "630=7" + '\u0001' + "44=3.142" + '\u0001'
        + "52=20140617-09:30:00.686" + '\u0001' + "75=20130412" + '\u0001' + "45=0" + '\u0001'
        + "47=0" + '\u0001' + "48=1.5" + '\u0001' + "49=SENDERCOMP" + '\u0001' + "56=TARGETCOMP"
        + '\u0001' + "382=2" + '\u0001' + "375=1.5" + '\u0001' + "655=fubi" + '\u0001' + "375=3"
        + '\u0001' + "655=yubl" + '\u0001' + "10=004";

    @Test
    public void t7() {
        try {
            String sampleRule = "&45==0 ? &47=1200; &48=1400";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("0", resultStore.get("45").get(0));
            Assert.assertEquals("1200", resultStore.get("47").get(0));
            Assert.assertEquals("1400", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t8() {
        try {
            String sampleRule = "&45==1 ? &47=1200; &48=1400";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("0", resultStore.get("45").get(0));
            Assert.assertEquals("0", resultStore.get("47").get(0));
            Assert.assertEquals("1400", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t9() {
        try {
            String sampleRule = "&45==1 ? [&47=1200; &48=1400]";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("0", resultStore.get("45").get(0));
            Assert.assertEquals("0", resultStore.get("47").get(0));
            Assert.assertEquals("1.5", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t10() {
        try {
            String sampleRule = "&45==0 ? [&47=1200; &48=1400]";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("0", resultStore.get("45").get(0));
            Assert.assertEquals("1200", resultStore.get("47").get(0));
            Assert.assertEquals("1400", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t11() {
        try {
            String sampleRule = "&35==\"D\" ? [&47=1200; &48=1400] : [&47=4; &48=\"P\"]";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("0", resultStore.get("45").get(0));
            Assert.assertEquals("4", resultStore.get("47").get(0));
            Assert.assertEquals("P", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t12() {
        try {
            String sampleRule = "&35==\"D\" ? &47=1200 : &47=4";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("4", resultStore.get("47").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    /*-
    static final String sampleMessage = "8=FIX.5.0SP2" + '\u0001' + "9=1000" + '\u0001' + "34=8"
       + '\u0001' + "35=8" + '\u0001' + "627=1" + '\u0001' + "628=COMPID" + '\u0001'
       + "629=20130412-19:30:00.686" + '\u0001' + "630=7" + '\u0001' + "44=3.142" + '\u0001'
       + "52=20140617-09:30:00.686" + '\u0001' + "75=20130412" + '\u0001' + "45=0" + '\u0001'
       + "47=0" + '\u0001' + "48=1.5" + '\u0001' + "49=SENDERCOMP" + '\u0001' + "56=TARGETCOMP"
       + '\u0001' + "382=2" + '\u0001' + "375=1.5" + '\u0001' + "655=fubi" + '\u0001' + "375=3"
       + '\u0001' + "655=yubl" + '\u0001' + "10=004";
       */

    @Test
    public void t13() {
        try {
            String sampleRule = "&35==\"D\" ? &47=1200 : &47=";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void t14() {
        try {
            String sampleRule = "&35==8 ? &47=";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    /*-
    static final String sampleMessage = "8=FIX.5.0SP2" + '\u0001' + "9=1000" + '\u0001' + "34=8"
       + '\u0001' + "35=8" + '\u0001' + "627=1" + '\u0001' + "628=COMPID" + '\u0001'
       + "629=20130412-19:30:00.686" + '\u0001' + "630=7" + '\u0001' + "44=3.142" + '\u0001'
       + "52=20140617-09:30:00.686" + '\u0001' + "75=20130412" + '\u0001' + "45=0" + '\u0001'
       + "47=0" + '\u0001' + "48=1.5" + '\u0001' + "49=SENDERCOMP" + '\u0001' + "56=TARGETCOMP"
       + '\u0001' + "382=2" + '\u0001' + "375=1.5" + '\u0001' + "655=fubi" + '\u0001' + "375=3"
       + '\u0001' + "655=yubl" + '\u0001' + "10=004";
    */
    @Test
    public void t15() {
        try {
            String sampleRule = "&35==8 ? (&47==8 ? &47=4 : &47=6) : &47=2";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("6", resultStore.get("47").get(0));
        } catch (Exception e) {
        }
    }

    @Test
    public void t15b() {
        try {
            String sampleRule = "&35==8 ? (&47==0 ? &47=4 : &47=6) : &47=2";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("4", resultStore.get("47").get(0));
        } catch (Exception e) {
        }
    }

    @Test
    public void t15c() {
        try {
            String sampleRule = "&35==\"D\" ? (&47==0 ? &47=4 : &47=6) : &47=2";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("2", resultStore.get("47").get(0));
        } catch (Exception e) {
        }
    }

    @Test
    public void t16() {
        try {
            String sampleRule = "&35==8 ? &47=4 : &47=6";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("4", resultStore.get("47").get(0));
        } catch (Exception e) {
        }
    }

    @Test
    public void t17() {
        try {
            String sampleRule = "&35==7 ? &47=4 : &47=6";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("6", resultStore.get("47").get(0));
        } catch (Exception e) {
        }
    }
    static final String sm1 = "8=FIX.4.4" + '\u0001' + "9=1042" + '\u0001' + "35=D" + '\u0001'
        + "44=" + '\u0001' + "43=-1" + '\u0001' + "-43=-1" + '\u0001' + "-44=1" + '\u0001' + "45=0"
        + '\u0001' + "78=2" + '\u0001' + "79=FOO" + '\u0001' + "80=eb8cd" + '\u0001' + "79=BAR"
        + '\u0001' + "80=8dhosb" + '\u0001' + "10=004";

    @Test
    public void testTag18() {
        try {
            String sampleRule = "^&44 ? &44=42.00";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(sm1);
            ListMultimap<String, String> resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get("44").get(0), "");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testTag19() {
        try {
            String sampleRule = "!&44 ? &44=42.00";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(sm1);
            ListMultimap<String, String> resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get("44").get(0), "42.00");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testTag20() {
        try {
            String sampleRule = "!&44 ? ~&44";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(sm1);
            ListMultimap<String, String> resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertTrue(resultStore.get("44").isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testTag21() {
        try {
            String sampleRule = "!&102 ? ~&102";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(sm1);
            ListMultimap<String, String> resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertTrue(resultStore.get("102").isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    static class Assign1 implements InfixUserTerminal {
        @Override
        public String visitTerminal(InfixAPI infixApi) {
            return "0";
        }
    }
}
