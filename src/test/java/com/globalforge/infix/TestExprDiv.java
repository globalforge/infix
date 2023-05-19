package com.globalforge.infix;

import org.junit.Assert;
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
public class TestExprDiv {
   static final String sampleMessage1 = "8=FIX.4.4" + '\u0001' + "9=1000" + '\u0001' + "35=D"
      + '\u0001' + "43=-1" + '\u0001' + "-43=-1" + '\u0001' + "-44=1" + '\u0001' + "44=3.142"
      + '\u0001' + "60=20130412-19:30:00.686" + '\u0001' + "75=20130412" + '\u0001' + "45=0"
      + '\u0001' + "453=2" + '\u0001' + "448=1.5" + '\u0001' + "447=eb8cd" + '\u0001' + "448=3"
      + '\u0001' + "447=8dhosb" + '\u0001' + "10=004";
   static StaticTestingUtils msgStore = null;
   InfixActions rules = null;
   String sampleRule = null;
   String result = null;
   ListMultimap<String, String> resultStore = null;

   @Test
   public void testd1() {
      try {
         sampleRule = "&45=&45/1";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestExprDiv.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals(resultStore.get("45").get(0), "0");
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testd2() {
      try {
         sampleRule = "&45=&45/0";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestExprDiv.sampleMessage1);
         Assert.fail();
      } catch (Exception e) {
      }
   }

   @Test
   public void testd3() {
      try {
         sampleRule = "&453=&9/1.5";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestExprDiv.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals(resultStore.get("453").get(0), "666.6667");
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testd4() {
      try {
         sampleRule = "&448=12/2/3";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestExprDiv.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals(resultStore.get("448").get(2), "2");
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testd5() {
      try {
         sampleRule = "&453[0]->&448=&453[1]->&448/&453[0]->&448"; // 3 / 1.5
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestExprDiv.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals(resultStore.get("448").get(0), "2");
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testd6() {
      try {
         sampleRule = "&453[1]->&448=&453[1]->&448/&453[0]->&448"; // 3 / 1.5
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestExprDiv.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals(resultStore.get("448").get(1), "2");
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testd7() {
      try {
         sampleRule = "&453[1]->&448=&44/&453[0]->&448/&9/&453[1]->&448/2"; // 3.142
                                                                           // /
                                                                           // 1.5
                                                                           // /
                                                                           // 10
                                                                           // /
                                                                           // 3
                                                                           // /
                                                                           // 2
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestExprDiv.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals(resultStore.get("448").get(1), "0.0003491112");
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testd8() {
      try {
         sampleRule = "&453[1]->&448=&44/&453[0]->&448/&9/&453[1]->&448/0";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestExprDiv.sampleMessage1);
         Assert.fail();
      } catch (Exception e) {
      }
   }

   @Test
   public void testd9() {
      try {
         sampleRule = "&453[1]->&448=&44/&453[0]->&448/&45/&453[1]->&448/2";
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestExprDiv.sampleMessage1);
         Assert.fail();
      } catch (Exception e) {
      }
   }

   // /////////
   @Test
   public void testd10() {
      try {
         sampleRule = "&453=&453 / &43"; // 2 / -1
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestExprDiv.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("-2", resultStore.get("453").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testd11() {
      try {
         sampleRule = "&453=&453 / &-44"; // 2 / 1
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestExprDiv.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("2", resultStore.get("453").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testd12() {
      try {
         sampleRule = "&453=&43 / &-43"; // -1 / -1
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestExprDiv.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("1", resultStore.get("453").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testd13() {
      try {
         sampleRule = "&-15=&43 / &-44"; // -1 / 1
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestExprDiv.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("-1", resultStore.get("-15").get(0));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }

   @Test
   public void testd14() {
      try {
         sampleRule = "&453[1]->&448=&43 / &-44"; // -1 / 1
         rules = new InfixActions(sampleRule);
         result = rules.transformFIXMsg(TestExprDiv.sampleMessage1);
         resultStore = StaticTestingUtils.parseMessage(result);
         Assert.assertEquals("-1", resultStore.get("448").get(1));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }
}
