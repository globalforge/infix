package com.globalforge.infix;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
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
public class TestAssignTerminalsSimple {
   static StaticTestingUtils msgStore = null;
   InfixSimpleActions rules = null;
   String sampleRule = null;
   String result = null;
   ListMultimap<String, String> resultStore = null;
   static final String sampleMessage1 = "8=FIX.4.4" + '\u0001' + "9=76" + '\u0001' + "35=D"
      + '\u0001' + "44=3.142" + '\u0001' + "43=-1" + '\u0001' + "-43=-1" + '\u0001' + "-44=1"
      + '\u0001' + "45=0" + '\u0001' + "78=2" + '\u0001' + "79=FOO" + '\u0001' + "80=eb8cd"
      + '\u0001' + "79=BAR" + '\u0001' + "80=8dhosb" + '\u0001' + "10=004";
   static final String sampleMessageNonNumeric = "8=FIX.4.4" + '\u0001' + "9=1042" + '\u0001'
      + "35=D" + '\u0001' + "44=3.142" + '\u0001' + "43=-1" + '\u0001' + "-43=-1" + '\u0001'
      + "-44=1" + '\u0001' + "45=0" + '\u0001' + "FOO=0" + '\u0001' + "BAR=NONSENSE" + '\u0001'
      + "&=Ridiculous" + '\u0001' + "78=2" + '\u0001' + "79=FOO" + '\u0001' + "80=eb8cd" + '\u0001'
      + "79=BAR" + '\u0001' + "80=8dhosb" + '\u0001' + "10=004";

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
   }

   @Before
   public void setUp() throws Exception {
   }

   @After
   public void tearDown() throws Exception {
   }

   @Test
   public void testTagVAL() {
      try {
         sampleRule = "&35=\"8\"";
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessage1);
         System.out.println(StaticTestingUtils.rs(result));
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals(resultStore.get("35").get(0), "8");
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   @Test
   public void testTagNonNumeric2() {
      try {
         sampleRule = "&43=\"NNT2\"";
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessageNonNumeric);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals(resultStore.get("43").get(0), "NNT2");
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testTagNonNumeric3() {
      try {
         sampleRule = "&-43=\"NNT2\"";
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessageNonNumeric);
         System.out.println(StaticTestingUtils.rs(result));
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals(resultStore.get("-43").get(0), "NNT2");
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testQuotes() {
      try {
         sampleRule = "&43=\"\\\"NNT2\\\"\""; // &43="NNT2"
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessageNonNumeric);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("\"NNT2\"", resultStore.get("43").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   /**
    * Creates a rule that assigns a tag (43) a quoted string. <br>
    * Specified like this using infix: &43="\"NNT2\"" <br>
    * Resulting in a FIX field that equals 43="NNT2"
    */
   @Test
   public void testEscapedQuotes() {
      try {
         sampleRule = "&43=" + "\"" + "\\\"" + "NNT2" + "\\\"" + "\""; // &43="\"NNT2\""
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessageNonNumeric);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("\"NNT2\"", resultStore.get("43").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testTagINT() {
      try {
         sampleRule = "&44=42";
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals(resultStore.get("44").get(0), "42");
      } catch (Exception e) {
         Assert.fail();
      }
   }

   @Test
   public void testTagFLOAT1() {
      try {
         sampleRule = "&44=42.01";
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals(resultStore.get("44").get(0), "42.01");
      } catch (Exception e) {
         Assert.fail();
      }
   }

   @Test
   public void testTagFLOAT_Truncates() {
      try {
         sampleRule = "&44 = (int) 42.01";
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals(resultStore.get("44").get(0), "42");
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testTagFLOAT2() {
      try {
         sampleRule = "&44=0.01";
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals(resultStore.get("44").get(0), "0.01");
      } catch (Exception e) {
         Assert.fail();
      }
   }

   @Test
   public void testTagFLOAT3() {
      try {
         sampleRule = "&44=.01";
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals(resultStore.get("44").get(0), "0.01");
      } catch (Exception e) {
         Assert.fail();
      }
   }

   @Test
   public void testTagFLOAT4() {
      try {
         sampleRule = "&44=.48797209745700345";
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         String f = resultStore.get("44").get(0);
         Assert.assertEquals(f, "0.4879721");
      } catch (Exception e) {
         Assert.fail();
      }
   }

   @Test
   public void testTagTAG1() {
      try {
         sampleRule = "&45=&35";
         rules = new InfixSimpleActions(sampleRule);
         System.out
            .println("before: " + StaticTestingUtils.rs(TestAssignTerminalsSimple.sampleMessage1));
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessage1);
         System.out.println("after : " + StaticTestingUtils.rs(result));
         resultStore = StaticTestingUtils.parseMessage(result);
         String f = resultStore.get("45").get(0);
         Assert.assertEquals(f, "D");
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testTagTAG2() {
      try {
         sampleRule = "&45=&44";
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         String f = resultStore.get("45").get(0);
         Assert.assertEquals(f, "3.142");
      } catch (Exception e) {
         Assert.fail();
      }
   }

   @Test
   public void testTagTAG4() {
      try {
         sampleRule = "&45=&45";
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         String f = resultStore.get("45").get(0);
         Assert.assertEquals(f, "0");
      } catch (Exception e) {
         Assert.fail();
      }
   }

   @Test
   public void testd10() {
      try {
         sampleRule = "&215=&43"; // -1
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("-1", resultStore.get("215").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testd11() {
      try {
         sampleRule = "&215=&-44"; // 1
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("1", resultStore.get("215").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testd12() {
      try {
         sampleRule = "&215=&-43"; // -1
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("-1", resultStore.get("215").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testd13() {
      try {
         sampleRule = "&-15=&-44"; // 1
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("1", resultStore.get("-15").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testd15() {
      try {
         sampleRule = "&43=-2"; // 1
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("-2", resultStore.get("43").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testd16() {
      try {
         sampleRule = "&43=-2.3"; // 1
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("-2.3", resultStore.get("43").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void t24() {
      try {
         sampleRule = "&1058=2;&1059=1;&1060=2;&1059=3;&1060=4"; // 1
         rules = new InfixSimpleActions(sampleRule);
         // rules.primeEngine("FIX.4.4");
         // rules.primeEngine("FIX.5.0SP2");
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessage1);
         // System.out.println(StaticTestingUtils.rs(result));
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("3", resultStore.get("1059").get(0));
         Assert.assertEquals("4", resultStore.get("1060").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void t25() {
      try {
         sampleRule = "&1058=2;&1059=1;&1060=2"; // 1
         rules = new InfixSimpleActions(sampleRule);
         // rules.primeEngine("FIX.4.4");
         // rules.primeEngine("FIX.5.0SP2");
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessage1);
         // System.out.println(StaticTestingUtils.rs(result));
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("1", resultStore.get("1059").get(0));
         Assert.assertEquals("2", resultStore.get("1060").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   static final String sampleMessageAI = "8=FIX.4.4" + '\u0001' + "9=1042" + '\u0001' + "35=AI"
      + '\u0001' + "44=3.142" + '\u0001' + "43=-1" + '\u0001' + "-43=-1" + '\u0001' + "-44=1"
      + '\u0001' + "45=0" + '\u0001' + "78=2" + '\u0001' + "79=FOO" + '\u0001' + "80=eb8cd"
      + '\u0001' + "79=BAR" + '\u0001' + "80=8dhosb" + '\u0001' + "10=233";

   /* fail so assert no change */
   @Test
   public void testd28() {
      try {
         sampleRule = "&43="; // 1
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("-1", resultStore.get("43").get(0));
         // Assert.fail();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   static final String sampleMessageSymSuffix =
      "8=FIX.4.2" + '\u0001' + "9=1042" + '\u0001' + "35=D" + '\u0001' + "55=ACL" + '\u0001'
         + "65=U" + '\u0001' + "421=US" + '\u0001' + "10=004";

   /*
    * SOH not allowed
    */
   @Test
   public void testd29() {
      try {
         sampleRule = "&55 == " + '\u0001' + "? &55=\"FOO\"";
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessageSymSuffix);
         Assert.fail();
      } catch (Exception e) {
      }
   }

   /**
    * Test apply a suffix. If 65 exists and country code is US then concatenate
    * 55 and 65 into 55/65.
    */
   @Test
   public void testd30() {
      try {
         sampleRule = "^&65 && &421==\"US\" ? &55 = &55|\"\\\"|&65;~&65"; // 1
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessageSymSuffix);
         System.out.println(StaticTestingUtils.rs(result));
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("ACL\\U", resultStore.get("55").get(0));
         List<String> suffix = resultStore.get("65");
         Assert.assertTrue(suffix.isEmpty());
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   /*
    * \n not allowed
    */
   @Test
   public void testd31() {
      try {
         sampleRule = "&55 == " + '\n' + "? &55=\"FOO\"";
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessageSymSuffix);
         Assert.assertEquals(resultStore.get("55").get(0), "ACL"); // rule
         // failure
      } catch (Exception e) {
      }
   }

   /*
    * \r not allowed
    */
   @Test
   public void testd32() {
      try {
         sampleRule = "&55 == " + '\r' + "? &55=\"FOO\"";
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessageSymSuffix);
         Assert.assertEquals(resultStore.get("55").get(0), "ACL"); // rule
         // failure
      } catch (Exception e) {
      }
   }

   /*
    * \r not allowed
    */
   @Test
   public void testd32a() {
      try {
         sampleRule = "&55 == \"\n\" ? &55=\"FOO\" : &55=\"BAR\"";
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessageSymSuffix);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("BAR", resultStore.get("55").get(0));
      } catch (Exception e) {
         Assert.fail();
      }
   }

   /*
    * \r not allowed
    */
   @Test
   public void testd32b() {
      try {
         sampleRule = "&55 == \"\r\" ? &55=\"FOO\" : &55=\"BAR\"";
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessageSymSuffix);
      } catch (Exception e) {
         Assert.fail();
      }
   }

   /*
    * \t ok
    */
   @Test
   public void testd33() {
      try {
         sampleRule = "&55 == \"\t\" ? &55=\"FOO\" : &55=\"BAR\"";
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessageSymSuffix);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("BAR", resultStore.get("55").get(0));
      } catch (Exception e) {
         Assert.fail();
      }
   }

   /*
    * \t ok
    */
   @Test
   public void testd33a() {
      try {
         sampleRule = "&55 != \"\t\" ? &55=\"FOO\" : &55=\"BAR\"";
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessageSymSuffix);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("FOO", resultStore.get("55").get(0));
      } catch (Exception e) {
         Assert.fail();
      }
   }

   /*
    * \t ok
    */
   @Test
   public void testd34() {
      try {
         sampleRule = "&55 != \"FOO\" ? &55=\"\\\"";
         rules = new InfixSimpleActions(sampleRule);
         result = rules.transformFIXMsg(TestAssignTerminalsSimple.sampleMessageSymSuffix);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("\\", resultStore.get("55").get(0));
      } catch (Exception e) {
         Assert.fail();
      }
   }
}
