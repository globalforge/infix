package com.globalforge.infix;

import java.util.ArrayList;

import com.globalforge.infix.api.InfixField;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

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
public class StaticTestingUtils {
   // name = [InstrmtLegExecGrp], id = [555], members = [600|601|602
   // name = [NestedParties], id = [539], members = [524|525|538],
   public static String FIX_44_EXEC_REPORT = "8=FIX.4.4" + '\u0001' + "9=182" + '\u0001' + "35=8" // 2
      + '\u0001' + "34=3" + '\u0001' + "49=BRKR" + '\u0001' + "52=20121105-23:24:42" + '\u0001' // 5
      + "56=BANZAI" + '\u0001' + "6=0" + '\u0001' + "11=1352157882577" + '\u0001' + "14=0" // 9
      + '\u0001' + "17=1" + '\u0001' + "31=0" + '\u0001' + "32=0" + '\u0001' + "37=1" + '\u0001' // 13
      + "38=10000" + '\u0001' + "39=0" + '\u0001' + "54=1" + '\u0001' + "55=MSFT" + '\u0001' // 17
      + "150=2" + '\u0001' + "151=0" + '\u0001' + "382=1" + '\u0001' + "375=1" + '\u0001' // 21
      + "437=444" + '\u0001' + "438=20121105-23:24:42" + '\u0001' + "655=2" + '\u0001' + "555=2" // 25
      + '\u0001' + "600=FOO" + '\u0001' + "601=2 " + '\u0001' + "539=1" + '\u0001' + "524=STR" // 29
      + '\u0001' + "538=-33" + '\u0001' + "600=FOO1" + '\u0001' + "601=3" + '\u0001' + "602=4" // 33
      + "10=085" + '\u0001'; // 34
   // 35=s, 552=2, SideCrossOrdModGrp, 453=2, Parties (nested)
   public static String FIX_44_NEW_ORDER_CROSS = "8=FIX.4.4" + '\u0001' + "9=247" + '\u0001' // 1
      + "35=s" + '\u0001' + "34=5" + '\u0001' + "49=sender" + '\u0001' // 4
      + "52=20060319-09:08:20.881" + '\u0001' + "56=target" + '\u0001' + "22=8" + '\u0001' // 7
      + "40=2" + '\u0001' + "44=9" + '\u0001' + "48=ABC" + '\u0001' + "55=ABC" + '\u0001' // 11
      + "60=20060319-09:08:19" + '\u0001' + "548=184214" + '\u0001' + "549=2" + '\u0001' + "550=0" // 15
      + '\u0001' + "552=2" + '\u0001' + "54=1" + '\u0001' + "453=2" + '\u0001' + "448=8" // 19
      + '\u0001' + "447=D" + '\u0001' + "452=4" + '\u0001' + "448=AAA35777" + '\u0001' + "447=D" // 23
      + '\u0001' + "452=3" + '\u0001' + "38=9" + '\u0001' + "54=2" + '\u0001' + "453=2" + '\u0001' // 27
      + "448=8" + '\u0001' + "447=D" + '\u0001' + "452=4" + '\u0001' + "448=aaa" + '\u0001' // 31
      + "447=D" + '\u0001' + "452=3" + '\u0001' + "38=9" + '\u0001' + "10=056" + '\u0001'; // 35
   /*-
   public static String FIX_44_NEW_ORDER_LIST = "8=FIX.4.4" + '\u0001' + "9=247" + '\u0001' // 1
       + "35=E" + '\u0001' + "66=MyListID" + '\u0001' + "394=1"  + '\u0001' //4
       + "73=2" + '\u0001' //5
           + "11=ClOrdID1" + '\u0001' //6
           + "67=1" + '\u0001' //7
           + "78=2" + '\u0001'
               + "79=acct1" + '\u0001'
               + "661=IDSource1" + '\u0001'
               + "736=USD" + '\u0001'
               + "467=allocID1" + '\u0001'
               + "539=2" + '\u0001'  //13
                   + "524=nPartyID1" +  '\u0001'
                   + "538=nPartyRole1" +  '\u0001'
                   + "524=nPartyID2" +  '\u0001'
                   + "525=nPartyIDSource1" +  '\u0001'
   
                + "79=acct2" + '\u0001' //18
                + "661=IDSource2" + '\u0001'
                + "736=CAD" + '\u0001'
                + "467=allocID2" + '\u0001'
                + "539=1" + '\u0001' //22
                    + "524=nPartyID2" +  '\u0001'
                    + "538=nPartyRole2" +  '\u0001'
   
            + "11=ClOrdID2" + '\u0001' //25
            + "67=2" + '\u0001'
            + "78=2" + '\u0001'
                + "79=acct3" + '\u0001'
                + "661=IDSource3" + '\u0001'
                + "736=USD" + '\u0001'
                + "467=allocID3" + '\u0001'
                + "539=2" + '\u0001'
                    + "524=nPartyID3" +  '\u0001'
                    + "538=nPartyRole3" +  '\u0001' //34
                    + "524=nPartyID4" +  '\u0001'
                    + "525=nPartyIDSource3" +  '\u0001'
   
                 + "79=acct4" + '\u0001'
                 + "661=IDSource4" + '\u0001'
                 + "736=CAD" + '\u0001'
                 + "467=allocID4" + '\u0001'
                 + "539=1" + '\u0001'
                     + "524=nPartyID4" +  '\u0001'
                     + "538=nPartyRole4" +  '\u0001';
   
   */
   public static String FIX_44_NEW_ORDER_LIST = "8=FIX.4.4" + '\u0001' + "9=247" + '\u0001' // 1
      + "35=E" + '\u0001' + "66=MyListID" + '\u0001' + "394=1" + '\u0001' + "73=2" + '\u0001' // 5
      + "11=ClOrdID1" + '\u0001' + "67=1" + '\u0001' + "78=2" + '\u0001' + "79=acct1" + '\u0001' // 9
      + "661=IDSource1" + '\u0001' + "736=USD" + '\u0001' + "467=allocID1" + '\u0001' + "539=2" // 13
      + '\u0001' + "524=nPartyID1" + '\u0001' + "538=nPartyRole1" + '\u0001' + "524=nPartyID2" // 16
      + '\u0001' + "525=nPartyIDSource1" + '\u0001' + "79=acct2" + '\u0001' + "661=IDSource2" // 19
      + '\u0001' + "736=CAD" + '\u0001' + "467=allocID2" + '\u0001' + "539=1" + '\u0001' // 22
      + "524=nPartyID2" + '\u0001' + "538=nPartyRole2" + '\u0001' + "11=ClOrdID2" + '\u0001' // 25
      + "67=2" + '\u0001' + "78=2" + '\u0001' + "79=acct3" + '\u0001' + "661=IDSource3" + '\u0001' // 29
      + "736=USD" + '\u0001' + "467=allocID3" + '\u0001' + "539=2" + '\u0001' + "524=nPartyID3" // 33
      + '\u0001' + "538=nPartyRole3" + '\u0001' + "524=nPartyID4" + '\u0001' // 35
      + "525=nPartyIDSource3" + '\u0001' + "79=acct4" + '\u0001' + "661=IDSource4" + '\u0001' // 38
      + "736=CAD" + '\u0001' + "467=allocID4" + '\u0001' + "539=1" + '\u0001' + "524=nPartyID4" // 42
      + '\u0001' + "538=nPartyRole4" + '\u0001' + "10=056" + '\u0001';; // 44

   public static final ListMultimap<String, String> parseMessage(String baseMsg) {
      ListMultimap<String, String> dict = LinkedListMultimap.create();
      char[] baseMsgArray = baseMsg.toCharArray();
      int index = 0;
      for (int i = 0; i < baseMsgArray.length; i++) {
         if (baseMsgArray[i] == 0x01) {
            String fixField = baseMsg.substring(index, i);
            char[] fixFieldArray = fixField.toCharArray();
            for (int j = 0; j < fixFieldArray.length; j++) {
               if (fixFieldArray[j] == '=') {
                  String num = fixField.substring(0, j);
                  String val = fixField.substring(j + 1, fixField.length());
                  dict.put(num, val);
                  break;
               }
            }
            index = i + 1;
         }
      }
      return dict;
   }

   public static final ArrayList<InfixField> parseMessageIntoList(String baseMsg) {
      ArrayList<InfixField> myArray = new ArrayList<InfixField>();
      char[] baseMsgArray = baseMsg.trim().toCharArray();
      int index = 0;
      for (int i = 0; i < baseMsgArray.length; i++) {
         if (baseMsgArray[i] == 0x01) {
            // locate the next field (35=D)
            String fixField = baseMsg.substring(index, i);
            // System.out.println("fixfield: " + fixField);
            StaticTestingUtils.parseField(fixField, myArray);
            index = i + 1;
         }
      }
      String fixField = baseMsg.substring(index, baseMsgArray.length);
      StaticTestingUtils.parseField(fixField, myArray);
      return myArray;
   }

   public static final void parseField(String fixField, ArrayList<InfixField> myArray) {
      char[] fixFieldArray = fixField.toCharArray();
      // break up the field into tagNum and tagVal
      for (int j = 0; j < fixFieldArray.length; j++) {
         if (fixFieldArray[j] == '=') {
            String tagNum = fixField.substring(0, j);
            String tagVal = fixField.substring(j + 1, fixField.length());
            myArray.add(new InfixField(tagNum, tagVal));
            return;
         }
      }
   }

   public static String rs(String ins) {
      return ins.replaceAll("\u0001", "|");
   }

   public static String showCount(String ins) {
      return ins.replaceAll("\u0001", "|");
   }

   public static String ck(String str) {
      StringBuilder sb = new StringBuilder();
      char[] inputChars = str.toString().toCharArray();
      for (char aChar : inputChars) {
         sb.append((int) aChar);
         sb.append(" ");
      }
      return sb.toString();
   }
}
