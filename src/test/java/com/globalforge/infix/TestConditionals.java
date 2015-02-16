package com.globalforge.infix;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import com.globalforge.infix.api.InfixActions;
import com.globalforge.infix.api.InfixAPI;
import com.globalforge.infix.api.InfixUserTerminal;
import com.google.common.collect.ListMultimap;

public class TestConditionals {
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    private ListMultimap<Integer, String> getResults(String sampleRule)
        throws Exception {
        InfixActions rules = new InfixActions(sampleRule);
        String result = rules.transformFIXMsg(TestConditionals.sampleMessage);
        return StaticTestingUtils.parseMessage(result);
    }
    static final String sampleMessage = "8=FIX.5.0SP2" + '\u0001' + "9=10"
        + '\u0001' + "34=8" + '\u0001' + "35=8" + '\u0001' + "627=1" + '\u0001'
        + "628=COMPID" + '\u0001' + "629=20130412-19:30:00.686" + '\u0001'
        + "630=7" + '\u0001' + "44=3.142" + '\u0001'
        + "52=20140617-09:30:00.686" + '\u0001' + "75=20130412" + '\u0001'
        + "45=0" + '\u0001' + "47=0" + '\u0001' + "48=1.5" + '\u0001'
        + "49=SENDERCOMP" + '\u0001' + "56=TARGETCOMP" + '\u0001' + "382=2"
        + '\u0001' + "375=1.5" + '\u0001' + "655=fubi" + '\u0001' + "375=3"
        + '\u0001' + "655=yubl" + '\u0001' + "10=004";

    @Test
    public void t1() {
        try {
            String sampleRule =
                "^&627 ? &627=&627+1; &627[&627]->&628=\"NEWCOMPID\"; &627[&627]->&629=&52; &627[&627]->&630=&34 : &627=1; &627[0]->&628=\"NEWCOMPID\"; &627[0]->&629=&75; &627[0]->&630=4;";
            ListMultimap<Integer, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("2", resultStore.get(627).get(0));
            Assert.assertEquals("COMPID", resultStore.get(628).get(0));
            Assert.assertEquals("20130412-19:30:00.686", resultStore.get(629)
                .get(0));
            Assert.assertEquals("7", resultStore.get(630).get(0));
            Assert.assertEquals("NEWCOMPID", resultStore.get(628).get(1));
            Assert.assertEquals("20140617-09:30:00.686", resultStore.get(629)
                .get(1));
            Assert.assertEquals("8", resultStore.get(630).get(1));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t2() {
        try {
            String sampleRule =
                "^&626 ? &627=&627+1; &627[&627]->&628=\"NEWCOMPID\"; &627[&627]->&629=&52; &627[&627]->&630=&34 : &627=1; &627[0]->&628=\"NEWCOMPID\"; &627[0]->&629=&75; &627[0]->&630=4;";
            ListMultimap<Integer, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("1", resultStore.get(627).get(0));
            Assert.assertEquals("NEWCOMPID", resultStore.get(628).get(0));
            Assert.assertEquals("20130412", resultStore.get(629).get(0));
            Assert.assertEquals("4", resultStore.get(630).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t3() {
        try {
            String sampleRule =
                "^&627 ? &627=&627+1; &627[&627]->&628=\"NEWCOMPID\"; &627[&627]->&629=&52; &627[&627]->&630=&34";
            ListMultimap<Integer, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("2", resultStore.get(627).get(0));
            Assert.assertEquals("COMPID", resultStore.get(628).get(0));
            Assert.assertEquals("20130412-19:30:00.686", resultStore.get(629)
                .get(0));
            Assert.assertEquals("7", resultStore.get(630).get(0));
            Assert.assertEquals("NEWCOMPID", resultStore.get(628).get(1));
            Assert.assertEquals("20140617-09:30:00.686", resultStore.get(629)
                .get(1));
            Assert.assertEquals("8", resultStore.get(630).get(1));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void t4() {
        try {
            String sampleRule =
                "&627[{com.globalforge.infix.TestConditionals$Assign1}]->&628=\"MIKEY\"";
            ListMultimap<Integer, String> resultStore = getResults(sampleRule);
            Assert.assertEquals("1", resultStore.get(627).get(0));
            Assert.assertEquals("MIKEY", resultStore.get(628).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    static class Assign1 implements InfixUserTerminal {
        @Override
        public String visitTerminal(InfixAPI infixApi) {
            return "0";
        }
    }
}
