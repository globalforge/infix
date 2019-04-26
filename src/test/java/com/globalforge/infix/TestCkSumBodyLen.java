package com.globalforge.infix;

import java.util.ArrayList;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import com.globalforge.infix.api.InfixActions;
import com.globalforge.infix.api.InfixField;

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
public class TestCkSumBodyLen {
    static final String sampleMessage1 = "8=FIX.4.4" + '\u0001' + "9=1000000" + '\u0001' + "35=8"
        + '\u0001' + "43=1" + '\u0001' + "207=USA" + '\u0001' + "10=000";
    InfixActions rules = null;
    String sampleRule = null;
    String result = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Test
    public void test() {
        try {
            sampleRule = "&43=8";
            rules = new InfixActions(sampleRule);
            result = rules.transformFIXMsg(TestCkSumBodyLen.sampleMessage1, true);
            ArrayList<InfixField> myList = StaticTestingUtils.parseMessageIntoList(result);
            InfixField fld = myList.get(2);
            Assert.assertEquals("8", fld.getTagVal());
            Assert.assertEquals(35, fld.getTagNum());
            Assert.assertEquals(6, myList.size());
            fld = myList.get(1);
            Assert.assertEquals(9, fld.getTagNum());
            Assert.assertEquals("18", fld.getTagVal());
            fld = myList.get(5);
            Assert.assertEquals(10, fld.getTagNum());
            Assert.assertEquals("124", fld.getTagVal());
            System.out.println(StaticTestingUtils.rs(result));
            System.out.println(StaticTestingUtils.ck(result));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
