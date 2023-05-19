package com.globalforge.infix;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.globalforge.infix.api.InfixActions;
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
public class TestUnary {
   static StaticTestingUtils msgStore = null;
   InfixActions rules = null;
   String sampleRule = null;
   String result = null;
   ListMultimap<String, String> resultStore = null;

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
         sampleRule = "~&35";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestUnary.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         List<String> msgType = resultStore.get("35");
         Assert.assertFalse(msgType.isEmpty());
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void t2() {
      try {
         resultStore = StaticTestingUtils.parseMessage(TestUnary.sampleMessage1);
         List<String> val = resultStore.get("655");
         Assert.assertTrue(val.size() == 2);
         sampleRule = "~&382[1]->&655";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestUnary.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         val = resultStore.get("655");
         Assert.assertTrue(val.size() == 1);
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void t3() {
      try {
         resultStore = StaticTestingUtils.parseMessage(TestUnary.sampleMessage1);
         List<String> val = resultStore.get("382");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("655");
         Assert.assertTrue(val.size() == 2);
         val = resultStore.get("375");
         Assert.assertTrue(val.size() == 2);
         sampleRule = "~&382";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestUnary.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         val = resultStore.get("655");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("382");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("375");
         Assert.assertTrue(val.size() == 0);
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void t4() {
      try {
         resultStore = StaticTestingUtils.parseMessage(TestUnary.sampleMessage1);
         List<String> val = resultStore.get("-43");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("43");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("-44");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("44");
         Assert.assertTrue(val.size() == 1);
         sampleRule = "~&[-43, 43, -44, 44]";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestUnary.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         val = resultStore.get("-43");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("43");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("-44");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("44");
         Assert.assertTrue(val.size() == 0);
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void t5() {
      try {
         resultStore = StaticTestingUtils.parseMessage(TestUnary.sampleMessage1);
         List<String> val = resultStore.get("-43");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("43");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("-44");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("44");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("382");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("655");
         Assert.assertTrue(val.size() == 2);
         val = resultStore.get("375");
         Assert.assertTrue(val.size() == 2);
         sampleRule = "~&[-43, 43, 382, -44, 44]";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestUnary.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         val = resultStore.get("-43");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("43");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("-44");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("44");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("655");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("382");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("375");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("45");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("8");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("9");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("35");
         Assert.assertTrue(val.size() == 1);
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void t6() {
      try {
         resultStore = StaticTestingUtils.parseMessage(TestUnary.sampleMessage1);
         List<String> val = resultStore.get("-43");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("43");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("-44");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("44");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("382");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("375");
         Assert.assertTrue(val.size() == 2);
         val = resultStore.get("655");
         Assert.assertTrue(val.size() == 2);
         sampleRule = "+&[8, 9, 35, 44, 43, -43, -44, 45]";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestUnary.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         val = resultStore.get("8");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("9");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("35");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("44");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("43");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("-43");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("-44");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("45");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("382");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("375");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("655");
         Assert.assertTrue(val.size() == 0);
         System.out.println(StaticTestingUtils.rs(result));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void t7() {
      try {
         resultStore = StaticTestingUtils.parseMessage(TestUnary.sampleMessage1);
         List<String> val = resultStore.get("-43");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("43");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("-44");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("44");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("382");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("655");
         Assert.assertTrue(val.size() == 2);
         val = resultStore.get("375");
         Assert.assertTrue(val.size() == 2);
         sampleRule = "~&[-43, 43, 382, -44, 44, 382]";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestUnary.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         val = resultStore.get("-43");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("43");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("-44");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("44");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("655");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("382");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("375");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("45");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("8");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("9");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("35");
         Assert.assertTrue(val.size() == 1);
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   static final String sampleMessage1 = "8=FIX.4.4" + '\u0001' + "9=1042" + '\u0001' + "35=8"
      + '\u0001' + "44=3.142" + '\u0001' + "43=-1" + '\u0001' + "-43=-1" + '\u0001' + "-44=1"
      + '\u0001' + "45=0" + '\u0001' + "382=2" + '\u0001' + "375=FOO" + '\u0001' + "655=eb8cd"
      + '\u0001' + "375=BAR" + '\u0001' + "655=8dhosb" + '\u0001' + "10=004";

   @Test
   public void t7a() {
      try {
         resultStore = StaticTestingUtils.parseMessage(TestUnary.sampleMessage1);
         List<String> val = resultStore.get("382");
         Assert.assertTrue(val.size() == 1);
         Assert.assertEquals(val.get(0), "2");
         val = resultStore.get("-43");
         Assert.assertEquals(val.get(0), "-1");
         val = resultStore.get("45");
         Assert.assertEquals(val.get(0), "0");
         val = resultStore.get("8");
         Assert.assertEquals(val.get(0), "FIX.4.4");
         val = resultStore.get("9");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("375");
         Assert.assertTrue(val.size() == 2);
         Assert.assertEquals(val.get(0), "FOO");
         Assert.assertEquals(val.get(1), "BAR");
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void t9() {
      try {
         resultStore = StaticTestingUtils.parseMessage(TestUnary.sampleMessage1);
         List<String> val = resultStore.get("-43");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("43");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("-44");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("44");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("382");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("375");
         Assert.assertTrue(val.size() == 2);
         val = resultStore.get("655");
         Assert.assertTrue(val.size() == 2);
         sampleRule = "+&[382]";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestUnary.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         val = resultStore.get("8");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("9");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("35");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("44");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("43");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("-43");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("-44");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("45");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("382");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("375");
         Assert.assertTrue(val.size() == 2);
         val = resultStore.get("655");
         Assert.assertTrue(val.size() == 2);
         System.out.println(StaticTestingUtils.rs(result));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void t8() {
      try {
         resultStore = StaticTestingUtils.parseMessage(TestUnary.sampleMessage1);
         List<String> val = resultStore.get("-43");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("43");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("-44");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("44");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("382");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("655");
         Assert.assertTrue(val.size() == 2);
         val = resultStore.get("375");
         Assert.assertTrue(val.size() == 2);
         sampleRule = "~&[-43, 43, -44, 44];~&382[0]->&375";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestUnary.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         val = resultStore.get("-43");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("43");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("-44");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("44");
         Assert.assertTrue(val.size() == 0);
         val = resultStore.get("655");
         Assert.assertTrue(val.size() == 2);
         val = resultStore.get("382");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("375");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("45");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("8");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("9");
         Assert.assertTrue(val.size() == 1);
         val = resultStore.get("35");
         Assert.assertTrue(val.size() == 1);
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void t10() {
      try {
         sampleRule = "~&8";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestUnary.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void t11() {
      try {
         sampleRule = "~&[8]";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestUnary.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }
}
