package com.globalforge.infix;

import org.junit.Assert;
import org.junit.Test;
import com.globalforge.infix.api.InfixActions;
import com.google.common.collect.ListMultimap;

/*
 * <Name>Parties</Name> <Id>453</Id> <Delim>448</Delim> <Member>447</Member>
 * <Member>452</Member>
 */
public class TestExprParen {
    static final String sampleMessage2 = "8=FIX.4.4" + '\u0001' + "9=1000" + '\u0001' + "35=D"
        + '\u0001' + "43=-1" + '\u0001' + "-43=-1" + '\u0001' + "-44=1" + '\u0001' + "44=3.142"
        + '\u0001' + "161=45.34" + '\u0001' + "60=20130412-19:30:00.686" + '\u0001' + "75=20130412"
        + '\u0001' + "45=0" + '\u0001' + "453=2" + '\u0001' + "448=1.5" + '\u0001' + "447=eb8cd"
        + '\u0001' + "802=2" + '\u0001' + "523=\"STR\"" + '\u0001' + "803=8" + '\u0001'
        + "523=\"MCS\"" + '\u0001' + "803=22" + '\u0001' + "448=3" + '\u0001' + "447=8dhosb"
        + '\u0001' + "207=\"USA\"" + '\u0001' + "10=004";
    static StaticTestingUtils msgStore = null;
    InfixActions rules = null;
    String sampleRule = null;
    String result = null;
    ListMultimap<Integer, String> resultStore = null;

    @Test
    public void test1() {
        try {
            sampleRule = "&45=(&43|1)|2";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprParen.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(45).get(0), "-112");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void test2() {
        try {
            sampleRule = "&45=(&-44+1)*2";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprParen.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(45).get(0), "4");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void test3() {
        try {
            sampleRule = "&45=&-43+(1*2)";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprParen.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(45).get(0), "1");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    // keep tags 9, 45,44,
    @Test
    public void test4() {
        try {
            System.err.println("---START HERE---");
            sampleRule = "&99=&552[0]->&54+(&552[1]->&453[1]->&452*2)"; // 1+(3*2)
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_NEW_ORDER_CROSS, true);
            System.err.println("---END HERE---");
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(99).get(0), "7");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void test5() {
        try {
            sampleRule = "&99=(&711[0]->&462+&711[1]->&457[1]->&458)*2"; // (7+3)*2
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprParen.sampleMessage3, true);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(99).get(0), "20");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    // LOOK HERE
    /*
     * <Group> <Name>Parties</Name> <Id>453</Id> <Delim>448</Delim>
     * <Member>447</Member> <Member>452</Member> </Group> <Group>
     * <Name>PtysSubGrp</Name> <Id>802</Id> <Delim>523</Delim>
     * <Member>803</Member> </Group>
     */
    static final String Parties_1 = "448=1" + '\u0001' + "447=1" + '\u0001' + "452=1" + '\u0001';
    static final String PtysSubGrp_1 = "523=1" + '\u0001' + "803=1" + '\u0001';
    static final String Parties_2 = "448=2" + '\u0001' + "447=2" + '\u0001' + "452=2" + '\u0001';
    static final String PtysSubGrp_2 = "523=2" + '\u0001' + "803=2" + '\u0001';
    static final String PtysSubGrp_3 = "523=3" + '\u0001' + "803=3" + '\u0001';
    // /////////////////////
    static final String sampleMessage1_part2 = "453=2" + '\u0001' + TestExprParen.Parties_1
        + "802=1" + '\u0001' + TestExprParen.PtysSubGrp_1 + TestExprParen.Parties_2 + "802=2"
        + '\u0001' + TestExprParen.PtysSubGrp_2 + TestExprParen.PtysSubGrp_3 + "10=004";
    // /////////////////////
    static final String sampleMessage1 =
        TestExprParen.sampleMessage1_part1 + TestExprParen.sampleMessage1_part2;

    // sampleRule = "&99=&552[0]->&54+(&552[1]->&453[1]->&452*2)"; // 1+(3*2)
    @Test
    public void test6() {
        try {
            sampleRule = "&552[0]->&54=&552[1]->&453[1]->&452*2"; //
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_NEW_ORDER_CROSS);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(54).get(0), "6");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void test7() {
        try {
            sampleRule = "&711[0]->&311= &711 + &711[0]->&311 + (&9/2) -1 + (&45*&44)"; // 2
                                                                                        // +
                                                                                        // 1
                                                                                        // +
                                                                                        // (10
                                                                                        // /
                                                                                        // 2)
                                                                                        // -
                                                                                        // 1
                                                                                        // +
                                                                                        // (0
                                                                                        // *
                                                                                        // 3.142)
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprParen.sampleMessage3);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(311).get(0), "502.000");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void test8() {
        try {
            sampleRule =
                "&711[0]->&311=( ( (&711 + &711[0]->&311) + &9 ) ) / ( ( (2 - (1 + &45) ) * &44) )"; // ((2
                                                                                                     // +
                                                                                                     // 1)
                                                                                                     // +
                                                                                                     // 10)
                                                                                                     // /
                                                                                                     // ((
                                                                                                     // (2
                                                                                                     // -
                                                                                                     // (1+0))
                                                                                                     // *
                                                                                                     // 3.142))
            // 13 / (-3.142)
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprParen.sampleMessage3);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(311).get(0), "319.2234");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    /* default behavior if no parens are used */
    public void test9() {
        try {
            sampleRule =
                "&711[0]->&311=( ( (&711 + &711[0]->&311) + (&9 / 2) ) - 1 ) + (&45 * &44)"; // ((2+1)
                                                                                             // +
                                                                                             // (10/2)-1)
                                                                                             // +
                                                                                             // (0*3.142)
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprParen.sampleMessage3);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(311).get(0), "502.000");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    static final String UnderlyingInstrument_1_1 =
        "311=1" + '\u0001' + "312=1" + '\u0001' + "309=1" + '\u0001' + "305=1" + '\u0001';
    static final String UnderlyingInstrument_1_2 = "462=7" + '\u0001' + "463=1" + '\u0001';
    static final String UnderlyingInstrument_2_1 =
        "311=2" + '\u0001' + "312=2" + '\u0001' + "309=2" + '\u0001' + "305=2" + '\u0001';
    static final String UnderlyingInstrument_2_2 = "462=2" + '\u0001' + "463=2" + '\u0001';
    static final String UndSecAltIDGrp_1 = "458=1" + '\u0001' + "459=1" + '\u0001';
    static final String UndSecAltIDGrp_2 = "458=2" + '\u0001' + "459=2" + '\u0001';
    static final String UndSecAltIDGrp_3 = "458=3" + '\u0001' + "459=3" + '\u0001';
    static final String sampleMessage3 =
        TestExprParen.sampleMessage1_part1 + TestExprParen.sampleMessage2_part2;
    static final String sampleMessage1_part1 =
        "8=FIX.4.4" + '\u0001' + "9=1000" + '\u0001' + "35=6" + '\u0001' + "43=-1" + '\u0001'
            + "-43=-1" + '\u0001' + "-44=1" + '\u0001' + "44=3.142" + '\u0001'
            + "60=20130412-19:30:00.686" + '\u0001' + "75=20130412" + '\u0001' + "45=0" + '\u0001';
    static final String sampleMessage2_part2 =
        "711=2" + '\u0001' + TestExprParen.UnderlyingInstrument_1_1 + "457=1" + '\u0001'
            + TestExprParen.UndSecAltIDGrp_1 + TestExprParen.UnderlyingInstrument_1_2
            + TestExprParen.UnderlyingInstrument_2_1 + "457=2" + '\u0001'
            + TestExprParen.UndSecAltIDGrp_2 + TestExprParen.UndSecAltIDGrp_3
            + TestExprParen.UnderlyingInstrument_2_2 + "10=004";

    // /////////////////////
    // 711 - NoUnderlyings
    // -->UndInstrmtGrp (block) 311,312,309,305, (UndSecAltIDGrp
    // (457),458,459)),462,463
    @Test
    /* default behavior if no parens are used */
    public void test10() {
        try {
            sampleRule = "&711[0]->&457[0]->&458 = &711[1]->&457[1]->&458 + &711[0]->&311"; // 3
                                                                                            // +
                                                                                            // 1
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprParen.sampleMessage3);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(458).get(0), "4");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void test11() {
        try {
            sampleRule = "&711[1]->&457[1]->&458 = &711[0]->&457[0]->&458 + &711[1]->&311"; // 1
                                                                                            // +
                                                                                            // 2
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprParen.sampleMessage3);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(458).get(2), "3");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void test12() {
        try {
            sampleRule = "&711[1]->&457[1]->&458 = 42";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprParen.sampleMessage3);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(458).get(2), "42");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void test13() {
        try {
            sampleRule = "&662 = 42";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestExprParen.sampleMessage2);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get(662).get(0), "42");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
