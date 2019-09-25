package com.globalforge.infix;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.globalforge.infix.api.InfixFieldInfo;

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
/**
 * Holds the state of the fix message being transformed. Tag and value lookups
 * are based on maps for speed. Tag order is maintained. Ordered mappings are
 * maintained as tag number to InfixFieldInfo where InfixFieldInfo contains a
 * FixField and a number representing the position of the field in the message.
 * <p>
 * The SimpleFix Manager will ignore all repeating groups such that the last
 * occurrence of a repeating tag will end up in the transformed message thus
 * corrupting the group. Use the Simple Manager only if you don't care about
 * repeating groups.
 * <p>
 * Simple: No support for repeating groups, you get back only the tags that were
 * given unless provided by rules, no message type or FIX version required or
 * enforced. You can modify any tag at your own risk.
 * 
 * @author Michael Starkie
 */
public class FixSimpleMessageMgr extends FixMessageMgr {
   final static Logger logger = LoggerFactory.getLogger(FixSimpleMessageMgr.class);
   /** keeps the original order of FIX tags */
   private int pos = 0;

   /**
    * You better know what you're doing.
    * <p>
    * Use this constructor when you want to build the message map from scratch.
    * <br>
    * You should call the constructor and then make repeated calls to
    * parseField(). <br>
    */
   public FixSimpleMessageMgr() {
   }

   /**
    * Parses a fix message in raw fix format, assigns context, and keeps state.
    *
    * @param baseMsg The input message
    */
   public FixSimpleMessageMgr(String baseMsg) throws Exception {
      parseMessage(baseMsg);
   }

   /**
    * Initialize the message manager with a map.
    *
    * @param mMap A map of tag number to InfixFieldInfo
    */
   public FixSimpleMessageMgr(Map<String, InfixFieldInfo> mMap) {
      ArrayList<InfixFieldInfo> orderedFields = new ArrayList<InfixFieldInfo>(mMap.values());
      Collections.sort(orderedFields);
      for (InfixFieldInfo field : orderedFields) {
         parseField(field.getTag(), field.getTagVal());
      }
   }

   /**
    * Parses a string in the form "35=D" into a tag number (35) and tag value
    * (D) and calls {@link FixSimpleMessageMgr#putField(int, String)} to map the
    * results.
    *
    * @param fixField The string representing a Fix field as it is found in a
    * Fix message.
    * @throws Exception can't create the runtime classes specified by the FIX
    * version.
    */
   public void parseField(String tagStr, String tagVal) {
      putField(tagStr, tagVal);
   }

   /**
    * Keeps the order of fix fields excluding the header and trailer.
    * 
    * @param tagStr FIX Tag
    * @param tagVal FIX Tag value
    * @return InfixFieldInfo
    */
   private InfixFieldInfo getInfixFieldInfo(String tagStr, String tagVal) {
      InfixFieldInfo fieldInfo =
         new InfixFieldInfo(tagStr, tagVal, new BigDecimal(pos, MathContext.DECIMAL32));
      pos += 1;
      return fieldInfo;
   }

   /**
    * Inserts a fix field into the mapping. Converts a tag number and value into
    * a rule context and inserts the context and associated field data into the
    * mappings. Order of tag data within the message is maintained.
    *
    * @param tagNum The tag number
    * @param tagVal The tag value
    */
   protected void putField(String tagStr, String tagVal) {
      msgMap.put(tagStr, getInfixFieldInfo(tagStr, tagVal));
   }

   /**
    * Inserts an Infix context directly into the message map
    *
    * @param ctx The Infix context
    * @param tagVal The tag value and order of the field referenced by the Infix
    * context
    */
   void putContext(String ctx, String tagVal) {
      InfixFieldInfo fldInfo = msgMap.get(ctx);
      if (fldInfo != null) {
         InfixFieldInfo newInfo = new InfixFieldInfo(ctx, tagVal, fldInfo.getPosition());
         msgMap.put(ctx, newInfo);
         return;
      }
      msgMap.put(ctx, getInfixFieldInfo(ctx, tagVal));
   }

   /**
    * Removes a fix field given a tag reference in rule syntax.
    *
    * @param ctx The tag number in rule syntax.
    */
   void removeContext(String ctx) {
      remove(ctx);
   }

   /**
    * Produces a valid FIX string from the state of this instance.
    *
    * @return String A valid FIX string.
    */
   @Override
   public String toString() {
      StringBuilder str = new StringBuilder();
      int bodyLength = 0;
      ArrayList<InfixFieldInfo> orderedFields = new ArrayList<InfixFieldInfo>(msgMap.values());
      Collections.sort(orderedFields);
      String fieldStr = null;
      // Calculate body length
      for (InfixFieldInfo fieldInfo : orderedFields) {
         if ("8".equals(fieldInfo.getTag()) || "9".equals(fieldInfo.getTag())
            || "10".equals(fieldInfo.getTag())) {
            continue;
         }
         fieldStr = fieldInfo.getField().toString() + '\u0001';
         bodyLength += fieldStr.length();
         str.append(fieldStr);
      }
      if (containsContext("9")) {
         putContext("9", Integer.toString(bodyLength));
         InfixFieldInfo bodyLen = getContext("9");
         fieldStr = bodyLen.getField().toString() + '\u0001';
         str.insert(0, fieldStr);
      }
      InfixFieldInfo version = getContext("8");
      if (version != null) {
         fieldStr = version.getField().toString() + '\u0001';
         str.insert(0, fieldStr);
      }
      if (containsContext("10")) {
         char[] inputChars = str.toString().toCharArray();
         int checkSum = 0;
         for (int aChar : inputChars) {
            checkSum += aChar;
         }
         putContext("10", String.format("%03d", checkSum % 256));
         InfixFieldInfo chksum = getContext("10");
         str.append(chksum.getField()).append('\u0001');
      }
      return str.toString();
   }
}
