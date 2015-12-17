package com.globalforge.infix;

import java.util.ArrayList;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import com.globalforge.infix.api.InfixActions;
import com.globalforge.infix.api.InfixField;

public class TestCkSumBodyLen {
    static final String sampleMessage1 = "8=FIX.4.4" + '\u0001' + "9=1000000"
        + '\u0001' + "35=8" + '\u0001' + "43=1" + '\u0001' + "207=USA"
        + '\u0001' + "10=000";
    InfixActions rules = null;
    String sampleRule = null;
    String result = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Test
    public void test() {
        try {
            sampleRule = "&35=8";
            rules = new InfixActions(sampleRule);
            result =
                rules.transformFIXMsg(TestCkSumBodyLen.sampleMessage1, true);
            ArrayList<InfixField> myList =
                StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(2);
            Assert.assertEquals("8", fld.getTagVal());
            Assert.assertEquals(35, fld.getTagNum());
            Assert.assertEquals(6, myList.size());
            fld = myList.get(1);
            Assert.assertEquals(9, fld.getTagNum());
            Assert.assertEquals("18", fld.getTagVal());
            fld = myList.get(5);
            Assert.assertEquals(10, fld.getTagNum());
            Assert.assertEquals("117", fld.getTagVal());
            System.out.println(StaticTestingUtils.rs(result));
            System.out.println(StaticTestingUtils.ck(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
