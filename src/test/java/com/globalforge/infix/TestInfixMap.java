package com.globalforge.infix;

import org.junit.Assert;
import org.junit.Test;

import com.globalforge.infix.api.InfixFieldInfoNameComparator;
import com.globalforge.infix.api.InfixFieldInfoPosComparator;
import com.globalforge.infix.api.InfixFieldInfoValComparator;

/*-
The MIT License (MIT)

Copyright (c) 2019-2022 Global Forge LLC

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
public class TestInfixMap {
    public static String fixMsg1 = "8=FIX.4.2^A9=69^A35=0^A49=RPTRDCSHFD^A56=CANTRPTRDCSHFD^A34=427^A52=20230518-04:03:03.328^A21=3^A10=079^A";
    public static String fixMsg44 = "8=FIX.4.4^A9=69^A35=0^A49=RPTRDCSHFD^A56=CANTRPTRDCSHFD^A34=427^A52=20230518-04:03:03.328^A21=1^A10=079^A";

    @Test
    public void t1() {
        try {
            String properFix = fixMsg1.replaceAll("\\^A", "\u0001");
            FixMessageMgr msgMgr = new FixMessageMgr(properFix);
            String displayString = msgMgr.getInfixMap().toDisplayString(new InfixFieldInfoNameComparator());
            System.out.println(displayString);
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    
    @Test
    public void t2() {
        try {
            String properFix = fixMsg44.replaceAll("\\^A", "\u0001");
            FixMessageMgr msgMgr = new FixMessageMgr(properFix);
            String displayString = msgMgr.getInfixMap().toDisplayString(new InfixFieldInfoValComparator());
            System.out.println(displayString);
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    
    @Test
    public void t3() {
        try {
            String properFix = fixMsg44.replaceAll("\\^A", "\u0001");
            FixMessageMgr msgMgr = new FixMessageMgr(properFix);
            String displayString = msgMgr.getInfixMap().toDisplayString(new InfixFieldInfoPosComparator());
            System.out.println(displayString);
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
