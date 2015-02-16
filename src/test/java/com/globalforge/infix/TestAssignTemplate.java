package com.globalforge.infix;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import com.globalforge.infix.api.InfixActions;
import com.google.common.collect.ListMultimap;

public class TestAssignTemplate {
    static final String sampleMessage1 = "8=FIX.4.4" + '\u0001' + "9=1042"
        + '\u0001' + "35=D" + '\u0001' + "44=3.142" + '\u0001'
        + "60=20130412-19:30:00.686" + '\u0001' + "75=20130412" + '\u0001'
        + "45=0" + '\u0001' + "215=2" + '\u0001' + "216=FOO" + '\u0001'
        + "217=eb8cd" + '\u0001' + "216=BAR" + '\u0001' + "217=8dhosb"
        + '\u0001' + "10=004";
    private final SimpleDateFormat datetime = new SimpleDateFormat(
        "yyyyMMdd-HH:mm:ss");
    private final SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
    static StaticTestingUtils msgStore = null;
    InfixActions rules = null;
    String sampleRule = null;
    String result = null;
    ListMultimap<Integer, String> resultStore = null;

    @Test
    public void testDate() {
        try {
            sampleRule = "&75=<DATE>";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestAssignTemplate.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            String dateString = resultStore.get(75).get(0);
            String myDate = date.format(new Date());
            Assert.assertEquals(dateString, myDate);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testTimestamp() {
        try {
            sampleRule = "&60=<DATETIME>";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestAssignTemplate.sampleMessage1);
            resultStore = StaticTestingUtils.parseMessage(result);
            String dateString = resultStore.get(60).get(0);
            String myDate = datetime.format(new Date());
            Assert.assertTrue(dateString.contains(myDate));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
