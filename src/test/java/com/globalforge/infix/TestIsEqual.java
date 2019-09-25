package com.globalforge.infix;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class TestIsEqual {
   static Logger logger = LoggerFactory.getLogger(TestIsEqual.class);
   static {
   }
   static final String sampleMessage1 = "8=FIX.4.4" + '\u0001' + "9=1000" + '\u0001' + "35=8"
      + '\u0001' + "43=-1" + '\u0001' + "-43=-1.25" + '\u0001' + "-44=1" + '\u0001' + "44=3.142"
      + '\u0001' + "60=20130412-19:30:00.686" + '\u0001' + "75=20130412" + '\u0001' + "45=0"
      + '\u0001' + "47=0" + '\u0001' + "48=1.5" + '\u0001' + "49=8dhosb" + '\u0001' + "382=2"
      + '\u0001' + "375=1.5" + '\u0001' + "655=eb8cd" + '\u0001' + "375=3" + '\u0001' + "655=8dhosb"
      + '\u0001' + "207=FOOBAR" + '\u0001' + "10=004";
   InfixActions rules = null;
   String sampleRule = null;
   String result = null;
   ListMultimap<String, String> resultStore = null;

   @Test
   public void testIsEqual1() {
      try {
         sampleRule = "&45==0 ? &45=1";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("1", resultStore.get("45").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testIsEqual2() {
      try {
         sampleRule = "&45==1?&45=2";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("0", resultStore.get("45").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testIsEqualElse() {
      try {
         sampleRule = "&45==1 ? &45=2 : &45=3";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("3", resultStore.get("45").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testIsEqualElse2() {
      try {
         sampleRule = "&45==0 ? &45=3 : &45=2";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("3", resultStore.get("45").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testIsEqualElse3() {
      try {
         sampleRule = "&45==&47 ? &45=\"FOO\" : &45=2";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("FOO", resultStore.get("45").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testIsEqualElse4() {
      try {
         sampleRule = "&45==&382 ? &45=\"FOO\" : &45=\"BAR\"";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("BAR", resultStore.get("45").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testIsEqualElse5() {
      try {
         sampleRule = "&49==&382[1]->&655 ? &45=\"FOO\" : &45=\"BAR\"";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("FOO", resultStore.get("45").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testIsEqualElse6() {
      try {
         sampleRule = "&49==&382[1]->&655 ? &655=\"FOO\" : &45=\"BAR\"";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("FOO", resultStore.get("655").get(2));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testIsEqualElse7() {
      try {
         sampleRule = "&49==&382[0]->&655 ? &655=\"FOO\" : &382[0]->&655=\"BAR\"";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("BAR", resultStore.get("655").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testIsEqualElse8() {
      try {
         sampleRule = "&49==\"8dhosb\" ? &382[0]->&655=\"FOO\" : &382[0]->&655=\"BAR\"";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("FOO", resultStore.get("655").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testIsEqualElse9() {
      try {
         sampleRule = "&49==\"8dhosb\" ? &382[0]->&655=\"FOO\" : &382[0]->&655=\"BAR\"";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("FOO", resultStore.get("655").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   /* FAIL with parser error but no way to test */
   @Test
   public void testIsEqualElse10() {
      try {
         sampleRule = "&49== ? &382[0]->&655=\"FOO\" : &382[0]->&655=\"BAR\"";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         // Assert.fail();
      } catch (Exception e) {
      }
   }

   @Test
   public void testIsEqualElse11() {
      try {
         sampleRule = "&49==\"\" ? &382[0]->&655=\"FOO\" : &382[0]->&655=\"BAR\"";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("BAR", resultStore.get("655").get(0));
      } catch (Exception e) {
      }
   }

   /* FAIL with parser error but no way to test */
   @Test
   public void testIsEqualElse12() {
      try {
         sampleRule = "&49==\"8dhosb\" ? &382[0]->&655= : &382[0]->&655=\"BAR\"";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         // Assert.fail();
      } catch (Exception e) {
      }
   }

   @Test
   public void testIsEqualElse13() {
      try {
         sampleRule = "&49==\"8dhosc\" ? &382[0]->&655=111 : &382[0]->&655=";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         String text = StaticTestingUtils.rs(result);
         Assert.fail();
      } catch (Exception e) {
      }
   }

   @Test
   public void testIsEqualElse13b() {
      try {
         // sampleRule = "&49==\"8dhosc\" ? &382[0]->&655=111 :
         // &382[0]->&655=";
         sampleRule = "&49==\"8dhosc\" ? &382[0]->&655=\"FOO\" : &382[0]->&655=\"";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         String text = StaticTestingUtils.rs(result);
         Assert.fail();
      } catch (Exception e) {
      }
   }

   @Test
   public void testIsEqual14() {
      try {
         sampleRule = "&45==0 ? &45=1.005";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("1.005", resultStore.get("45").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testIsEqual3() {
      try {
         sampleRule = "&45==1?&45=2.0";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("0", resultStore.get("45").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testIsEqualElseF1() {
      try {
         sampleRule = "&45==1 ? &45=2 : &45=3.13";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("3.13", resultStore.get("45").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testIsEqualElseF2() {
      try {
         sampleRule = "&49==&382[1]->&655 ? &382[1]->&655=4.3 : &45=\"BAR\"";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("4.3", resultStore.get("655").get(1));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testIsEqualElseF3() {
      try {
         sampleRule = "&49==&382[0]->&655 ? &382[1]->&655=4.3 : &382[0]->&655=6.6";
         rules = new InfixActions(sampleRule);
         long start = System.currentTimeMillis();
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         long end = System.currentTimeMillis();
         long delay = end - start;
         System.out.println("transform time: " + delay);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("6.6", resultStore.get("655").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testIsEqualFloat1() {
      try {
         sampleRule = "&45==0.0 ? &45=1";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("0", resultStore.get("45").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testIsEqualFloat2() {
      try {
         sampleRule = "&44==3.142 ? &45=1";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("1", resultStore.get("45").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testIsEqualFloat3() {
      try {
         sampleRule = "&47==0.0 ? &45=1";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("0", resultStore.get("45").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   // /////////
   @Test
   public void testd10() {
      try {
         sampleRule = "&43==-1 ? &-43=1";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("1", resultStore.get("-43").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testd11() {
      try {
         sampleRule = "&-43==-1.25 ? &43=-2.3"; // 2 / -1
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("-2.3", resultStore.get("43").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testd12() {
      try {
         sampleRule = "&-43==-1.26 ? &43=-2.3 : &43=2"; // 2 / -1
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("2", resultStore.get("43").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testd13() {
      try {
         sampleRule = "&-43==-1.26 ? &43=-2.3 : &43=2.56"; // 2 / -1
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("2.56", resultStore.get("43").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testd14() {
      try {
         sampleRule = "&-43==-1.26 ? &43=-2.3 : &43=-2"; // 2 / -1
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("-2", resultStore.get("43").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testd15() {
      try {
         sampleRule = "&-43==-1.26 ? &43=-2.3 : &43=-2.56"; // 2 / -1
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("-2.56", resultStore.get("43").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testd16() {
      try {
         sampleRule = "&43==-1.26 ? &43=-2.3 : &43=2.56"; // 2 / -1
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("2.56", resultStore.get("43").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testIsEqual17() {
      try {
         sampleRule = "&207==\"FOOBAR\" ? &45=1";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("1", resultStore.get("45").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testIsEqual18() {
      try {
         sampleRule = "&207=={com.globalforge.infix.example.ExampleUserAssignment} ? &45=1";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("0", resultStore.get("45").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testIsEqual19() {
      try {
         sampleRule = "&44=={com.globalforge.infix.example.ExampleUserAssignment} ? &45=1";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestIsEqual.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("0", resultStore.get("45").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }
}
