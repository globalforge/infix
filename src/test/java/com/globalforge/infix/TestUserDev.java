package com.globalforge.infix;

import static org.junit.Assert.fail;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import com.globalforge.infix.api.InfixAPI;
import com.globalforge.infix.api.InfixActions;
import com.globalforge.infix.api.InfixField;
import com.globalforge.infix.api.InfixUserContext;
import com.globalforge.infix.api.InfixUserTerminal;

public class TestUserDev {
    @Test
    public void t1() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx1}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage1);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("199", fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t2() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UserCtx1#visitInfixAPI}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage1);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("199", fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t3() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UserCtx1#visitMessage}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage1);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("99.0100", fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t4() {
        try {
            String sampleRule =
                "&44=\"FOOBAR\";{com.globalforge.infix.TestUserDev$UserCtx1}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage1);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("199", fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t5() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UserCtx1};&44=\"FOOBAR\"";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage1);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("FOOBAR", fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t6() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx2}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage1);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(5);
            Assert.assertEquals("BAR", fld.getTagVal());
            fld = myList.get(11);
            Assert.assertEquals("BAR1", fld.getTagVal());
            fld = myList.get(10);
            Assert.assertEquals("525", fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t7() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx3}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage1);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(7);
            Assert.assertEquals("2", fld.getTagVal());
            fld = myList.get(10);
            Assert.assertEquals("524", fld.getTagVal());
            fld = myList.get(11);
            Assert.assertEquals("525", fld.getTagVal());
            fld = myList.get(12);
            Assert.assertEquals("538", fld.getTagVal());
            fld = myList.get(13);
            Assert.assertEquals("FOO1", fld.getTagVal());
            fld = myList.get(16);
            Assert.assertEquals("999", fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t8() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx4}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage1);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(12);
            Assert.assertEquals("199", fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t9() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx5}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage1);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(9);
            Assert.assertEquals("MCS", fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t10() {
        try {
            String sampleRule =
                "&44={com.globalforge.infix.example.ExampleUserAssignment}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage1);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals(Double.toString(Math.PI), fld.getTagVal());
            // again
            result = rules.transformFIXMsg(sampleMessage1);
            myList = StaticTestingUtils.parseMessageIntoList(result);
            fld = myList.get(3);
            Assert.assertEquals(Double.toString(Math.PI), fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void $t10() {
        try {
            String sampleRule =
                "&44 = &44 - {com.globalforge.infix.example.ExampleUserNumberAssignment}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage1);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("-43", fld.getTagVal());
            // again
            result = rules.transformFIXMsg(sampleMessage1);
            myList = StaticTestingUtils.parseMessageIntoList(result);
            fld = myList.get(3);
            Assert.assertEquals("-43", fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t11() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx5}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage1);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(9);
            Assert.assertEquals("MCS", fld.getTagVal());
            // again
            result = rules.transformFIXMsg(sampleMessage1);
            myList = StaticTestingUtils.parseMessageIntoList(result);
            fld = myList.get(9);
            Assert.assertEquals("MCS", fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t12() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx6}";
            InfixActions rules = new InfixActions(sampleRule);
            @SuppressWarnings("unused")
            String result = rules.transformFIXMsg(sampleMessage1);
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t13() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx7}";
            InfixActions rules = new InfixActions(sampleRule);
            rules.transformFIXMsg(sampleMessage1);
            fail();
        } catch (Throwable t) {
        }
    }

    @Test
    public void t14() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx8}";
            InfixActions rules = new InfixActions(sampleRule);
            rules.transformFIXMsg(sampleMessage1);
            fail();
        } catch (Throwable t) {
        }
    }

    @Test
    public void t15() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx9}";
            InfixActions rules = new InfixActions(sampleRule);
            rules.transformFIXMsg(sampleMessage1);
            fail();
        } catch (Throwable t) {
        }
    }

    @Test
    public void t16() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx10}";
            InfixActions rules = new InfixActions(sampleRule);
            rules.transformFIXMsg(sampleMessage1);
            fail();
        } catch (Throwable t) {
        }
    }

    @Test
    public void t17() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx11}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage1);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(7);
            Assert.assertEquals("2", fld.getTagVal());
            fld = myList.get(10);
            Assert.assertEquals("524", fld.getTagVal());
            fld = myList.get(11);
            Assert.assertEquals("525", fld.getTagVal());
            fld = myList.get(12);
            Assert.assertEquals("538", fld.getTagVal());
            fld = myList.get(13);
            Assert.assertEquals("FOO1", fld.getTagVal());
            fld = myList.get(16);
            Assert.assertEquals("999", fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }
    // name = [InstrmtLegExecGrp], id = [555], members = [555|600|601|602
    // name = [NestedParties], id = [539], members = [539|524|525|538],
    // 382->555, 78->539, 79->524
    // size: 22
    static final String sampleMessage1 = "8=FIX.4.4" + '\u0001' + "9=10"
        + '\u0001' + "35=8" + '\u0001' + "44=-1" + '\u0001' + "555=2"
        + '\u0001' + "600=FOO" + '\u0001' + "601=2 " + '\u0001' + "539=1"
        + '\u0001' + "524=STR" + '\u0001' + "538=-33" + '\u0001' + "600=FOO1"
        + '\u0001' + "601=3" + '\u0001' + "602=4" + '\u0001' + "10=004"
        + '\u0001';

    @Test
    public void t18() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UTerm1} == {com.globalforge.infix.TestUserDev$UTerm1} ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage1);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("1", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t19() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UTerm1} == &45 ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage1);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("0", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t20() {
        try {
            String sampleRule =
                "&45 == {com.globalforge.infix.TestUserDev$UTerm1} ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage1);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("0", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t21() {
        try {
            String sampleRule =
                "&44 == {com.globalforge.infix.TestUserDev$UTerm1} ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage1);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("0", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }
    static final String sampleMessage2 = "8=FIX.4.4" + '\u0001' + "9=10"
        + '\u0001' + "35=8" + '\u0001' + "44=-1" + '\u0001' + "555=2"
        + '\u0001' + "600=FOO" + '\u0001' + "601=2" + '\u0001' + "539=1"
        + '\u0001' + "524=STR" + '\u0001' + "538=-33" + '\u0001' + "600=FOO1"
        + '\u0001' + "601=3" + '\u0001' + "602=4" + '\u0001' + "207=42"
        + '\u0001' + "10=004";

    @Test
    public void t22() {
        try {
            String sampleRule =
                "&207 == {com.globalforge.infix.TestUserDev$UTerm1} ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage2);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("1", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t23() {
        try {
            String sampleRule =
                "&207 != {com.globalforge.infix.TestUserDev$UTerm1} ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage2);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("0", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t24() {
        try {
            String sampleRule =
                "&45 != {com.globalforge.infix.TestUserDev$UTerm1} ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage2);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("0", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t25() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UTerm1} != &45 ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage2);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("0", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t26() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UTerm1} != {com.globalforge.infix.TestUserDev$UTerm1} ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage2);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("0", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t27() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UTerm1} != &207 ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage2);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("0", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t28() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UTerm1} <= &207 ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage2);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("1", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t29() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UTerm1} >= &207 ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage2);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("1", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t30() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UTerm1} < &207 ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage2);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("0", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t31() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UTerm1} > &207 ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage2);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("0", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t32() {
        try {
            String sampleRule =
                "!{com.globalforge.infix.TestUserDev$UTerm1} ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage2);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("0", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t33() {
        try {
            String sampleRule =
                "^{com.globalforge.infix.TestUserDev$UTerm1} ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage2);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("1", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void t34() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UTerm1} != {com.globalforge.infix.TestUserDev$UTerm1} ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(sampleMessage2);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("0", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    public static class UTerm1 implements InfixUserTerminal {
        @Override
        public String visitTerminal(InfixAPI infixApi) {
            return 42 + "";
        }
    }

    public static class UserCtx1 implements InfixUserContext {
        @Override
        public String visitMessage(String fixMessage) {
            return calculatePrice(fixMessage);
        }

        private String calculatePrice(String baseMsg) {
            String[] msgArray =
                Pattern.compile(Character.toString((char) 0x01),
                    Pattern.LITERAL).split(baseMsg);
            StringBuilder newMsg = new StringBuilder();
            for (String field : msgArray) {
                int tagNum =
                    Integer.parseInt(field.substring(0, field.indexOf("=")));
                if (tagNum == 44) {
                    newMsg.append("44=99.0100");
                } else {
                    newMsg.append(field);
                }
                newMsg.append((char) 0x01);
            }
            return newMsg.toString();
        }

        @Override
        public void visitInfixAPI(InfixAPI infixApi) {
            infixApi.putContext("&44", "199");
        }
    }

    public static class UserCtx2 implements InfixUserContext {
        @Override
        public String visitMessage(String fixMessage) {
            return fixMessage;
        }

        @Override
        public void visitInfixAPI(InfixAPI infixApi) {
            infixApi.putContext("&555[0]->&600", "BAR");
            infixApi.putContext("&555[1]->&600", "BAR1");
            infixApi.putContext("&555[0]->&539[0]->&525", "525");
        }
    }

    public static class UserCtx3 implements InfixUserContext {
        @Override
        public String visitMessage(String fixMessage) {
            return fixMessage;
        }

        // name = [NestedParties], id = [539], members = [539|524|525|538],
        @Override
        public void visitInfixAPI(InfixAPI infixApi) {
            infixApi.putContext("&555[0]->&539", "2");
            infixApi.putContext("&555[0]->&539[1]->&524", "524");
            infixApi.putContext("&555[0]->&539[1]->&525", "525");
            infixApi.putContext("&555[0]->&539[1]->&538", "538");
            infixApi.putContext("&999", "999");
        }
    }

    public static class UserCtx4 implements InfixUserContext {
        @Override
        public String visitMessage(String fixMessage) {
            return fixMessage;
        }

        // name = [NestedParties], id = [539], members = [539|524|525|538],
        @Override
        public void visitInfixAPI(InfixAPI infixApi) {
            infixApi.removeContext("&44");
            infixApi.putContext("&44", "199");
        }
    }

    public static class UserCtx5 implements InfixUserContext {
        @Override
        public String visitMessage(String fixMessage) {
            return fixMessage;
        }

        // name = [NestedParties], id = [539], members = [539|524|525|538],
        @Override
        public void visitInfixAPI(InfixAPI infixApi) {
            infixApi.removeContext("&555[0]->&539[0]->&524");
            infixApi.putContext("&555[0]->&539[0]->&524", "MCS");
            InfixField fld = infixApi.getContext("&44");
            System.out.println(fld);
            Map<String, BigDecimal> ctxDict = infixApi.getCtxToOrderDict();
            Iterator<Entry<String, BigDecimal>> it =
                ctxDict.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, BigDecimal> e = it.next();
                System.out.println("key=" + e.getKey() + ", val="
                    + e.getValue());
            }
            Map<BigDecimal, InfixField> fldDict =
                infixApi.getOrderToFieldDict();
            Iterator<Entry<BigDecimal, InfixField>> it2 =
                fldDict.entrySet().iterator();
            while (it2.hasNext()) {
                Entry<BigDecimal, InfixField> e = it2.next();
                System.out.println("key=" + e.getKey() + ", val="
                    + e.getValue());
            }
            // fldDict.put(new BigDecimal(1), new FixField(55, "FOO"));
        }
    }

    public static class UserCtx6 implements InfixUserContext {
        @Override
        public String visitMessage(String fixMessage) {
            return fixMessage;
        }

        // name = [NestedParties], id = [539], members = [539|524|525|538],
        @Override
        public void visitInfixAPI(InfixAPI infixApi) {
            Map<String, InfixField> idxDict = infixApi.getCtxToFieldDict();
            Iterator<Entry<String, InfixField>> it2 =
                idxDict.entrySet().iterator();
            while (it2.hasNext()) {
                Entry<String, InfixField> e = it2.next();
                System.out.println("key=" + e.getKey() + ", val=["
                    + e.getValue() + "]");
            }
            InfixField tag539 = idxDict.get("&555");
            Map<String, BigDecimal> ctxDict = infixApi.getCtxToOrderDict();
            BigDecimal val = ctxDict.get("&555");
            Map<BigDecimal, InfixField> fldDict =
                infixApi.getOrderToFieldDict();
            InfixField fld = fldDict.get(val);
            Assert.assertEquals(tag539.getTagVal(), fld.getTagVal());
            Assert.assertEquals(tag539.getTagNum(), fld.getTagNum());
            InfixField tag = idxDict.get("&555[0]->&539[0]->&538");
            val = ctxDict.get("&555[0]->&539[0]->&538");
            fld = fldDict.get(val);
            Assert.assertEquals(tag.getTagVal(), fld.getTagVal());
            Assert.assertEquals(tag.getTagNum(), fld.getTagNum());
        }
    }

    public static class UserCtx7 implements InfixUserContext {
        @Override
        public String visitMessage(String fixMessage) {
            return fixMessage;
        }

        @Override
        public void visitInfixAPI(InfixAPI infixApi) {
            infixApi.putContext("&8", "FIX.4.2");
        }
    }

    public static class UserCtx8 implements InfixUserContext {
        @Override
        public String visitMessage(String fixMessage) {
            return fixMessage;
        }

        @Override
        public void visitInfixAPI(InfixAPI infixApi) {
            infixApi.putContext("&35", "D");
        }
    }

    public static class UserCtx9 implements InfixUserContext {
        @Override
        public String visitMessage(String fixMessage) {
            return fixMessage;
        }

        @Override
        public void visitInfixAPI(InfixAPI infixApi) {
            Map<String, String> myMap = new HashMap<String, String>();
            myMap.put("&8", "FIX.4.2");
            infixApi.putMessageDict(myMap);
        }
    }

    public static class UserCtx10 implements InfixUserContext {
        @Override
        public String visitMessage(String fixMessage) {
            return fixMessage;
        }

        @Override
        public void visitInfixAPI(InfixAPI infixApi) {
            Map<String, String> myMap = new HashMap<String, String>();
            myMap.put("&35", "D");
            infixApi.putMessageDict(myMap);
        }
    }

    public static class UserCtx11 implements InfixUserContext {
        @Override
        public String visitMessage(String fixMessage) {
            return fixMessage;
        }

        // name = [NestedParties], id = [539], members = [539|524|525|538],
        @Override
        public void visitInfixAPI(InfixAPI infixApi) {
            Map<String, String> myMap = new HashMap<String, String>();
            myMap.put("&555[0]->&539", "2");
            myMap.put("&555[0]->&539[1]->&524", "524");
            myMap.put("&555[0]->&539[1]->&525", "525");
            myMap.put("&555[0]->&539[1]->&538", "538");
            myMap.put("&999", "999");
            infixApi.putMessageDict(myMap);
        }
    }
}
