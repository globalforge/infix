package com.globalforge.infix;

import org.junit.Assert;
import org.junit.BeforeClass;
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
public class TestAndOrSimple {
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    private ListMultimap<String, String> getResults(String sampleRule) throws Exception {
        InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
        String result = rules.transformFIXMsg(TestAndOrSimple.sampleMessage1);
        return StaticTestingUtils.parseMessage(result);
    }

    @Test
    public void t1() {
        try {
            String sampleRule = "&45==0 && &47==0 ? &50=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("1", resultStore.get("50").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t2() {
        try {
            String sampleRule = "&45==1 && &47==0 ? &50=1 : &50=2";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("2", resultStore.get("50").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t3() {
        try {
            String sampleRule = "&45!=1 && &47==0 ? &50=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("1", resultStore.get("50").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t4() {
        try {
            String sampleRule = "&45==0 && &47 != 1 ? &50=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("1", resultStore.get("50").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t9() {
        try {
            String sampleRule = "&45==0 && &47==0 && &48==1.5 ? &45=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("1", resultStore.get("45").get(0));
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
            String sampleRule = "&45==1 && &47==0 && &48==1.5 ? &45=1 : &47=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("0", resultStore.get("45").get(0));
            Assert.assertEquals("1", resultStore.get("47").get(0));
            Assert.assertEquals("1.5", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t11() {
        try {
            String sampleRule = "&45==0 && &47==1 && &48==1.5 ? &45=1 : &47=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("0", resultStore.get("45").get(0));
            Assert.assertEquals("1", resultStore.get("47").get(0));
            Assert.assertEquals("1.5", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t12() {
        try {
            String sampleRule = "&45==0 && &47==0 && &48==1.6 ? &45=1 : &48=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("0", resultStore.get("45").get(0));
            Assert.assertEquals("0", resultStore.get("47").get(0));
            Assert.assertEquals("1", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t13() {
        try {
            String sampleRule = "&45==0 || &47==0 && &48==1.6 ? &45=1 : &48=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("0", resultStore.get("45").get(0));
            Assert.assertEquals("0", resultStore.get("47").get(0));
            Assert.assertEquals("1", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t14() {
        try {
            String sampleRule = "&45==0 && &47==0 || &48==1.6 ? &45=1 : &48=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("1", resultStore.get("45").get(0));
            Assert.assertEquals("0", resultStore.get("47").get(0));
            Assert.assertEquals("1.5", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t15() {
        try {
            String sampleRule = "&45==0 || &47==0 && &48==1.6 ? &45=1 : &48=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("0", resultStore.get("45").get(0));
            Assert.assertEquals("0", resultStore.get("47").get(0));
            Assert.assertEquals("1", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t16() {
        try {
            String sampleRule = "(&45==0 || &47==0) && (&48==1.6) ? &45=1 : &48=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("0", resultStore.get("45").get(0));
            Assert.assertEquals("0", resultStore.get("47").get(0));
            Assert.assertEquals("1", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t17() {
        try {
            String sampleRule = "&45==0 || (&47==0 && &48==1.6) ? &45=1 : &48=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("1", resultStore.get("45").get(0));
            Assert.assertEquals("0", resultStore.get("47").get(0));
            Assert.assertEquals("1.5", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t18() {
        try {
            String sampleRule = "^&45 && ^&47 && ^&48 ? &45=1 : &48=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("1", resultStore.get("45").get(0));
            Assert.assertEquals("1.5", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t19() {
        try {
            String sampleRule = "^&45 && ^&47 && ^&50 ? &45=1 : &48=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("0", resultStore.get("45").get(0));
            Assert.assertEquals("1", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t20() {
        try {
            String sampleRule = "^&45 || ^&47 || ^&50 ? &45=1 : &48=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("1", resultStore.get("45").get(0));
            Assert.assertEquals("1.5", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t21() {
        try {
            String sampleRule = "!&50 && !&51 && !&52 ? &45=1 : &48=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("1", resultStore.get("45").get(0));
            Assert.assertEquals("1.5", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t22() {
        try {
            String sampleRule = "^&45 || !&51 && !&52 ? &45=1 : &48=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("1", resultStore.get("45").get(0));
            Assert.assertEquals("1.5", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t23() {
        try {
            String sampleRule = "(^&45 || !&51) && !&52 ? &45=1 : &48=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("1", resultStore.get("45").get(0));
            Assert.assertEquals("1.5", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t24() {
        try {
            String sampleRule = "^&45 || (!&51 && !&52) ? &45=1 : &48=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("1", resultStore.get("45").get(0));
            Assert.assertEquals("1.5", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t25() {
        try {
            String sampleRule = "!&50 || !&45 && !&52 ? &45=1 : &48=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("1", resultStore.get("45").get(0));
            Assert.assertEquals("1.5", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t26() {
        try {
            String sampleRule = "(!&50 || !&45) && !&52 ? &45=1 : &48=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("1", resultStore.get("45").get(0));
            Assert.assertEquals("1.5", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t27() {
        try {
            String sampleRule = "!&50 || (!&45 && !&52) ? &45=1 : &48=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("1", resultStore.get("45").get(0));
            Assert.assertEquals("1.5", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t28() {
        try {
            String sampleRule = "!&55 && (!&54 && (!&53 && (!&47 && !&52))) ? &45=1 : &48=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("0", resultStore.get("45").get(0));
            Assert.assertEquals("1", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t29() {
        try {
            String sampleRule = "!&55 && (!&54 && (!&53 && (!&56 && !&52))) ? &45=1 : &48=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("1", resultStore.get("45").get(0));
            Assert.assertEquals("1.5", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t30() {
        try {
            String sampleRule = "(!&55 || (!&54 || (!&53 || (!&52 && !&47)))) ? &45=1 : &48=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("1", resultStore.get("45").get(0));
            Assert.assertEquals("1.5", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t31() {
        try {
            String sampleRule = "((((!&55 || !&54) || !&53) || !&52) && !&47) ? &45=1 : &48=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("0", resultStore.get("45").get(0));
            Assert.assertEquals("1", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t32() {
        try {
            String sampleRule = "(&382[1]->&655!=\"tarz\" || (&382[0]->&655==\"fubi\" "
                + "|| (&382[1]->&375==3 || (&382 >= 2 || (&45 > -1 || (&48 <=1.5 && &47 < 0.0001)))))) ? &45=1 : &48=1";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("1", resultStore.get("45").get(0));
            Assert.assertEquals("1.5", resultStore.get("48").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t34() {
        try {
            // left to right
            String sampleRule = "&45 == 0 || &43 == -100 && &207 == \"USA\" ? &43=1 : &43=2";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("2", resultStore.get("43").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t35() {
        try {
            String sampleRule = "&45 == 0 || (&43 == -100 && &207 == \"USA\") ? &43=1 : &43=2";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("1", resultStore.get("43").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    static final String sampleMessage1 = "8=FIX.4.4" + '\u0001' + "9=1000" + '\u0001' + "35=8"
        + '\u0001' + "44=3.142" + '\u0001' + "60=20130412-19:30:00.686" + '\u0001' + "75=20130412"
        + '\u0001' + "45=0" + '\u0001' + "47=0" + '\u0001' + "48=1.5" + '\u0001' + "49=8dhosb"
        + '\u0001' + "382=2" + '\u0001' + "375=1.5" + '\u0001' + "655=fubi" + '\u0001' + "375=3"
        + '\u0001' + "655=yubl" + '\u0001' + "10=004";

    @Test
    public void t36() {
        try {
            // 45=0,
            String sampleRule = "(&45 == 0 || &43 == -100) && &207 == \"USA\" ? &43=1 : &43=2";
            ListMultimap<String, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("2", resultStore.get("43").get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
