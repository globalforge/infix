package com.globalforge.infix;

import java.util.ArrayList;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import com.globalforge.infix.api.InfixActions;
import com.globalforge.infix.api.InfixField;

public class TestGroup {
    static final String sampleMessage1 = "8=FIX.4.4 " + '\u0001' + "9=10"
        + '\u0001' + "35=8" + '\u0001' + "43=-1" + '\u0001' + "207=USA"
        + '\u0001' + "10=004";
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
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(2);
            Assert.assertEquals("D", fld.getTagVal());
            Assert.assertEquals(35, fld.getTagNum());
            Assert.assertEquals(6, myList.size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t2() {
        try {
            sampleRule = "&382=1;&375=1;&655=2;&218=4";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage1);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(5);
            Assert.assertEquals("1", fld.getTagVal());
            Assert.assertEquals(382, fld.getTagNum());
            fld = myList.get(6);
            Assert.assertEquals("1", fld.getTagVal());
            Assert.assertEquals(375, fld.getTagNum());
            fld = myList.get(7);
            Assert.assertEquals("2", fld.getTagVal());
            Assert.assertEquals(655, fld.getTagNum());
            fld = myList.get(8);
            Assert.assertEquals("4", fld.getTagVal());
            Assert.assertEquals(218, fld.getTagNum());
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
            sampleRule =
                "&382=1;&382[0]->&375=1;&382[0]->&655=2;&382[0]->&218=3";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage1);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(5);
            Assert.assertEquals("1", fld.getTagVal());
            Assert.assertEquals(382, fld.getTagNum());
            fld = myList.get(6);
            Assert.assertEquals("1", fld.getTagVal());
            Assert.assertEquals(375, fld.getTagNum());
            fld = myList.get(7);
            Assert.assertEquals("2", fld.getTagVal());
            Assert.assertEquals(655, fld.getTagNum());
            fld = myList.get(8);
            Assert.assertEquals("3", fld.getTagVal());
            Assert.assertEquals(218, fld.getTagNum());
            Assert.assertEquals(10, myList.size());
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
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
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
    static final String sampleMessage2 = "8=FIX.4.4" + '\u0001' + "9=10"
        + '\u0001' + "35=8" + '\u0001' + "43=-1" + '\u0001' + "382=1"
        + '\u0001' + " 375=FOO" + '\u0001' + "655=2" + '\u0001' + "207=USA"
        + '\u0001' + "10=004";

    @Test
    public void t5() {
        try {
            sampleRule =
                "&382[0]->&375=\"MIKE\";&382[0]->&655=99;&382[0]->&218=\"XYZ\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage2);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(5);
            Assert.assertEquals("MIKE", fld.getTagVal());
            Assert.assertEquals(375, fld.getTagNum());
            fld = myList.get(6);
            Assert.assertEquals("99", fld.getTagVal());
            Assert.assertEquals(655, fld.getTagNum());
            fld = myList.get(7);
            Assert.assertEquals("XYZ", fld.getTagVal());
            Assert.assertEquals(218, fld.getTagNum());
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

    @Test
    public void t6() {
        try {
            sampleRule =
                "&35=8;&382[0]->&375=\"MIKE\";&382[0]->&655=99;&382[0]->&218=\"XYZ\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage2);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(5);
            Assert.assertEquals("MIKE", fld.getTagVal());
            Assert.assertEquals(375, fld.getTagNum());
            fld = myList.get(6);
            Assert.assertEquals("99", fld.getTagVal());
            Assert.assertEquals(655, fld.getTagNum());
            fld = myList.get(7);
            Assert.assertEquals("XYZ", fld.getTagVal());
            Assert.assertEquals(218, fld.getTagNum());
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
    static final String sampleMessage3 = "8=FIX.4.4" + '\u0001' + "9=10"
        + '\u0001' + "35=8" + '\u0001' + "43=-1" + '\u0001' + "382=2"
        + '\u0001' + "375=FOO" + '\u0001' + "655=2" + '\u0001' + "375=BAR"
        + '\u0001' + " 655=3 " + '\u0001' + "207=USA" + '\u0001' + "10=004";

    @Test
    public void t7() {
        try {
            sampleRule =
                "&35=8;&382[0]->&218=\"MIKE\";&382[1]->&655=99;&382[1]->&218=\"XYZ\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage3);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(7);
            Assert.assertEquals("MIKE", fld.getTagVal());
            Assert.assertEquals(218, fld.getTagNum());
            fld = myList.get(9);
            Assert.assertEquals("99", fld.getTagVal());
            Assert.assertEquals(655, fld.getTagNum());
            fld = myList.get(10);
            Assert.assertEquals("XYZ", fld.getTagVal());
            Assert.assertEquals(218, fld.getTagNum());
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
    static final String sampleMessage4 = " 8=FIX.4.4 " + '\u0001' + "9=10"
        + '\u0001' + "35=8" + '\u0001' + "43=-1" + '\u0001' + "555=2"
        + '\u0001' + "600=FOO" + '\u0001' + "601=2" + '\u0001' + "539=2"
        + '\u0001' + "524=STR" + '\u0001' + " 525=8" + '\u0001' + "538=-33"
        + '\u0001' + "524=\"MCS\"" + '\u0001' + "525=22" + '\u0001' + "538=33"
        + '\u0001' + "600=FOO1" + '\u0001' + "601=3" + '\u0001' + "539=1"
        + '\u0001' + "524=\"STR1\"" + '\u0001' + "525=0" + '\u0001' + "538=-34"
        + '\u0001' + "207=USA" + '\u0001' + " 10=004 ";

    @Test
    public void t8() {
        try {
            sampleRule = "&555[0]->&539[0]->&524=\"STARK\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage4);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(8);
            Assert.assertEquals("STARK", fld.getTagVal());
            Assert.assertEquals(524, fld.getTagNum());
            Assert.assertEquals(22, myList.size());
            // System.out.println(TestResultsStore.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t9() {
        try {
            sampleRule = "&555[0]->&539[1]->&524=\"STARK\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage4);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(11);
            Assert.assertEquals("STARK", fld.getTagVal());
            Assert.assertEquals(524, fld.getTagNum());
            // System.out.println(TestResultsStore.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t10() {
        try {
            sampleRule = "&555[0]->&218=\"STARK\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage4);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(7);
            Assert.assertEquals("STARK", fld.getTagVal());
            Assert.assertEquals(218, fld.getTagNum());
            Assert.assertEquals(23, myList.size());
            // System.out.println(TestResultsStore.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t11() {
        try {
            sampleRule = "&555[1]->&218=\"STARK\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage4);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(16);
            Assert.assertEquals("STARK", fld.getTagVal());
            Assert.assertEquals(218, fld.getTagNum());
            Assert.assertEquals(23, myList.size());
            // System.out.println(TestResultsStore.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t12() {
        try {
            sampleRule = "&555[0]->&218=\"STARK\";&555[1]->&218=\"STARK\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage4);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(7);
            Assert.assertEquals("STARK", fld.getTagVal());
            Assert.assertEquals(218, fld.getTagNum());
            fld = myList.get(17);
            Assert.assertEquals("STARK", fld.getTagVal());
            Assert.assertEquals(218, fld.getTagNum());
            Assert.assertEquals(24, myList.size());
            // System.out.println(TestResultsStore.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t13() {
        try {
            sampleRule =
                "&555[0]->&539[0]->&366=998;&555[0]->&539[1]->&79=\"WAS\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage4);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(11);
            Assert.assertEquals("998", fld.getTagVal());
            Assert.assertEquals(366, fld.getTagNum());
            fld = myList.get(15);
            Assert.assertEquals("WAS", fld.getTagVal());
            Assert.assertEquals(79, fld.getTagNum());
            Assert.assertEquals(24, myList.size());
            // System.out.println(TestResultsStore.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t15() {
        try {
            sampleRule = "~&555[0]->&539[0]->&525";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage4);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(9);
            Assert.assertEquals("-33", fld.getTagVal());
            Assert.assertEquals(538, fld.getTagNum());
            Assert.assertEquals(21, myList.size());
            // System.out.println(TestResultsStore.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t16() {
        try {
            sampleRule = "~&555[0]->&539[1]->&525";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage4);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(12);
            Assert.assertEquals("33", fld.getTagVal());
            Assert.assertEquals(538, fld.getTagNum());
            Assert.assertEquals(21, myList.size());
            // System.out.println(TestResultsStore.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t17() {
        try {
            sampleRule =
                "~&555[0]->&539[1]->&538;&555[0]->&539[1]->&538=999.91";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage4);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(13);
            Assert.assertEquals("999.91", fld.getTagVal());
            Assert.assertEquals(538, fld.getTagNum());
            Assert.assertEquals(22, myList.size());
            // System.out.println(TestResultsStore.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t18() {
        try {
            sampleRule = "~&555[0]->&539[1]->&538;&555[0]->&539[1]->&538=&43";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage4);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(13);
            Assert.assertEquals("-1", fld.getTagVal());
            Assert.assertEquals(538, fld.getTagNum());
            Assert.assertEquals(22, myList.size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t19() {
        try {
            sampleRule =
                "&555[0]->&539=4;" + "&555[0]->&539[2]->&524=111;"
                    + "&555[0]->&539[2]->&525=222;"
                    + "&555[0]->&539[3]->&524=333;"
                    + "&555[0]->&539[3]->&525=444";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage4);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(7);
            Assert.assertEquals("4", fld.getTagVal());
            Assert.assertEquals(539, fld.getTagNum());
            fld = myList.get(14);
            Assert.assertEquals("111", fld.getTagVal());
            Assert.assertEquals(524, fld.getTagNum());
            fld = myList.get(15);
            Assert.assertEquals("222", fld.getTagVal());
            Assert.assertEquals(525, fld.getTagNum());
            fld = myList.get(16);
            Assert.assertEquals("333", fld.getTagVal());
            Assert.assertEquals(524, fld.getTagNum());
            fld = myList.get(17);
            Assert.assertEquals("444", fld.getTagVal());
            Assert.assertEquals(525, fld.getTagNum());
            fld = myList.get(8);
            Assert.assertEquals("STR", fld.getTagVal());
            Assert.assertEquals(524, fld.getTagNum());
            Assert.assertEquals(26, myList.size());
            // System.out.println(TestResultsStore.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t25() {
        try {
            sampleRule =
                "~&555[0]->&539;" + "~&555[0]->&539[0]->&524;"
                    + "~&555[0]->&539[0]->&525;" + "~&555[0]->&539[0]->&538;"
                    + "&555[0]->&539=1;" + "&555[0]->&539[0]->&524=\"FOO\";"
                    + "&555[0]->&539[0]->&525=12;"
                    + "&555[0]->&539[0]->&538=13;";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage6);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(6);
            Assert.assertEquals("1", fld.getTagVal());
            Assert.assertEquals(539, fld.getTagNum());
            fld = myList.get(7);
            Assert.assertEquals("FOO", fld.getTagVal());
            Assert.assertEquals(524, fld.getTagNum());
            fld = myList.get(8);
            Assert.assertEquals("12", fld.getTagVal());
            Assert.assertEquals(525, fld.getTagNum());
            fld = myList.get(9);
            Assert.assertEquals("13", fld.getTagVal());
            Assert.assertEquals(538, fld.getTagNum());
            Assert.assertEquals(14, myList.size());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t27() {
        try {
            sampleRule =
                "~&555[0]->&539;" + "~&555[0]->&539[0]->&524;"
                    + "~&555[0]->&539[0]->&525;" + "~&555[0]->&539[0]->&538;"
                    + "&555[1]->&539=1;" + "&555[1]->&539[0]->&524=\"FOO\";"
                    + "&555[1]->&539[0]->&525=12;"
                    + "&555[1]->&539[0]->&538=13;";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage6);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(9);
            Assert.assertEquals("1", fld.getTagVal());
            Assert.assertEquals(539, fld.getTagNum());
            fld = myList.get(10);
            Assert.assertEquals("FOO", fld.getTagVal());
            Assert.assertEquals(524, fld.getTagNum());
            fld = myList.get(11);
            Assert.assertEquals("12", fld.getTagVal());
            Assert.assertEquals(525, fld.getTagNum());
            fld = myList.get(12);
            Assert.assertEquals("13", fld.getTagVal());
            Assert.assertEquals(538, fld.getTagNum());
            Assert.assertEquals(14, myList.size());
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
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
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
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
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
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
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
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
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
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
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
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            // System.out.println(StaticTestingUtils.rs(result));
            InfixField fld = myList.get(3);
            Assert.assertEquals("1", fld.getTagVal());
            Assert.assertEquals(382, fld.getTagNum());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    static final String sampleMessage7 = "8=FIX.4.4" + '\u0001' + "9=10"
        + '\u0001' + "35=8" + '\u0001' + "10=004";

    @Test
    public void t33() {
        try {
            sampleRule =
                "&555=1;~&555;&555=1;&555[0]->&600=42;~&555[0]->&600;&555[0]->&600=42;~&555;&555=1;&555[0]->&600=42";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage7);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
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
            sampleRule = "&555[0]->&539[0]->&524=\"XYZ\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage6);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(7);
            Assert.assertEquals("XYZ", fld.getTagVal());
            Assert.assertEquals(524, fld.getTagNum());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    static final String sampleMessage6 = "8=FIX.4.4" + '\u0001' + "9=10"
        + '\u0001' + "35=8" + '\u0001' + "555=2" + '\u0001' + "600=FOO"
        + '\u0001' + "601=2" + '\u0001' + "539=1" + '\u0001' + "524=STR"
        + '\u0001' + "525=8" + '\u0001' + "538=-33" + '\u0001' + "600=FOO1"
        + '\u0001' + "601=3" + '\u0001' + "602=4" + '\u0001' + "10=004";

    @Test
    public void t35() {
        try {
            sampleRule = "&555[0]->&539[0]->&524=\"XYZ\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage8);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(19);
            Assert.assertEquals("XYZ", fld.getTagVal());
            Assert.assertEquals(524, fld.getTagNum());
            System.out.println(StaticTestingUtils.rs(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t36() {
        try {
            sampleRule = "&555[0]->&539[0]->&804[0]->&545=\"XYZ\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage8);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(23);
            // System.out.println(StaticTestingUtils.rs(result));
            Assert.assertEquals("XYZ", fld.getTagVal());
            Assert.assertEquals(545, fld.getTagNum());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t37() {
        try {
            sampleRule = "&382[1]->&655=\"XYZ\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage8, true);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(14);
            System.out.println(StaticTestingUtils.rs(result));
            Assert.assertEquals("XYZ", fld.getTagVal());
            Assert.assertEquals(655, fld.getTagNum());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t38() {
        try {
            sampleRule = "&555[0]->&539[1]->&804[0]->&805=\"XYZ\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage8);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(32);
            // System.out.println(StaticTestingUtils.rs(result));
            Assert.assertEquals("XYZ", fld.getTagVal());
            Assert.assertEquals(805, fld.getTagNum());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t39() {
        try {
            sampleRule = "~&555";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage8);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(15);
            // System.out.println(StaticTestingUtils.rs(result));
            Assert.assertEquals("USA", fld.getTagVal());
            Assert.assertEquals(207, fld.getTagNum());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t40() {
        try {
            sampleRule = "~&382";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage8);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(10);
            System.out.println(StaticTestingUtils.rs(result));
            Assert.assertEquals("2", fld.getTagVal());
            Assert.assertEquals(555, fld.getTagNum());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t41() {
        try {
            sampleRule = "~&555[0]->&539";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage8);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(18);
            System.out.println(StaticTestingUtils.rs(result));
            Assert.assertEquals("FOO1", fld.getTagVal());
            Assert.assertEquals(600, fld.getTagNum());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t42() {
        try {
            sampleRule = "~&555[1]->&539";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage8);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(35);
            System.out.println(StaticTestingUtils.rs(result));
            Assert.assertEquals("USA", fld.getTagVal());
            Assert.assertEquals(207, fld.getTagNum());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t45() {
        try {
            // NPSG15451 | 3
            sampleRule =
                "&555[0]->&539[1]->&804[0]->&805 = &555[0]->&539[0]->&804[0]->&545 | &382[1]->&655";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage8);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(32);
            System.out.println(StaticTestingUtils.rs(result));
            Assert.assertEquals("NPSG154513", fld.getTagVal());
            Assert.assertEquals(805, fld.getTagNum());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    static final String sampleMessage8 = "8=FIX.4.4" + '\u0001' + "9=10"
        + '\u0001' + "35=8" + '\u0001' + "43=-100" + '\u0001' + "44=37.0500"
        + '\u0001'
        + "46=RefSym"
        + '\u0001'
        + "49=GSCO"
        + '\u0001'
        + "55=Symbol"
        + '\u0001'
        + "115=COMPID"
        + '\u0001'
        + "116=SUBID"   // 9
        + '\u0001' + "382=2" + '\u0001' + "375=FOO" + '\u0001' + "655=2"
        + '\u0001' + "375=BAR" + '\u0001' + "655=3" + '\u0001' + "555=2"
        + '\u0001' + "600=FOO" + '\u0001' + "601=2" + '\u0001' + "539=2"
        + '\u0001' + "524=STR" + '\u0001' + "525=8" + '\u0001' + "538=-33"
        + '\u0001'
        + "804=2"
        + '\u0001'
        + "545=NPSG15451"
        + '\u0001' // 23
        + "805=NPSG18051" + '\u0001' + "545=NPSG15452" + '\u0001'
        + "805=NPSG18052" + '\u0001' + "524=MCS" + '\u0001' + "525=22"
        + '\u0001' + "538=33" + '\u0001' + "804=1" + '\u0001' + "545=NPSG25451"
        + '\u0001' + "805=NPSG28051" + '\u0001' + "600=FOO1"
        + '\u0001' // 33
        + "601=3" + '\u0001' + "539=1" + '\u0001' + "524=STR1"
        + '\u0001' // 36
        + "525=0" + '\u0001' + "538=-34" + '\u0001' + "207=USA" + '\u0001'
        + "10=004";

    // name = [InstrmtLegExecGrp], id = [555], members = [600|601|602...]
    // --> name = [NestedParties], id = [539], members = [524|525|538]
    // --------> name = [NstdPtysSubGrp], id = [804], members = [545|805]
    // name = [ContraGrp], id = [382], members = [375|337|437|438|655]
    @Test
    public void t46() {
        try {
            // NPSG15451 | 3
            sampleRule = "&555[0]->&539[0]->&804[0]->&545=\"MY_ACCT1\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestGroup.sampleMessage8);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(23);
            System.out.println(StaticTestingUtils.rs(TestGroup.sampleMessage8));
            System.out.println(StaticTestingUtils.rs(result));
            Assert.assertEquals("MY_ACCT1", fld.getTagVal());
            Assert.assertEquals(545, fld.getTagNum());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    // name = [InstrmtLegExecGrp], id = [555], members = [600|601|602...]
    // --> name = [NestedParties], id = [539], members = [524|525|538]
    // --------> name = [NstdPtysSubGrp], id = [804], members = [545|805]
    // name = [ContraGrp], id = [382], members = [375|337|437|438|655]
    static final String sampleMsg9 =
        "8=FIX.4.4\u00019=10\u000135=8\u000143=-100\u000144=37.0500\u000146=RefSym\u000149=GSCO\u000155=Symbol\u0001115=COMPID\u0001116=SUBID\u0001382=2\u0001375=FOO\u0001655=2\u0001375=BAR\u0001655=3\u0001555=2\u0001600=FOO\u0001601=2\u0001539=2\u0001524=STR\u0001525=8\u0001538=-33\u0001524=MCS\u0001525=22\u0001538=33\u0001804=2\u0001545=THEIR_ACCT1\u0001805=1\u0001545=THEIR_ACCT2\u0001805=1\u0001600=FOO1\u0001601=3\u0001539=1\u0001524=STR1\u0001525=0\u0001538=-34\u0001804=1\u0001545=THEIR_ACCT3\u0001805=2\u0001207=USA\u000110=004";

    @Test
    public void t47() {
        try {
            // NPSG15451 | 3
            sampleRule = "&555[0]->&539[1]->&804[0]->&545=\"MY_ACCT1\"";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(sampleMsg9, true);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(25);
            System.out.println(StaticTestingUtils.rs(TestGroup.sampleMessage8));
            System.out.println(StaticTestingUtils.rs(result));
            Assert.assertEquals("2", fld.getTagVal());
            Assert.assertEquals(804, fld.getTagNum());
            fld = myList.get(26);
            Assert.assertEquals("MY_ACCT1", fld.getTagVal());
            Assert.assertEquals(545, fld.getTagNum());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
