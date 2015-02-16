package com.globalforge.infix;

import java.util.Calendar;
import java.util.GregorianCalendar;

// -keep class !com.acme.Algorithm { *; }
/**
 * Don't delete this class from this package! I haven't found a good place for
 * it yet. It's currently copied to com.globalforge.infix.antlr but each time
 * you build the antlr source code from Gradle it will get blown away so you
 * have to copy it back from here if you want to expire the software at some
 * future date by calling this class from somewhere deep inside antlr where
 * supposedly no one will find it. It could easily be called from the infix code
 * instead and then you wouldn't have to worry about it.
 * 
 * @author Michael
 */
/*-
 The MIT License (MIT)

 Copyright (c) 2015 Global Forge LLC

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
class FixTimeChecker {
    int a = 2115;
    int b = Calendar.JUNE;
    int c = 30;
    Calendar t = new GregorianCalendar(a, b, c);
    Calendar u = GregorianCalendar.getInstance();

    void checkTime() {
        long e = t.getTimeInMillis();
        long $n = u.getTimeInMillis();
        if ($n > e) { throw new RuntimeException(); }
    }

    public static void main(String[] args) {
        FixTimeChecker eTest = new FixTimeChecker();
        eTest.checkTime();
    }
}
