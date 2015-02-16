package com.globalforge.infix;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import com.globalforge.infix.api.InfixActions;
import com.google.common.collect.ListMultimap;

public class TestExprAdd {
    static StaticTestingUtils msgStore = null;
    InfixActions rules = null;
    String sampleRule = null;
    String result = null;
    ListMultimap<Integer, String> resultStore = null;

    @Test
    public void testADD1() {
        try {
            sampleRule = "&45=&45+1";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(45).get(0), "1");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD2() {
        try {
            sampleRule = "&45=&44+1";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(45).get(0), "4.142");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD3() {
        try {
            sampleRule = "&45=&100+1";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprAdd.sampleMessage1, true);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(45).get(0), "0");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD4() {
        try {
            sampleRule = "&45=&382+&44";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(45).get(0), "5.142");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD5() {
        try {
            sampleRule = "&45=&382+&100";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(45).get(0), "0");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD6() {
        try {
            sampleRule = "&100=&382+&100";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            resultStore.get(100).get(0);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testADD7() {
        try {
            sampleRule = "&100=&100";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            List<String> r = resultStore.get(100);
            Assert.assertTrue(r.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD8() {
        try {
            sampleRule = "&45=&100";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(45).get(0), "0");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD9() {
        try {
            sampleRule = "&45=&44+\"FOO\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprAdd.sampleMessage1);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testADD10() {
        try {
            sampleRule = "&45=&382 + &44 + &9";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(45).get(0), "15.142");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    static final String sampleMessage1 = "8=FIX.4.4" + '\u0001' + "9=10"
        + '\u0001' + "35=8" + '\u0001' + "43=-1" + '\u0001' + "-43=-1"
        + '\u0001' + "-44=1" + '\u0001' + "44=3.142" + '\u0001'
        + "60=20130412-19:30:00.686" + '\u0001' + "75=20130412" + '\u0001'
        + "45=0" + '\u0001' + "382=2" + '\u0001' + "375=FOO" + '\u0001'
        + "337=eb8cd" + '\u0001' + "375=BAR" + '\u0001' + "337=8dhosb"
        + '\u0001' + "10=004";

    @Test
    public void testADD11() {
        try {
            sampleRule = "&45=&382 + &100 + &9";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(45).get(0), "0");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD12() {
        try {
            sampleRule = "&45=&100 + &382 + &9";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(45).get(0), "0");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD13() {
        try {
            sampleRule = "&45=&382 + &9 + &100 ";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(45).get(0), "0");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD14() {
        try {
            sampleRule = "&45=";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprAdd.sampleMessage1);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testADD15() {
        try {
            sampleRule = "&45";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprAdd.sampleMessage1);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testADD16() {
        try {
            sampleRule = "45";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprAdd.sampleMessage1);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testADD17() {
        try {
            sampleRule = "45=&44";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprAdd.sampleMessage1);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    // /////////
    @Test
    public void testd10() {
        try {
            sampleRule = "&382=&382 + &43"; // 2 + -1
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("1", resultStore.get(382).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testd11() {
        try {
            sampleRule = "&382=&382 + &-44"; // 2 + 1
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("3", resultStore.get(382).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testd12() {
        try {
            sampleRule = "&382=&43 + &-43"; // -1 + -1
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("-2", resultStore.get(382).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testd13() {
        try {
            sampleRule = "&-15=&43 + &-44"; // -1 + 1
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("0", resultStore.get(-15).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testADD22() {
        try {
            sampleRule = "&382[1]->&375=&43 + &-44"; // -1 + 1
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprAdd.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("0", resultStore.get(375).get(1));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
