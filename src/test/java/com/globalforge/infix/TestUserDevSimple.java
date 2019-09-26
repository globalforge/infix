package com.globalforge.infix;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import com.globalforge.infix.api.InfixAPI;
import com.globalforge.infix.api.InfixField;
import com.globalforge.infix.api.InfixFieldInfo;
import com.globalforge.infix.api.InfixSimpleActions;
import com.globalforge.infix.api.InfixUserContext;
import com.globalforge.infix.api.InfixUserTerminal;
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
public class TestUserDevSimple {
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
            infixApi.putContext("14", "199");
        }
    }

    @Test
    public void t1() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx1}";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(14, "199"));
            Assert.assertTrue(wasDone >= 0);
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
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(14, "199"));
            Assert.assertTrue(wasDone >= 0);
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
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(44, "99.0100"));
            Assert.assertTrue(wasDone >= 0);
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
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(14, "199"));
            Assert.assertTrue(wasDone >= 0);
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
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(14, "FOOBAR"));
            Assert.assertTrue(wasDone >= 0);
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
            infixApi.removeContext("14");
            infixApi.putContext("14", "199");
        }
    }

    @Test
    public void t8() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx4}";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(14, "199"));
            Assert.assertTrue(wasDone >= 0);
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
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(14, Math.PI + ""));
            Assert.assertTrue(wasDone >= 0);
            // again
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            myList = StaticTestingUtils.parseMessageIntoList(result);
            wasDone = myList.indexOf(new InfixField(14, Math.PI + ""));
            Assert.assertTrue(wasDone >= 0);
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
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(14, "-42"));
            Assert.assertTrue(wasDone >= 0);
            // again
            result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            myList = StaticTestingUtils.parseMessageIntoList(result);
            wasDone = myList.indexOf(new InfixField(14, "-42"));
            Assert.assertTrue(wasDone >= 0);
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
            InfixFieldInfo tag539 = idxDict.get("555");
            tag539.getPosition();
            InfixField fld = tag539.getField();
            Assert.assertEquals(fld.getTagVal(), fld.getTagVal());
            Assert.assertEquals(fld.getTagNum(), fld.getTagNum());
            idxDict.get("555[0]->539[0]->538");
            Assert.assertEquals(fld.getTagVal(), fld.getTagVal());
            Assert.assertEquals(fld.getTagNum(), fld.getTagNum());
        }
    }

    @Test
    public void t12() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx6}";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
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
            infixApi.putContext("8", "FIX.4.2");
        }
    }

    @Test(expected = RuntimeException.class)
    public void t13() throws UnsupportedEncodingException, IOException {
        String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx7}";
        InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
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
            infixApi.putContext("35", "D");
        }
    }

    @Test(expected = RuntimeException.class)
    public void t14() throws UnsupportedEncodingException, IOException {
        String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx8}";
        InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
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
            myMap.put("8", "FIX.4.2");
            infixApi.putMessageDict(myMap);
        }
    }

    @Test
    public void t15() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx9}";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
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
            myMap.put("35", "D");
            infixApi.putMessageDict(myMap);
        }
    }

    @Test
    public void t16() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtx10}";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(0);
            Assert.assertEquals("D", fld.getTagVal());
            Assert.fail();
        } catch (Throwable t) {
        }
    }

    @Test
    public void t18() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UTerm1} == {com.globalforge.infix.TestUserDev$UTerm1} ? &14=1 : &14=0";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
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
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(14, "0"));
            Assert.assertTrue(wasDone >= 0);
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t20() {
        try {
            String sampleRule = "&45 == {com.globalforge.infix.TestUserDev$UTerm1} ? &14=1 : &14=0";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(14, "0"));
            Assert.assertTrue(wasDone >= 0);
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t21() {
        try {
            String sampleRule = "&14 == {com.globalforge.infix.TestUserDev$UTerm1} ? &14=1 : &14=0";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(StaticTestingUtils.FIX_44_EXEC_REPORT);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(14, "0"));
            Assert.assertTrue(wasDone >= 0);
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
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDevSimple.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(14, "1"));
            Assert.assertTrue(wasDone >= 0);
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
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDevSimple.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(44, "0"));
            Assert.assertTrue(wasDone >= 0);
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t24() {
        try {
            String sampleRule = "&45 != {com.globalforge.infix.TestUserDev$UTerm1} ? &44=1 : &44=0";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDevSimple.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(44, "0"));
            Assert.assertTrue(wasDone >= 0);
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t25() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UTerm1} != &45 ? &44=1 : &44=0";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDevSimple.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(44, "0"));
            Assert.assertTrue(wasDone >= 0);
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
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDevSimple.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(44, "0"));
            Assert.assertTrue(wasDone >= 0);
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
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDevSimple.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(44, "0"));
            Assert.assertTrue(wasDone >= 0);
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
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDevSimple.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(44, "1"));
            Assert.assertTrue(wasDone >= 0);
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
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDevSimple.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(44, "1"));
            Assert.assertTrue(wasDone >= 0);
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t30() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UTerm1} < &207 ? &44=1 : &44=0";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDevSimple.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(44, "0"));
            Assert.assertTrue(wasDone >= 0);
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t31() {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$UTerm1} > &207 ? &44=1 : &44=0";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDevSimple.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(44, "0"));
            Assert.assertTrue(wasDone >= 0);
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t32() {
        try {
            String sampleRule = "!{com.globalforge.infix.TestUserDev$UTerm1} ? &44=1 : &44=0";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDevSimple.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(44, "0"));
            Assert.assertTrue(wasDone >= 0);
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t33() {
        try {
            String sampleRule = "^{com.globalforge.infix.TestUserDev$UTerm1} ? &44=1 : &44=0";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDevSimple.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(44, "1"));
            Assert.assertTrue(wasDone >= 0);
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
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDevSimple.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            int wasDone = myList.indexOf(new InfixField(44, "0"));
            Assert.assertTrue(wasDone >= 0);
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
            infixApi.putContext("FOO", "BAR");
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
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDevSimple.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(13);
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
            infixApi.putContext("#", "BAR");
        }
    }

    @Test
    public void t37$2() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UserCtxNonNumeric2};&42=\"BAR\"";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDevSimple.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(12);
            Assert.assertEquals("#", fld.getTag());
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
            infixApi.putContext("# ", "BAR");
        }
    }

    @Test
    public void t37$3() {
        try {
            String sampleRule =
                "{com.globalforge.infix.TestUserDev$UserCtxNonNumeric3};&42=\"BAR\"";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDevSimple.sampleMessage2);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(12);
            Assert.assertEquals("# ", fld.getTag());
            fld = myList.get(13);
            Assert.assertEquals("BAR", fld.getTagVal());
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail();
        }
    }
    static final String sampleMessageSymSuffixCombined = "8=FIX.4.2" + '\u0001' + "9=1042"
        + '\u0001' + "35=8" + '\u0001' + "55=ACL/U" + '\u0001' + "421=US" + '\u0001' + "10=004";

    @Test
    public void testSymbolSuffixSplit() throws UnsupportedEncodingException, IOException {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$SymbolSuffixSplit}";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDevSimple.sampleMessageSymSuffixCombined);
            ListMultimap<String, String> resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("ACL", resultStore.get("55").get(0));
            Assert.assertEquals("U", resultStore.get("65").get(0));
            result = StaticTestingUtils.rs(result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    static final String sampleMessageSymSuffixCombined2 = "8=FIX.4.2" + '\u0001' + "9=1042"
        + '\u0001' + "35=8" + '\u0001' + "55=ACL" + '\u0001' + "421=US" + '\u0001' + "10=004";

    @Test
    public void testSymbolSuffixSplit2() throws UnsupportedEncodingException, IOException {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$SymbolSuffixSplit}";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result =
                rules.transformFIXMsg(TestUserDevSimple.sampleMessageSymSuffixCombined2);
            ListMultimap<String, String> resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("ACL", resultStore.get("55").get(0));
            List<String> suffix = resultStore.get("65");
            Assert.assertTrue(suffix.isEmpty());
            result = StaticTestingUtils.rs(result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    static final String sampleMessageSymSuffixSplit =
        "8=FIX.4.2" + '\u0001' + "9=1042" + '\u0001' + "35=D" + '\u0001' + "55=ACL" + '\u0001'
            + "65=U" + '\u0001' + "421=US" + '\u0001' + "10=004";

    @Test
    public void testSymbolSuffixCombine() throws UnsupportedEncodingException, IOException {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$SymbolSuffixCombine}";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDevSimple.sampleMessageSymSuffixSplit);
            ListMultimap<String, String> resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("ACL/U", resultStore.get("55").get(0));
            List<String> suffix = resultStore.get("65");
            Assert.assertTrue(suffix.isEmpty());
            result = StaticTestingUtils.rs(result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    static final String sampleMessageSymSuffixSplit2 = "8=FIX.4.2" + '\u0001' + "9=1042" + '\u0001'
        + "35=D" + '\u0001' + "55=ACL" + '\u0001' + "421=US" + '\u0001' + "10=004";

    @Test
    public void testSymbolSuffixCombine2() throws UnsupportedEncodingException, IOException {
        try {
            String sampleRule = "{com.globalforge.infix.TestUserDev$SymbolSuffixCombine}";
            InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
            String result = rules.transformFIXMsg(TestUserDevSimple.sampleMessageSymSuffixSplit2);
            ListMultimap<String, String> resultStore = StaticTestingUtils.parseMessage(result);
            Assert.assertEquals("ACL", resultStore.get("55").get(0));
            List<String> suffix = resultStore.get("65");
            Assert.assertTrue(suffix.isEmpty());
            result = StaticTestingUtils.rs(result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
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

    // @Test(expected = RuntimeException.class)
    public void t37$4() throws UnsupportedEncodingException, IOException {
        String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtxNonNumeric4};&42=\"BAR\"";
        InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
        rules.transformFIXMsg(TestUserDevSimple.sampleMessage2);
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

    public void t37$5() throws UnsupportedEncodingException, IOException {
        String sampleRule = "{com.globalforge.infix.TestUserDev$UserCtxNonNumeric5};&42=\"BAR\"";
        InfixSimpleActions rules = new InfixSimpleActions(sampleRule);
        rules.transformFIXMsg(TestUserDevSimple.sampleMessage2);
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
            myMap.put("42", "FOO");
            infixApi.putMessageDict(myMap);
        }
    }

    public static class UTerm1 implements InfixUserTerminal {
        @Override
        public String visitTerminal(InfixAPI infixApi) {
            return 42 + "";
        }
    }

    public static class SymbolSuffixSplit implements InfixUserContext {
        @Override
        public String visitMessage(String fixMessage) {
            return fixMessage;
        }

        @Override
        public void visitInfixAPI(InfixAPI infixApi) {
            InfixFieldInfo field55 = infixApi.getContext("55");
            if (field55 == null) { return; }
            String[] symsfx = field55.getTagVal().split(Pattern.quote("/"));
            if (symsfx == null || symsfx.length != 2) { return; }
            infixApi.putContext("55", symsfx[0]);
            infixApi.putContext("65", symsfx[1]);
        }
    }

    public static class SymbolSuffixCombine implements InfixUserContext {
        @Override
        public String visitMessage(String fixMessage) {
            return fixMessage;
        }

        @Override
        public void visitInfixAPI(InfixAPI infixApi) {
            InfixFieldInfo field55 = infixApi.getContext("55");
            if (field55 == null) { return; }
            InfixFieldInfo field65 = infixApi.getContext("65");
            if (field65 == null) { return; }
            infixApi.putContext("55", field55.getTagVal() + "/" + field65.getTagVal());
            infixApi.removeContext("65");
        }
    }
}
