package com.globalforge.infix.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import com.globalforge.infix.api.InfixActions;

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
/**
 * An example test application showing how to use the infix tool and to ensure
 * the integrity of builds.
 * 
 * @author Michael Starkie
 */
public class ExampleApp {
    /*
     * Some sample FIX messages to be used as input.
     */
    static final String sampleMessage = "8=FIX.4.4" + '\u0001' + "9=100"
        + '\u0001' + "34=8" + '\u0001' + "35=8" + '\u0001' + "44=3.142"
        + '\u0001' + "60=20130412-19:30:00.686" + '\u0001' + "75=20130412"
        + '\u0001' + "45=0" + '\u0001' + "47=0" + '\u0001' + "48=1.5"
        + '\u0001' + "49=SENDERCOMP" + '\u0001' + "56=TARGETCOMP" + '\u0001'
        + "382=2" + '\u0001' + "375=1.5" + '\u0001' + "655=fubi" + '\u0001'
        + "375=3" + '\u0001' + "655=yubl" + '\u0001' + "10=004";
    static final String sampleMessage2 = "8=FIX.5.0SP2" + '\u0001' + "9=100"
        + '\u0001' + "34=8" + '\u0001' + "35=8" + '\u0001' + "627=1" + '\u0001'
        + "628=COMPID" + '\u0001' + "629=20130412-19:30:00.686" + '\u0001'
        + "630=7" + '\u0001' + "44=3.142" + '\u0001'
        + "52=20140617-09:30:00.686" + '\u0001' + "75=20130412" + '\u0001'
        + "45=0" + '\u0001' + "47=0" + '\u0001' + "48=1.5" + '\u0001'
        + "49=SENDERCOMP" + '\u0001' + "56=TARGETCOMP" + '\u0001' + "382=2"
        + '\u0001' + "375=1.5" + '\u0001' + "655=fubi" + '\u0001' + "375=3"
        + '\u0001' + "655=yubl" + '\u0001' + "10=004";
    public static final String sampleMessage44Report = "8=FIX.4.4" + '\u0001'
        + "9=100" + '\u0001' + "35=8" + '\u0001' + "43=-1" + '\u0001' + "555=2"
        + '\u0001' + "600=FOO" + '\u0001' + "601=2" + '\u0001' + "539=2"
        + '\u0001' + "524=STR" + '\u0001' + "525=8" + '\u0001' + "538=-33"
        + '\u0001' + "524=MCS" + '\u0001' + "525=22" + '\u0001' + "538=33"
        + '\u0001' + "600=FOO1" + '\u0001' + "601=3" + '\u0001' + "539=1"
        + '\u0001' + "524=STR1" + '\u0001' + "525=0" + '\u0001' + "538=-34"
        + '\u0001' + "207=USA" + '\u0001' + "10=004";

    /*
     * Make console FIX output human readable.
     */
    public static String rs(String ins) {
        return ins.replaceAll("\u0001", "|");
    }

    public static void main(String[] args) {
        try {
            // priming the engine first makes the first transformation faster.
            InfixActions.primeEngine("FIX.4.4");
            for (;;) {
                System.out.println("Original Msg    : "
                    + ExampleApp.rs(ExampleApp.sampleMessage44Report));
                System.out
                    .println("Enter action(s) (see http://infix.globalforge.com/roadmap.html): ");
                BufferedReader br =
                    new BufferedReader(new InputStreamReader(System.in,
                        Charset.forName("UTF-8")));
                String sampleAction = br.readLine();
                // STEP 1: Encapsulate the action
                InfixActions action = new InfixActions(sampleAction);
                String result = null;
                long start = 0L;
                long diff = 0L;
                try {
                    start = System.nanoTime();
                    // STEP 2: Apply the action to a FIX message and obtain
                    // result
                    result =
                        action
                            .transformFIXMsg(ExampleApp.sampleMessage44Report);
                    diff = System.nanoTime() - start;
                } catch (Throwable t) {
                    t.printStackTrace();
                    continue;
                }
                String outMsg = ExampleApp.rs(result);
                System.out.println("Transformed Msg : " + outMsg);
                System.out
                    .println("time in millis: "
                        + TimeUnit.MILLISECONDS.convert(diff,
                            TimeUnit.NANOSECONDS));
                System.out.println();
                System.out.println("------Enter another action--------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
