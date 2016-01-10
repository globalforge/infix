package com.globalforge.infix;

import org.junit.Assert;
import org.junit.Test;
import com.globalforge.infix.api.InfixActions;
import com.google.common.collect.ListMultimap;

public class TestExprGroupAdd {
    static StaticTestingUtils msgStore = null;
    InfixActions rules = null;
    String sampleRule = null;
    String result = null;
    ListMultimap<Integer, String> resultStore = null;

    @Test
    public void testADD1() {
        try {
            sampleRule = "&382=&382+1";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(382).get(0), "3");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD1dot1() {
        try {
            sampleRule = "&375=&382+1";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(375).get(2), "3");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD1dot2() {
        try {
            sampleRule = "&382[0]->&375=&382+1";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(375).get(0), "3");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD1dot3() {
        try {
            sampleRule = "&382[1]->&375=&382+1";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(375).get(1), "3");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    static final String sampleMessage1 = "8=FIX.4.4" + '\u0001' + "9=1000"
        + '\u0001' + "35=8" + '\u0001' + "44=3.142" + '\u0001'
        + "60=20130412-19:30:00.686" + '\u0001' + "75=20130412" + '\u0001'
        + "45=0" + '\u0001' + "382=2" + '\u0001' + "375=1.3" + '\u0001'
        + "337=eb8cd" + '\u0001' + "375=2" + '\u0001' + "337=8dhosb" + '\u0001'
        + "10=004";

    @Test
    public void testADD1dot4() {
        try {
            sampleRule = "&375[2]=&382+1";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testADD2() {
        try {
            sampleRule = "&382[1]->&375=&44+1";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(375).get(1), "4.142");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD2dot0() {
        try {
            sampleRule = "&382[1]->&375=&44+1+&382[1]->&375";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(375).get(1), "6.142");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD3() {
        try {
            sampleRule = "&382=&100+1";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(382).get(0), "2");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD3dot0() {
        try {
            sampleRule = "&375=&100+1";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            resultStore.get(375).get(2);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testADD3dot1() {
        try {
            sampleRule = "&382[0]->&375=&100+1";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(375).get(0), "1.3");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD3dot2() {
        try {
            sampleRule = "&382[1]->&375=&100+1";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(375).get(1), "2");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD4() {
        try {
            sampleRule = "&382[0]->&375=&382+&44";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(375).get(0), "5.142");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD5() {
        try {
            sampleRule = "&382[1]->&375=&382+&100";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(375).get(1), "2");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD6() {
        try {
            sampleRule = "&100=&382[1]->&375+&100";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            resultStore.get(100).get(0);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testADD7() {
        try {
            sampleRule = "&382[1]->&375=&382[1]->&375";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(375).get(1), "2");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD8() {
        try {
            sampleRule = "&382[1]->&375=&100";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(375).get(1), "2");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD9() {
        try {
            sampleRule = "&382[0]->&375=&44+\"FOO\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    // //////
    @Test
    public void testADD10() {
        try {
            sampleRule = "&382[0]->&375=&382 + &44 + &9";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(375).get(0), "1005.142");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD11() {
        try {
            sampleRule = "&382[1]->&375=&382 + &100 + &9";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(375).get(1), "2");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD12() {
        try {
            sampleRule = "&375=&100 + &382 + &9";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            resultStore.get(375).get(2);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testADD13() {
        try {
            sampleRule = "&382[1]->&375=&382 + &9 + &100 ";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(375).get(1), "2");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD14() {
        try {
            sampleRule = "&382[1]->&375=";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testADD15() {
        try {
            sampleRule = "&375";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testADD16() {
        try {
            sampleRule = "375";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testADD17() {
        try {
            sampleRule = "375=&44";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprGroupAdd.sampleMessage1);
            Assert.fail();
        } catch (Exception e) {
        }
    }
}
