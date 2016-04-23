package com.globalforge.infix;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import com.globalforge.infix.api.InfixAPI;
import com.globalforge.infix.api.InfixActions;
import com.globalforge.infix.api.InfixField;
import com.globalforge.infix.api.InfixFieldInfo;
import com.globalforge.infix.api.InfixUserContext;
import com.globalforge.infix.api.InfixUserTerminal;

public class TestUserDev {
    public static class UserCtx1 implements InfixUserContext {
        @Override
        public String visitMessage(String fixMessage) {
            return calculatePrice(fixMessage);
        }

        private String calculatePrice(String baseMsg) {
            String[] msgArray =
                Pattern.compile(Character.toString((char) 0x01), Pattern.LITERAL).split(baseMsg);
            StringBuilder newMsg = new StringBuilder();
            for (String field : msgArray) {
                int tagNum = Integer.parseInt(field.substring(0, field.indexOf("=")));
                if (tagNum == 14) {
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
            infixApi.putContext("&14", "199");
        }
    }

    @Test
    public void t1() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx1}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(9);
            Assert.assertEquals("199", fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t2() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx1#visitInfixAPI}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(9);
            Assert.assertEquals("199", fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t3() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx1#visitMessage}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(9);
            Assert.assertEquals("99.0100", fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t4() {
        try {
            String sampleRule = "&14=\"FOOBAR\";{com.globalforge.infix.TestUserDev$UserCtx1}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(9);
            Assert.assertEquals("199", fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t5() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx1};&14=\"FOOBAR\"";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(9);
            Assert.assertEquals("FOOBAR", fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t6() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx2}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(26);
            Assert.assertEquals("BAR", fld.getTagVal());
            fld = myList.get(31);
            Assert.assertEquals("BAR1", fld.getTagVal());
            fld = myList.get(30);
            Assert.assertEquals("525", fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
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

    @Test
    public void t7() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx3}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(28);
            Assert.assertEquals("2", fld.getTagVal());
            fld = myList.get(31);
            Assert.assertEquals("524", fld.getTagVal());
            fld = myList.get(32);
            Assert.assertEquals("525", fld.getTagVal());
            fld = myList.get(33);
            Assert.assertEquals("538", fld.getTagVal());
            fld = myList.get(34);
            Assert.assertEquals("FOO1", fld.getTagVal());
            fld = myList.get(37);
            Assert.assertEquals("999", fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
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
            infixApi.removeContext("&14");
            infixApi.putContext("&14", "199");
        }
    }

    @Test
    public void t8() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx4}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(33);
            Assert.assertEquals("199", fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t9() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx5}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(29);
            Assert.assertEquals("MCS", fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t10() {
        try {
            String sampleRule = "&14={com.globalforge.infix.example.ExampleUserAssignment}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(9);
            Assert.assertEquals(Double.toString(Math.PI), fld.getTagVal());
            // again
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            myList = StaticTestingUtils.parseMessageIntoList(result);
            fld = myList.get(9);
            Assert.assertEquals(Double.toString(Math.PI), fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void $t10() {
        try {
            String sampleRule =
                "&14 = &14 - {com.globalforge.infix.example.ExampleUserNumberAssignment}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(9);
            Assert.assertEquals("-42", fld.getTagVal());
            // again
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            myList = StaticTestingUtils.parseMessageIntoList(result);
            fld = myList.get(9);
            Assert.assertEquals("-42", fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
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
            InfixFieldInfo fld = infixApi.getContext("&44");
            System.out.println(fld);
        }
    }

    @Test
    public void t11() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx5}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(29);
            Assert.assertEquals("MCS", fld.getTagVal());
            // again
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            myList = StaticTestingUtils.parseMessageIntoList(result);
            fld = myList.get(29);
            Assert.assertEquals("MCS", fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
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
            Map<String, InfixFieldInfo> idxDict = infixApi.getMessageDict();
            InfixFieldInfo tag539 = idxDict.get("&555");
            BigDecimal val = tag539.getPosition();
            InfixField fld = tag539.getField();
            Assert.assertEquals(fld.getTagVal(), fld.getTagVal());
            Assert.assertEquals(fld.getTagNum(), fld.getTagNum());
            InfixFieldInfo tag = idxDict.get("&555[0]->&539[0]->&538");
            Assert.assertEquals(fld.getTagVal(), fld.getTagVal());
            Assert.assertEquals(fld.getTagNum(), fld.getTagNum());
        }
    }

    @Test
    public void t12() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx6}";
            InfixActions rules = new InfixActions(sampleRule);
            @SuppressWarnings("unused")
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
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

    @Test(expected = RuntimeException.class)
    public void t13() throws UnsupportedEncodingException, IOException {
        String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx7}";
        InfixActions rules = new InfixActions(sampleRule);
        rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
        Assert.fail();
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

    @Test(expected = RuntimeException.class)
    public void t14() throws UnsupportedEncodingException, IOException {
        String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx8}";
        InfixActions rules = new InfixActions(sampleRule);
        rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
        Assert.fail();
    }

    public static class UserCtx9 implements InfixUserContext {
        @Override
        public String visitMessage(String fixMessage) {
            return fixMessage;
        }

        @Override
        public void visitInfixAPI(InfixAPI infixApi) {
            LinkedHashMap<String, String> myMap = new LinkedHashMap<String, String>();
            myMap.put("&8", "FIX.4.2");
            infixApi.putMessageDict(myMap);
        }
    }

    @Test
    public void t15() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx9}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(0);
            Assert.assertEquals("FIX.4.2", fld.getTagVal());
        } catch (Throwable t) {
            Assert.fail();
        }
    }

    public static class UserCtx10 implements InfixUserContext {
        @Override
        public String visitMessage(String fixMessage) {
            return fixMessage;
        }

        @Override
        public void visitInfixAPI(InfixAPI infixApi) {
            LinkedHashMap<String, String> myMap = new LinkedHashMap<String, String>();
            myMap.put("&35", "D");
            infixApi.putMessageDict(myMap);
        }
    }

    @Test
    public void t16() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx10}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(0);
            Assert.assertEquals("D", fld.getTagVal());
            Assert.fail();
        } catch (Throwable t) {
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
            LinkedHashMap<String, String> myMap = new LinkedHashMap<String, String>();
            myMap.put("&555[0]->&539", "2");
            myMap.put("&555[0]->&539[1]->&524", "524");
            myMap.put("&555[0]->&539[1]->&525", "525");
            myMap.put("&555[0]->&539[1]->&538", "538");
            myMap.put("&999", "999");
            infixApi.putMessageDict(myMap);
        }
    }

    @Test
    public void t17() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx11}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            System.out.println(StaticTestingUtils.rs(result));
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(28);
            Assert.assertEquals("2", fld.getTagVal());
            fld = myList.get(31);
            Assert.assertEquals("524", fld.getTagVal());
            fld = myList.get(32);
            Assert.assertEquals("525", fld.getTagVal());
            fld = myList.get(33);
            Assert.assertEquals("538", fld.getTagVal());
            fld = myList.get(34);
            Assert.assertEquals("FOO1", fld.getTagVal());
            fld = myList.get(37);
            Assert.assertEquals("999", fld.getTagVal());
            // System.out.println(StaticTestingUtils.rs(result));
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t18() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UTerm1} == {com.globalforge.infix.TestUserDev$UTerm1} ? &14=1 : &14=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(9);
            Assert.assertEquals("1", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t19() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UTerm1} == &45 ? &14=1 : &14=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(9);
            Assert.assertEquals("0", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t20() {
        try {
            String sampleRule = "&45 == {com.globalforge.infix.TestUserDev$UTerm1} ? &14=1 : &14=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(9);
            Assert.assertEquals("0", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t21() {
        try {
            String sampleRule = "&14 == {com.globalforge.infix.TestUserDev$UTerm1} ? &14=1 : &14=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(9);
            Assert.assertEquals("0", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t22() {
        try {
            String sampleRule =
                "&207 == {com.globalforge.infix.TestUserDev$UTerm1} ? &14=1 : &14=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDev.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(14);
            Assert.assertEquals("1", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t23() {
        try {
            String sampleRule =
                "&207 != {com.globalforge.infix.TestUserDev$UTerm1} ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDev.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("0", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t24() {
        try {
            String sampleRule = "&45 != {com.globalforge.infix.TestUserDev$UTerm1} ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDev.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("0", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t25() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UTerm1} != &45 ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDev.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("0", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t26() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UTerm1} != {com.globalforge.infix.TestUserDev$UTerm1} ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDev.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("0", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t27() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UTerm1} != &207 ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDev.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("0", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t28() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UTerm1} <= &207 ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDev.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("1", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t29() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UTerm1} >= &207 ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDev.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("1", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t30() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UTerm1} < &207 ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDev.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("0", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t31() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UTerm1} > &207 ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDev.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("0", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t32() {
        try {
            String sampleRule = "!{com.globalforge.infix.TestUserDev$UTerm1} ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDev.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("0", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t33() {
        try {
            String sampleRule = "^{com.globalforge.infix.TestUserDev$UTerm1} ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDev.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("1", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t34() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UTerm1} != {com.globalforge.infix.TestUserDev$UTerm1} ? &44=1 : &44=0";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDev.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("0", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t35() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx12}";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg("FIX.4.4", "D");
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("FOO", fld.getTagVal());
            Assert.assertEquals(42, fld.getTagNum());
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t36() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx12};&42=\"BAR\"";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg("FIX.4.4", "D");
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(3);
            Assert.assertEquals("BAR", fld.getTagVal());
            Assert.assertEquals(42, fld.getTagNum());
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    public static class UserCtxNonNumeric1 implements InfixUserContext {
        @Override
        public String visitMessage(String fixMessage) {
            return fixMessage;
        }

        @Override
        public void visitInfixAPI(InfixAPI infixApi) {
            infixApi.putContext("&FOO", "BAR");
        }
    }
    static final String sampleMessage2 = "8=FIX.4.4" + '\u0001' + "9=10" + '\u0001' + "35=8" // 2
        + '\u0001' + "44=-1" + '\u0001' + "555=2" + '\u0001' + "600=FOO" + '\u0001' + "601=2" // 6
        + '\u0001' + "539=1" + '\u0001' + "524=STR" + '\u0001' + "538=-33" + '\u0001' + "600=FOO1" // 10
        + '\u0001' + "601=3" + '\u0001' + "602=4" + '\u0001' + "207=42" + '\u0001' + "10=004"; // 14

    @Test
    public void t37$1() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UserCtxNonNumeric1};&42=\"BAR\"";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDev.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(14);
            Assert.assertEquals("BAR", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    //
    public static class UserCtxNonNumeric2 implements InfixUserContext {
        @Override
        public String visitMessage(String fixMessage) {
            return fixMessage;
        }

        @Override
        public void visitInfixAPI(InfixAPI infixApi) {
            infixApi.putContext("&", "BAR");
        }
    }

    @Test
    public void t37$2() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UserCtxNonNumeric2};&42=\"BAR\"";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDev.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(14);
            Assert.assertEquals("BAR", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    public static class UserCtxNonNumeric3 implements InfixUserContext {
        @Override
        public String visitMessage(String fixMessage) {
            return fixMessage;
        }

        @Override
        public void visitInfixAPI(InfixAPI infixApi) {
            infixApi.putContext("& ", "BAR");
        }
    }

    @Test
    public void t37$3() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UserCtxNonNumeric3};&42=\"BAR\"";
            InfixActions rules = new InfixActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDev.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(14);
            Assert.assertEquals("BAR", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    public static class UserCtxNonNumeric4 implements InfixUserContext {
        @Override
        public String visitMessage(String fixMessage) {
            return fixMessage;
        }

        @Override
        public void visitInfixAPI(InfixAPI infixApi) {
            infixApi.putContext(" ", "BAR");
        }
    }

    @Test(expected = RuntimeException.class)
    public void t37$4() throws UnsupportedEncodingException, IOException {
        String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtxNonNumeric4};&42=\"BAR\"";
        InfixActions rules = new InfixActions(sampleRule);
        rules.transformFIXMsg(TestUserDev.sampleMessage2);
    }

    public static class UserCtxNonNumeric5 implements InfixUserContext {
        @Override
        public String visitMessage(String fixMessage) {
            return fixMessage;
        }

        @Override
        public void visitInfixAPI(InfixAPI infixApi) {
            infixApi.putContext("", "BAR");
        }
    }

    @Test(expected = RuntimeException.class)
    public void t37$5() throws UnsupportedEncodingException, IOException {
        String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtxNonNumeric5};&42=\"BAR\"";
        InfixActions rules = new InfixActions(sampleRule);
        rules.transformFIXMsg(TestUserDev.sampleMessage2);
    }

    //
    public static class UserCtx12 implements InfixUserContext {
        @Override
        public String visitMessage(String fixMessage) {
            return fixMessage;
        }

        @Override
        public void visitInfixAPI(InfixAPI infixApi) {
            LinkedHashMap<String, String> myMap = new LinkedHashMap<String, String>();
            myMap.put("&42", "FOO");
            infixApi.putMessageDict(myMap);
        }
    }

    public static class UTerm1 implements InfixUserTerminal {
        @Override
        public String visitTerminal(InfixAPI infixApi) {
            return 42 + "";
        }
    }

    public static class UserCtx2 implements InfixUserContext {
        @Override
        public String visitMessage(String fixMessage) {
            return fixMessage;
        }

        @Override
        public void visitInfixAPI(InfixAPI infixApi) {
            infixApi.putContext("&555[0]->&600", "BAR"); // 26
            infixApi.putContext("&555[1]->&600", "BAR1"); // 31
            infixApi.putContext("&555[0]->&539[0]->&538", "525"); // 30
        }
    }
}
