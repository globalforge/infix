package com.globalforge.infix;

import org.junit.Assert;
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
public class TestExprParenSimple {
    static final String sampleMessage2 = "8=FIX.4.4" + '\u0001' + "9=1000" + '\u0001' + "35=D"
        + '\u0001' + "43=-1" + '\u0001' + "-43=-1" + '\u0001' + "-44=1" + '\u0001' + "44=3.142"
        + '\u0001' + "161=45.34" + '\u0001' + "60=20130412-19:30:00.686" + '\u0001' + "75=20130412"
        + '\u0001' + "45=0" + '\u0001' + "453=2" + '\u0001' + "448=1.5" + '\u0001' + "447=eb8cd"
        + '\u0001' + "802=2" + '\u0001' + "523=\"STR\"" + '\u0001' + "803=8" + '\u0001'
        + "523=\"MCS\"" + '\u0001' + "803=22" + '\u0001' + "448=3" + '\u0001' + "447=8dhosb"
        + '\u0001' + "207=\"USA\"" + '\u0001' + "10=004";
    static StaticTestingUtils msgStore = null;
    InfixSimpleActions rules = null;
    String sampleRule = null;
    String result = null;
    ListMultimap<String, String> resultStore = null;

    @Test
    public void test1() {
        try {
            sampleRule = "&45=(&43|1)|2";
            rules = new InfixSimpleActions(sampleRule);
            result = rules.transformFIXMsg(TestExprParenSimple.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get("45").get(0), "-112");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void test2() {
        try {
            sampleRule = "&45=(&-44+1)*2";
            rules = new InfixSimpleActions(sampleRule);
            result = rules.transformFIXMsg(TestExprParenSimple.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get("45").get(0), "4");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void test3() {
        try {
            sampleRule = "&45=&-43+(1*2)";
            rules = new InfixSimpleActions(sampleRule);
            result = rules.transformFIXMsg(TestExprParenSimple.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get("45").get(0), "1");
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
    static final String sampleMessage1_part2 =
        "453=2" + '\u0001' + TestExprParenSimple.Parties_1 + "802=1" + '\u0001'
            + TestExprParenSimple.PtysSubGrp_1 + TestExprParenSimple.Parties_2 + "802=2" + '\u0001'
            + TestExprParenSimple.PtysSubGrp_2 + TestExprParenSimple.PtysSubGrp_3 + "10=004";
    // /////////////////////
    static final String sampleMessage1 =
        TestExprParenSimple.sampleMessage1_part1 + TestExprParenSimple.sampleMessage1_part2;
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
        TestExprParenSimple.sampleMessage1_part1 + TestExprParenSimple.sampleMessage2_part2;
    static final String sampleMessage1_part1 =
        "8=FIX.4.4" + '\u0001' + "9=1000" + '\u0001' + "35=6" + '\u0001' + "43=-1" + '\u0001'
            + "-43=-1" + '\u0001' + "-44=1" + '\u0001' + "44=3.142" + '\u0001'
            + "60=20130412-19:30:00.686" + '\u0001' + "75=20130412" + '\u0001' + "45=0" + '\u0001';
    static final String sampleMessage2_part2 =
        "711=2" + '\u0001' + TestExprParenSimple.UnderlyingInstrument_1_1 + "457=1" + '\u0001'
            + TestExprParenSimple.UndSecAltIDGrp_1 + TestExprParenSimple.UnderlyingInstrument_1_2
            + TestExprParenSimple.UnderlyingInstrument_2_1 + "457=2" + '\u0001'
            + TestExprParenSimple.UndSecAltIDGrp_2 + TestExprParenSimple.UndSecAltIDGrp_3
            + TestExprParenSimple.UnderlyingInstrument_2_2 + "10=004";

    @Test
    public void test13() {
        try {
            sampleRule = "&662 = 42";
            rules = new InfixSimpleActions(sampleRule);
            result = rules.transformFIXMsg(TestExprParenSimple.sampleMessage2);
            resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals(resultStore.get("662").get(0), "42");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
