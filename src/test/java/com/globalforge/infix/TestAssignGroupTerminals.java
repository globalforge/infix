package com.globalforge.infix;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.globalforge.infix.api.InfixActions;
import com.google.common.collect.ListMultimap;

public class TestAssignGroupTerminals {
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
    public void testGrpTagVAL() {
        try {
            sampleRule = "&375=\"D\"";
            rules = new InfixActions(sampleRule);
            result =
                rules.transformFIXMsg(TestAssignGroupTerminals.sampleMessage1,
                    true); // System.out.println(result);
            resultStore = StaticTestingUtils.parseMessage(result);
            String r = resultStore.get(375).get(2);
            Assert.assertEquals(r, "D");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testGrpTagVAL2() {
        try {
            sampleRule = "&382[0]->&375=\"D\"";
            rules = new InfixActions(sampleRule);
            result =
                rules.transformFIXMsg(TestAssignGroupTerminals.sampleMessage1,
                    true); // System.out.println(result);
            resultStore = StaticTestingUtils.parseMessage(result);
            String r = resultStore.get(375).get(0);
            Assert.assertEquals(r, "D");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testGrpTagVAL3() {
        try {
            sampleRule = "&382[1]->&375=\"D\"";
            rules = new InfixActions(sampleRule);
            result =
                rules.transformFIXMsg(TestAssignGroupTerminals.sampleMessage1); //
            System.out.println(StaticTestingUtils.rs(result));
            resultStore = StaticTestingUtils.parseMessage(result);
            String r = resultStore.get(375).get(1);
            Assert.assertEquals(r, "D");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testGrpTagVAL4() {
        try {
            sampleRule = "&375[2]=\"D\"";
            rules = new InfixActions(sampleRule);
            result =
                rules.transformFIXMsg(TestAssignGroupTerminals.sampleMessage1); //
            System.out.println(StaticTestingUtils.rs(result));
            resultStore = StaticTestingUtils.parseMessage(result);
            String r = resultStore.get(375).get(1);
            Assert.assertEquals(r, "BAR");
        } catch (Exception e) {
        }
    }

    @Test
    public void testTagINT() {
        try {
            sampleRule = "&375=1";
            System.out.println("before: "
                + StaticTestingUtils
                    .rs(TestAssignGroupTerminals.sampleMessage1));
            rules = new InfixActions(sampleRule);
            result =
                rules.transformFIXMsg(TestAssignGroupTerminals.sampleMessage1);
            System.out.println("after : " + StaticTestingUtils.rs(result));
            resultStore = StaticTestingUtils.parseMessage(result);
            // Improper context usage will should modify the last instance.
            String r = resultStore.get(375).get(2);
            Assert.assertEquals(r, "1");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testTagINT11() {
        try {
            sampleRule = "&337=1";
            rules = new InfixActions(sampleRule);
            result =
                rules.transformFIXMsg(TestAssignGroupTerminals.sampleMessage1);
            System.out.println(StaticTestingUtils.rs(result));
            resultStore = StaticTestingUtils.parseMessage(result);
            // Improper context usage will should modify the last instance.
            String r = resultStore.get(337).get(2);
            Assert.assertEquals(r, "1");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testTagINT1() {
        try {
            sampleRule = "&382[0]->&375=1";
            rules = new InfixActions(sampleRule);
            result =
                rules.transformFIXMsg(TestAssignGroupTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            String r = resultStore.get(375).get(0);
            Assert.assertEquals(r, "1");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testTagINT2() {
        try {
            sampleRule = "&382[1]->&375=1";
            rules = new InfixActions(sampleRule);
            result =
                rules.transformFIXMsg(TestAssignGroupTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            String r = resultStore.get(375).get(1);
            Assert.assertEquals(r, "1");
        } catch (Exception e) {
            Assert.fail();
        }
    }
    static final String sampleMessage1 = "8=FIX.4.4" + '\u0001' + "9=52"
        + '\u0001' + "35=8" + '\u0001' + "44=3.142" + '\u0001' + "45=0"
        + '\u0001' + "382=2" + '\u0001' + "375=FOO" + '\u0001' + "337=eb8cd"
        + '\u0001' + "375=BAR" + '\u0001' + "337=8dhosb" + '\u0001' + "10=004";

    @Test
    public void testTagINT3() {
        try {
            sampleRule = "&375[2]=1";
            rules = new InfixActions(sampleRule);
            result =
                rules.transformFIXMsg(TestAssignGroupTerminals.sampleMessage1);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertNull(resultStore);
        }
    }

    @Test
    public void testTagFLOAT1() {
        try {
            sampleRule = "&375=42.01";
            rules = new InfixActions(sampleRule);
            result =
                rules.transformFIXMsg(TestAssignGroupTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(375).get(2), "42.01");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testTagFLOAT2() {
        try {
            sampleRule = "&382[0]->&375=0.01";
            rules = new InfixActions(sampleRule);
            result =
                rules.transformFIXMsg(TestAssignGroupTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(375).get(0), "0.01");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testTagFLOAT3() {
        try {
            sampleRule = "&382[1]->&375=.01";
            rules = new InfixActions(sampleRule);
            result =
                rules.transformFIXMsg(TestAssignGroupTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(375).get(1), "0.01");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testTagFLOAT4() {
        try {
            sampleRule = "&375[2]=.48797209745700345";
            rules = new InfixActions(sampleRule);
            result =
                rules.transformFIXMsg(TestAssignGroupTerminals.sampleMessage1);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testTagTAG1() {
        try {
            System.out.println(StaticTestingUtils
                .rs(TestAssignGroupTerminals.sampleMessage1));
            sampleRule = "&382[0]->&375=&382[0]->&337";
            rules = new InfixActions(sampleRule);
            result =
                rules.transformFIXMsg(TestAssignGroupTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            System.out.println(StaticTestingUtils.rs(result));
            Assert.assertEquals(resultStore.get(375).get(0), "eb8cd");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testTagTAG2() {
        try {
            sampleRule = "&375=&382[1]->&337";
            rules = new InfixActions(sampleRule);
            result =
                rules.transformFIXMsg(TestAssignGroupTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(375).get(2), "8dhosb");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testTagTAG3() {
        try {
            sampleRule = "&375=&382[1]->&337";
            rules = new InfixActions(sampleRule);
            result =
                rules.transformFIXMsg(TestAssignGroupTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            int sz = resultStore.get(375).size();
            Assert.assertEquals(sz, 3);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testTagTAG4() {
        try {
            sampleRule = "&382[0]->&375=&382[1]->&337";
            rules = new InfixActions(sampleRule);
            result =
                rules.transformFIXMsg(TestAssignGroupTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(375).get(0), "8dhosb");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testTagTAG5() {
        try {
            sampleRule = "&382[1]->&375=&382[0]->&337";
            rules = new InfixActions(sampleRule);
            result =
                rules.transformFIXMsg(TestAssignGroupTerminals.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(375).get(1), "eb8cd");
        } catch (Exception e) {
            Assert.fail();
        }
    }
}