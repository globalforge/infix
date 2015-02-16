package com.globalforge.infix;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.globalforge.infix.api.InfixActions;
import com.google.common.collect.ListMultimap;

public class TestAssignTerminals {
    static StaticTestingUtils msgStore = null;
    InfixActions rules = null;
    String sampleRule = null;
    String result = null;
    ListMultimap<Integer, String> resultStore = null;

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
            sampleRule = "&35=\"D\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(35).get(0), "D");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testTagINT() {
        try {
            sampleRule = "&44=42";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(44).get(0), "42");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testTagFLOAT1() {
        try {
            sampleRule = "&44=42.01";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(44).get(0), "42.01");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testTagFLOAT2() {
        try {
            sampleRule = "&44=0.01";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(44).get(0), "0.01");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testTagFLOAT3() {
        try {
            sampleRule = "&44=.01";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(44).get(0), "0.01");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testTagFLOAT4() {
        try {
            sampleRule = "&44=.48797209745700345";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            String f = resultStore.get(44).get(0);
            Assert.assertEquals(f, "0.4879721");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testTagTAG1() {
        try {
            sampleRule = "&45=&35";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            String f = resultStore.get(45).get(0);
            Assert.assertEquals(f, "D");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testTagTAG2() {
        try {
            sampleRule = "&45=&44";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            String f = resultStore.get(45).get(0);
            Assert.assertEquals(f, "3.142");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testTagTAG4() {
        try {
            sampleRule = "&45=&45";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            String f = resultStore.get(45).get(0);
            Assert.assertEquals(f, "0");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    // /////////
    @Test
    public void testd10() {
        try {
            sampleRule = "&215=&43"; // -1
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("-1", resultStore.get(215).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testd11() {
        try {
            sampleRule = "&215=&-44"; // 1
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("1", resultStore.get(215).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testd12() {
        try {
            sampleRule = "&215=&-43"; // -1
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("-1", resultStore.get(215).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testd13() {
        try {
            sampleRule = "&-15=&-44"; // 1
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("1", resultStore.get(-15).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testd14() {
        try {
            sampleRule = "&78[1]->&79=&-44"; // 1
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("1", resultStore.get(79).get(1));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testd15() {
        try {
            sampleRule = "&43=-2"; // 1
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("-2", resultStore.get(43).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testd16() {
        try {
            sampleRule = "&43=-2.3"; // 1
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("-2.3", resultStore.get(43).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t17() {
        try {
            sampleRule = "&8=\"FIX.4.2\""; // 1
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("BAR", resultStore.get(79).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t18() {
        try {
            sampleRule = "&8=\"FIX.5.0SP2\""; // 1
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("FOO", resultStore.get(79).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t19() {
        try {
            sampleRule =
                "&8=\"FIX.5.0SP2\";&1058=2;&1058[0]->&1059=1;&1058[0]->&1060=2;&1058[1]->&1059=3;&1058[1]->&1060=4"; // 1
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            // System.out.println(StaticTestingUtils.rs(result));
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("1", resultStore.get(1059).get(0));
            Assert.assertEquals("2", resultStore.get(1060).get(0));
            Assert.assertEquals("3", resultStore.get(1059).get(1));
            Assert.assertEquals("4", resultStore.get(1060).get(1));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t20() {
        try {
            sampleRule =
                "&8=\"FIX.5.0SP2\";&1058=2;&1058[0]->&1059=1;&1058[0]->&1060=2;&1058[1]->&1059=3;&1058[1]->&1060=4"; // 1
            rules = new InfixActions(sampleRule);
            InfixActions.primeEngine("FIX.5.0SP2");
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            // System.out.println(StaticTestingUtils.rs(result));
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("1", resultStore.get(1059).get(0));
            Assert.assertEquals("2", resultStore.get(1060).get(0));
            Assert.assertEquals("3", resultStore.get(1059).get(1));
            Assert.assertEquals("4", resultStore.get(1060).get(1));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t21() {
        try {
            sampleRule =
                "&8=\"FIX.5.0SP2\";  &1058=2;  &1058[0]->&1059=1;  &1058[0]->&1060=2;  &1058[1]->&1059=3;  &1058[1]->&1060=4"; // 1
            rules = new InfixActions(sampleRule);
            InfixActions.primeEngine("FIX.4.4");
            InfixActions.primeEngine("FIX.5.0SP2");
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            // System.out.println(StaticTestingUtils.rs(result));
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("1", resultStore.get(1059).get(0));
            Assert.assertEquals("2", resultStore.get(1060).get(0));
            Assert.assertEquals("3", resultStore.get(1059).get(1));
            Assert.assertEquals("4", resultStore.get(1060).get(1));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t22() {
        try {
            sampleRule =
                "&8=\"FIX.4.4\";&1058=2;&1058[0]->&1059=1;&1058[0]->&1060=2;&1058[1]->&1059=3;&1058[1]->&1060=4"; // 1
            rules = new InfixActions(sampleRule);
            // rules.primeEngine("FIX.4.4");
            // rules.primeEngine("FIX.5.0SP2");
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            // System.out.println(StaticTestingUtils.rs(result));
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("3", resultStore.get(1059).get(0));
            Assert.assertEquals("4", resultStore.get(1060).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t23() {
        try {
            sampleRule =
                "&1058=2;&1058[0]->&1059=1;&1058[0]->&1060=2;&1058[1]->&1059=3;&1058[1]->&1060=4"; // 1
            rules = new InfixActions(sampleRule);
            // rules.primeEngine("FIX.4.4");
            // rules.primeEngine("FIX.5.0SP2");
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            // System.out.println(StaticTestingUtils.rs(result));
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("3", resultStore.get(1059).get(0));
            Assert.assertEquals("4", resultStore.get(1060).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t24() {
        try {
            sampleRule = "&1058=2;&1059=1;&1060=2;&1059=3;&1060=4"; // 1
            rules = new InfixActions(sampleRule);
            // rules.primeEngine("FIX.4.4");
            // rules.primeEngine("FIX.5.0SP2");
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            // System.out.println(StaticTestingUtils.rs(result));
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("3", resultStore.get(1059).get(0));
            Assert.assertEquals("4", resultStore.get(1060).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t25() {
        try {
            sampleRule = "&1058=2;&1059=1;&1060=2"; // 1
            rules = new InfixActions(sampleRule);
            // rules.primeEngine("FIX.4.4");
            // rules.primeEngine("FIX.5.0SP2");
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            // System.out.println(StaticTestingUtils.rs(result));
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("1", resultStore.get(1059).get(0));
            Assert.assertEquals("2", resultStore.get(1060).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    static final String sampleMessage1 = "8=FIX.4.4" + '\u0001' + "9=1042"
        + '\u0001' + "35=D" + '\u0001' + "44=3.142" + '\u0001' + "43=-1"
        + '\u0001' + "-43=-1" + '\u0001' + "-44=1" + '\u0001' + "45=0"
        + '\u0001' + "78=2" + '\u0001' + "79=FOO" + '\u0001' + "80=eb8cd"
        + '\u0001' + "79=BAR" + '\u0001' + "80=8dhosb" + '\u0001' + "10=004";

    @Test
    public void t26() {
        try {
            sampleRule =
                "&683=2;&683[0]->&688=0;&683[0]->&689=1;&683[1]->&688=3;&683[1]->&689=4"; // 1
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            // System.out.println(StaticTestingUtils.rs(result));
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("2", resultStore.get(683).get(0));
            Assert.assertEquals("3", resultStore.get(688).get(0));
            Assert.assertEquals("4", resultStore.get(689).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t27() {
        try {
            sampleRule =
                "&35=8;&683=2;&683[0]->&688=0;&683[0]->&689=1;&683[1]->&688=3;&683[1]->&689=4"; // 1
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestAssignTerminals.sampleMessage1);
            System.out.println(StaticTestingUtils.rs(result));
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("2", resultStore.get(683).get(0));
            Assert.assertEquals("0", resultStore.get(688).get(0));
            Assert.assertEquals("1", resultStore.get(689).get(0));
            Assert.assertEquals("3", resultStore.get(688).get(1));
            Assert.assertEquals("4", resultStore.get(689).get(1));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
