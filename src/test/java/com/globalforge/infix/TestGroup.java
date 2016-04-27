package com.globalforge.infix;

import java.util.ArrayList;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import com.globalforge.infix.api.InfixActions;
import com.globalforge.infix.api.InfixField;

public class TestGroup {
    static final String sampleMessage1 = "8=FIX.4.4 " + '\u0001' + "9=10" + '\u0001' + "35=8"
        + '\u0001' + "43=-1" + '\u0001' + "207=USA" + '\u0001' + "10=004";
    InfixActions rules = null;
    String sampleRule = null;
    String result = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Test
    public void t1() {
        try {
            sampleRule = "&35=\"D\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage1);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void t2() {
        try {
            sampleRule = "&382=1;&375=1;&655=2;&218=4";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage1);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(382, "1"));
            Assert.assertTrue(wasDone >= 0);
            wasDone = myList.indexOf(new InfixField(375, "1"));
            Assert.assertTrue(wasDone >= 0);
            wasDone = myList.indexOf(new InfixField(655, "2"));
            Assert.assertTrue(wasDone >= 0);
            wasDone = myList.indexOf(new InfixField(218, "4"));
            Assert.assertTrue(wasDone >= 0);
            Assert.assertEquals(10, myList.size());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t3() {
        try {
            sampleRule = "&382=1;&382[0]->&375=1;&382[0]->&655=2;&382[0]->&218=3";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage1);
            Assert.fail(); // no such member 218 in group 382
        } catch (Exception e) {
        }
    }

    @Test
    public void t3$2() {
        try {
            sampleRule = "&382=1;&382[0]->&375=1;&382[0]->&655=2;";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage1);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(5);
            Assert.assertEquals("1", fld.getTagVal());
            Assert.assertEquals(382, fld.getTagNum());
            fld = myList.get(6);
            Assert.assertEquals("1", fld.getTagVal());
            Assert.assertEquals(375, fld.getTagNum());
            fld = myList.get(7);
            Assert.assertEquals("2", fld.getTagVal());
            Assert.assertEquals(655, fld.getTagNum());
            Assert.assertEquals(9, myList.size());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t4() {
        try {
            sampleRule = "&382[0]->&375=\"MIKE\";&382[0]->&655=99";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(5);
            Assert.assertEquals("MIKE", fld.getTagVal());
            Assert.assertEquals(375, fld.getTagNum());
            fld = myList.get(6);
            Assert.assertEquals("99", fld.getTagVal());
            Assert.assertEquals(655, fld.getTagNum());
            fld = myList.get(7);
            Assert.assertEquals("USA", fld.getTagVal());
            Assert.assertEquals(207, fld.getTagNum());
            Assert.assertEquals(9, myList.size());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    static final String sampleMessage2 = "8=FIX.4.4" + '\u0001' + "9=10" + '\u0001' + "35=8"
        + '\u0001' + "43=-1" + '\u0001' + "382=1" + '\u0001' + " 375=FOO" + '\u0001' + "655=2"
        + '\u0001' + "207=USA" + '\u0001' + "10=004";

    @Test
    public void t5() {
        try {
            sampleRule = "&382[0]->&375=\"MIKE\";&382[0]->&655=99;";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(5);
            Assert.assertEquals("MIKE", fld.getTagVal());
            Assert.assertEquals(375, fld.getTagNum());
            fld = myList.get(6);
            Assert.assertEquals("99", fld.getTagVal());
            Assert.assertEquals(655, fld.getTagNum());
            fld = myList.get(7);
            Assert.assertEquals("USA", fld.getTagVal());
            Assert.assertEquals(207, fld.getTagNum());
            Assert.assertEquals(9, myList.size());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t6() {
        try {
            sampleRule = "&382[0]->&375=\"MIKE\";&382[0]->&655=99;&382[0]->&337=\"XYZ\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(5);
            Assert.assertEquals("MIKE", fld.getTagVal());
            Assert.assertEquals(375, fld.getTagNum());
            fld = myList.get(7);
            Assert.assertEquals("99", fld.getTagVal());
            Assert.assertEquals(655, fld.getTagNum());
            fld = myList.get(6);
            Assert.assertEquals("XYZ", fld.getTagVal());
            Assert.assertEquals(337, fld.getTagNum());
            fld = myList.get(8);
            Assert.assertEquals("USA", fld.getTagVal());
            Assert.assertEquals(207, fld.getTagNum());
            Assert.assertEquals(10, myList.size());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    static final String sampleMessage3 = "8=FIX.4.4" + '\u0001' + "9=1000" + '\u0001' + "35=8"
        + '\u0001' + "43=-1" + '\u0001' + "382=2" + '\u0001' + "375=FOO" + '\u0001' + "655=2"
        + '\u0001' + "375=BAR" + '\u0001' + " 655=3 " + '\u0001' + "207=USA" + '\u0001' + "10=004";

    @Test
    public void t7() {
        try {
            sampleRule = "&382[0]->&337=\"MIKE\";&382[1]->&655=99;&382[1]->&337=\"XYZ\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage3);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(6);
            Assert.assertEquals("MIKE", fld.getTagVal());
            Assert.assertEquals(337, fld.getTagNum());
            fld = myList.get(10);
            Assert.assertEquals("99", fld.getTagVal());
            Assert.assertEquals(655, fld.getTagNum());
            fld = myList.get(9);
            Assert.assertEquals("XYZ", fld.getTagVal());
            Assert.assertEquals(337, fld.getTagNum());
            fld = myList.get(11);
            Assert.assertEquals("USA", fld.getTagVal());
            Assert.assertEquals(207, fld.getTagNum());
            Assert.assertEquals(13, myList.size());
            // System.out.println(TestResultsStore.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    // name = [InstrmtLegExecGrp], id = [555], members = [555|600|601|602
    // name = [NestedParties], id = [539], members = [539|524|525|538],
    // 382->555, 78->539, 79->524
    // size: 22
    // static final String sampleMessage4 = "8=FIX.4.4" + '\u0001' + "9=1000" +
    // '\u0001' + "35=8"
    // + '\u0001' + "43=-1" + '\u0001' + "555=2" + '\u0001' + "600=FOO" +
    // '\u0001' + "601=2"
    // + '\u0001' + "539=2" + '\u0001' + "524=STR" + '\u0001' + "525=8" +
    // '\u0001' + "538=-33"
    // + '\u0001' + "524=MCS" + '\u0001' + "525=22" + '\u0001' + "538=33" +
    // '\u0001' + "600=FOO1"
    // + '\u0001' + "601=3" + '\u0001' + "539=1" + '\u0001' + "524=STR1" +
    // '\u0001' + "525=0"
    // + '\u0001' + "538=-34" + '\u0001' + "207=USA" + '\u0001' + "10=004 ";

    @Test
    public void t8() {
        try {
            sampleRule = "&552[0]->&453[0]->&448=\"STARK\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_NEW_ORDER_CROSS);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(448, "STARK"));
            Assert.assertTrue(wasDone >= 0);
            Assert.assertEquals(36, myList.size());
            // System.out.println(TestResultsStore.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t9() {
        try {
            sampleRule = "&552[0]->&453[1]->&448=\"STARK\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_NEW_ORDER_CROSS);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(448, "STARK"));
            Assert.assertTrue(wasDone >= 0);
            System.out.println(StaticTestingUtils.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t10() {
        try {
            sampleRule = "&552[0]->&54=\"STARK\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_NEW_ORDER_CROSS);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(54, "STARK"));
            Assert.assertTrue(wasDone >= 0);
            Assert.assertEquals(36, myList.size());
            // System.out.println(TestResultsStore.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t11() {
        try {
            sampleRule = "&552[1]->&54=\"STARK\"";
            rules = new InfixActions(sampleRule);
            System.out.println(
                "before: " + StaticTestingUtils.rs(StaticTestingUtils.FIX_44_NEW_ORDER_CROSS));
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_NEW_ORDER_CROSS, true);
            System.out.println("after :  " + StaticTestingUtils.rs(result));
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(54, "STARK"));
            Assert.assertTrue(wasDone >= 0);
            Assert.assertEquals(36, myList.size());
            // System.out.println(TestResultsStore.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t12() {
        try {
            sampleRule = "&552[0]->&54=\"STARK\";&552[1]->&54=\"STARK\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_NEW_ORDER_CROSS);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(54, "STARK"));
            Assert.assertTrue(wasDone >= 0);
            int wasDoneAgain = myList.lastIndexOf(new InfixField(54, "STARK"));
            Assert.assertTrue(wasDone >= 0);
            Assert.assertTrue(wasDoneAgain >= 0);
            Assert.assertTrue(wasDoneAgain >= wasDone);
            Assert.assertEquals(36, myList.size());
            // System.out.println(TestResultsStore.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t13() {
        try {
            sampleRule = "&552[0]->&453[0]->&448=998;&552[0]->&453[1]->&447=\"WAS\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_NEW_ORDER_CROSS);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(448, "998"));
            Assert.assertTrue(wasDone >= 0);
            wasDone = myList.indexOf(new InfixField(447, "WAS"));
            Assert.assertTrue(wasDone >= 0);
            Assert.assertEquals(36, myList.size());
            // System.out.println(TestResultsStore.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t15() {
        try {
            sampleRule = "~&552[0]->&453[0]->&448";
            rules = new InfixActions(sampleRule);
            System.out.println(
                "before: " + StaticTestingUtils.rs(StaticTestingUtils.FIX_44_NEW_ORDER_CROSS));
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_NEW_ORDER_CROSS);
            System.out.println("after : " + StaticTestingUtils.rs(result));
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(447, "D"));
            Assert.assertTrue(wasDone >= 0);
            Assert.assertEquals(35, myList.size());
            // System.out.println(TestResultsStore.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t16() {
        try {
            sampleRule = "~&552[0]->&453[1]->&448";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_NEW_ORDER_CROSS);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(22);
            Assert.assertEquals("D", fld.getTagVal());
            Assert.assertEquals(447, fld.getTagNum());
            int wasDone = myList.indexOf(new InfixField(448, "AAA35777"));
            Assert.assertTrue(wasDone < 0);
            wasDone = myList.indexOf(new InfixField(447, "D"));
            Assert.assertTrue(wasDone > 0);
            Assert.assertEquals(35, myList.size());
            // System.out.println(TestResultsStore.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t17() {
        try {
            sampleRule = "~&552[0]->&453[1]->&448;&552[0]->&453[1]->&448=999.91";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_NEW_ORDER_CROSS);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(448, "999.91"));
            Assert.assertTrue(wasDone >= 0);
            wasDone = myList.indexOf(new InfixField(448, "AAA35777"));
            Assert.assertTrue(wasDone < 0);
            Assert.assertEquals(36, myList.size());
            // System.out.println(TestResultsStore.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t18() {
        try {
            sampleRule = "~&552[0]->&453[1]->&448;&552[0]->&453[1]->&448=&49";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_NEW_ORDER_CROSS);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(448, "AAA35777"));
            Assert.assertTrue(wasDone < 0);
            wasDone = myList.indexOf(new InfixField(448, "sender"));
            Assert.assertTrue(wasDone > 0);
            Assert.assertEquals(36, myList.size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t18$1() {
        try {
            sampleRule = "~&552[0]->&453";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_NEW_ORDER_CROSS);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            Assert.assertEquals(29, myList.size());
            System.out.println("after : " + StaticTestingUtils.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t19() {
        try {
            sampleRule =
                "&552[0]->&453=4;" + "&552[0]->&453[2]->&448=111;" + "&552[0]->&453[2]->&447=222;"
                    + "&552[0]->&453[3]->&448=333;" + "&552[0]->&453[3]->&447=444";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_NEW_ORDER_CROSS);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            ///
            int wasDone = myList.indexOf(new InfixField(453, "4"));
            Assert.assertTrue(wasDone > 0);
            wasDone = myList.indexOf(new InfixField(448, "111"));
            Assert.assertTrue(wasDone > 0);
            wasDone = myList.indexOf(new InfixField(447, "222"));
            Assert.assertTrue(wasDone > 0);
            wasDone = myList.lastIndexOf(new InfixField(448, "333"));
            Assert.assertTrue(wasDone > 0);
            wasDone = myList.lastIndexOf(new InfixField(447, "444"));
            Assert.assertTrue(wasDone > 0);
            ///
            Assert.assertEquals(40, myList.size());
            // System.out.println(TestResultsStore.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t27() {
        try {
            sampleRule = "~&552[0]->&453;" + "&552[0]->&453=1;" + "&552[0]->&453[0]->&448=\"FOO\";"
                + "&552[0]->&453[0]->&447=12;" + "&552[0]->&453[0]->&452=13;";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_NEW_ORDER_CROSS);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.lastIndexOf(new InfixField(453, "1"));
            Assert.assertTrue(wasDone > 0);
            wasDone = myList.lastIndexOf(new InfixField(448, "FOO"));
            Assert.assertTrue(wasDone > 0);
            wasDone = myList.lastIndexOf(new InfixField(447, "12"));
            Assert.assertTrue(wasDone > 0);
            wasDone = myList.lastIndexOf(new InfixField(452, "13"));
            Assert.assertTrue(wasDone > 0);
            Assert.assertEquals(33, myList.size());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t28() {
        try {
            sampleRule = "&555=1;&555[0]->&600=42";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage7);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            // System.out.println(StaticTestingUtils.rs(result));
            InfixField fld = myList.get(3);
            Assert.assertEquals("1", fld.getTagVal());
            Assert.assertEquals(555, fld.getTagNum());
            fld = myList.get(4);
            Assert.assertEquals("42", fld.getTagVal());
            Assert.assertEquals(600, fld.getTagNum());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t28a() {
        try {
            sampleRule = "&555=1";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage7);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            // System.out.println(StaticTestingUtils.rs(result));
            InfixField fld = myList.get(3);
            Assert.assertEquals("1", fld.getTagVal());
            Assert.assertEquals(555, fld.getTagNum());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t29() {
        try {
            sampleRule = "&555=1;~&555";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage7);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            // System.out.println(StaticTestingUtils.rs(result));
            InfixField fld = myList.get(2);
            Assert.assertEquals("8", fld.getTagVal());
            Assert.assertEquals(35, fld.getTagNum());
            fld = myList.get(3);
            Assert.assertEquals(10, fld.getTagNum());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t30() {
        try {
            sampleRule = "&555=1;~&555;&555=1";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage7);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            // System.out.println(StaticTestingUtils.rs(result));
            InfixField fld = myList.get(3);
            Assert.assertEquals("1", fld.getTagVal());
            Assert.assertEquals(555, fld.getTagNum());
            fld = myList.get(4);
            Assert.assertEquals(10, fld.getTagNum());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t31() {
        try {
            sampleRule = "&555=1;~&555;&555=1;&555[0]->&600=42";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage7);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            // System.out.println(StaticTestingUtils.rs(result));
            InfixField fld = myList.get(3);
            Assert.assertEquals("1", fld.getTagVal());
            Assert.assertEquals(555, fld.getTagNum());
            fld = myList.get(4);
            Assert.assertEquals("42", fld.getTagVal());
            Assert.assertEquals(600, fld.getTagNum());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t32() {
        try {
            sampleRule = "&382=1;~&382;&382=1";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage7);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            // System.out.println(StaticTestingUtils.rs(result));
            InfixField fld = myList.get(3);
            Assert.assertEquals("1", fld.getTagVal());
            Assert.assertEquals(382, fld.getTagNum());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    static final String sampleMessage7 =
        "8=FIX.4.4" + '\u0001' + "9=10" + '\u0001' + "35=8" + '\u0001' + "10=004";

    @Test
    public void t33() {
        try {
            sampleRule =
                "&555=1;~&555;&555=1;&555[0]->&600=42;~&555[0]->&600;&555[0]->&600=42;~&555;&555=1;&555[0]->&600=42";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage7);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            // System.out.println(StaticTestingUtils.rs(result));
            InfixField fld = myList.get(3);
            Assert.assertEquals("1", fld.getTagVal());
            Assert.assertEquals(555, fld.getTagNum());
            fld = myList.get(4);
            Assert.assertEquals("42", fld.getTagVal());
            Assert.assertEquals(600, fld.getTagNum());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t34() {
        try {
            sampleRule = "&552[0]->&453[0]->&448=\"XYZ\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_NEW_ORDER_CROSS);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(448, "XYZ"));
            Assert.assertTrue(wasDone > 0);
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    // static final String sampleMessage6 = "8=FIX.4.4" + '\u0001' + "9=1000" +
    // '\u0001' + "35=8"
    // + '\u0001' + "555=2" + '\u0001' + "600=FOO" + '\u0001' + "601=2" +
    // '\u0001' + "539=1"
    // + '\u0001' + "524=STR" + '\u0001' + "525=8" + '\u0001' + "538=-33" +
    // '\u0001' + "600=FOO1"
    // + '\u0001' + "601=3" + '\u0001' + "602=4" + '\u0001' + "10=004";

    @Test
    public void t36() {
        try {
            sampleRule = "&73[0]->&78[0]->&539[0]->&538=\"XYZ\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_NEW_ORDER_LIST);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(538, "XYZ"));
            Assert.assertTrue(wasDone > 0);
            Assert.assertEquals(45, myList.size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t37() {
        try {
            sampleRule = "&73[1]->&11=\"XYZ\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_NEW_ORDER_LIST, true);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(25);
            System.out.println(StaticTestingUtils.rs(result));
            Assert.assertEquals("XYZ", fld.getTagVal());
            Assert.assertEquals(11, fld.getTagNum());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t38() {
        try {
            sampleRule = "&73[0]->&78[1]->&539[0]->&538=\"XYZ\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_NEW_ORDER_LIST);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(24);
            // System.out.println(StaticTestingUtils.rs(result));
            Assert.assertEquals("XYZ", fld.getTagVal());
            Assert.assertEquals(538, fld.getTagNum());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t39() {
        try {
            sampleRule = "~&73";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_NEW_ORDER_LIST);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(4);
            System.out.println(StaticTestingUtils.rs(result));
            Assert.assertEquals("1", fld.getTagVal());
            Assert.assertEquals(394, fld.getTagNum());
            Assert.assertEquals(6, myList.size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t41() {
        try {
            sampleRule = "~&73[0]->&78";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_NEW_ORDER_LIST);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(7);
            System.out.println(StaticTestingUtils.rs(result));
            Assert.assertEquals("1", fld.getTagVal());
            Assert.assertEquals(67, fld.getTagNum());
            fld = myList.get(8);
            Assert.assertEquals("ClOrdID2", fld.getTagVal());
            Assert.assertEquals(11, fld.getTagNum());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t42() {
        try {
            sampleRule = "~&73[1]->&78";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_NEW_ORDER_LIST);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(26);
            System.out.println(StaticTestingUtils.rs(result));
            Assert.assertEquals("2", fld.getTagVal());
            Assert.assertEquals(67, fld.getTagNum());
            Assert.assertEquals(28, myList.size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t45() {
        try {
            // nPartyID1 | ClOrdID2
            sampleRule =
                "&73[0]->&78[1]->&539[0]->&538 = &73[0]->&78[0]->&539[0]->&524 | &73[1]->&11";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_NEW_ORDER_LIST);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(24);
            System.out.println(StaticTestingUtils.rs(result));
            Assert.assertEquals("nPartyID1ClOrdID2", fld.getTagVal());
            Assert.assertEquals(538, fld.getTagNum());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    /*
     * static final String sampleMessage8 = "8=FIX.4.4" + '\u0001' + "9=1000" +
     * '\u0001' + "35=8" + '\u0001' + "43=-100" + '\u0001' + "44=37.0500" +
     * '\u0001' + "46=RefSym" + '\u0001' + "49=GSCO" + '\u0001' + "55=Symbol" +
     * '\u0001' + "115=COMPID" + '\u0001' + "116=SUBID" // 9 + '\u0001' +
     * "382=2" + '\u0001' + "375=FOO" + '\u0001' + "655=2" + '\u0001' +
     * "375=BAR" + '\u0001' + "655=3" + '\u0001' + "555=2" + '\u0001' +
     * "600=FOO" + '\u0001' + "601=2" + '\u0001' + "539=2" + '\u0001' +
     * "524=STR" + '\u0001' + "525=8" + '\u0001' + "538=-33" + '\u0001' +
     * "804=2" + '\u0001' + "545=NPSG15451" + '\u0001' // 23 + "805=NPSG18051" +
     * '\u0001' + "545=NPSG15452" + '\u0001' + "805=NPSG18052" + '\u0001' +
     * "524=MCS" + '\u0001' + "525=22" + '\u0001' + "538=33" + '\u0001' +
     * "804=1" + '\u0001' + "545=NPSG25451" + '\u0001' + "805=NPSG28051" +
     * '\u0001' + "600=FOO1" + '\u0001' // 33 + "601=3" + '\u0001' + "539=1" +
     * '\u0001' + "524=STR1" + '\u0001' // 36 + "525=0" + '\u0001' + "538=-34" +
     * '\u0001' + "207=USA" + '\u0001' + "10=004";
     */
    // name = [InstrmtLegExecGrp], id = [555], members = [600|601|602...]
    // --> name = [NestedParties], id = [539], members = [524|525|538]
    // --------> name = [NstdPtysSubGrp], id = [804], members = [545|805]
    // name = [ContraGrp], id = [382], members = [375|337|437|438|655]
    // name = [InstrmtLegExecGrp], id = [555], members = [600|601|602...]
    // --> name = [NestedParties], id = [539], members = [524|525|538]
    // --------> name = [NstdPtysSubGrp], id = [804], members = [545|805]
    // name = [ContraGrp], id = [382], members = [375|337|437|438|655]
    static final String sampleMsg9 =
        "8=FIX.4.4\u00019=1000\u000135=8\u000143=-100\u000144=37.0500\u000146=RefSym\u000149=GSCO\u000155=Symbol\u0001115=COMPID\u0001116=SUBID\u0001382=2\u0001375=FOO\u0001655=2\u0001375=BAR\u0001655=3\u0001555=2\u0001600=FOO\u0001601=2\u0001539=2\u0001524=STR\u0001525=8\u0001538=-33\u0001524=MCS\u0001525=22\u0001538=33\u0001804=2\u0001545=THEIR_ACCT1\u0001805=1\u0001545=THEIR_ACCT2\u0001805=1\u0001600=FOO1\u0001601=3\u0001539=1\u0001524=STR1\u0001525=0\u0001538=-34\u0001804=1\u0001545=THEIR_ACCT3\u0001805=2\u0001207=USA\u000110=004";
}
