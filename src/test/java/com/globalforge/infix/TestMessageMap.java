package com.globalforge.infix;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import com.globalforge.infix.api.InfixActions;
import com.globalforge.infix.api.InfixFieldInfo;
import com.globalforge.infix.api.InfixMap;

/*-
The MIT License (MIT)

Copyright (c) 2017 Global Forge LLC

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
public class TestMessageMap {


	@Test
	public void t1() {
		try {
			FixMessageMgr msgMgr = new FixMessageMgr();
			msgMgr.parseField("8=FIX.4.2Aqua");
			msgMgr.parseField("35=D");
			String s1 = msgMgr.toString();
			String sample = "8=FIX.4.2Aqua" + '\u0001' + "35=D";
			msgMgr = new FixMessageMgr(sample);
			String s2 = msgMgr.toString();
			Assert.assertEquals(s1, s2);
			HashMap<String, InfixFieldInfo> msgMap =
				msgMgr.getInfixMessageMap();
		}
		catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
	}

	@Test
	public void t2() {
		try {
			FixMessageMgr msgMgr = new FixMessageMgr();
			msgMgr.parseField("8=FIX.4.2Aqua");
			msgMgr.parseField("35=D");
			msgMgr.parseField("9000=2");
			msgMgr.parseField("9001=0.1");
			msgMgr.parseField("9002=0.2");
			msgMgr.parseField("9003=0.3");
			msgMgr.parseField("9001=1.1");
			msgMgr.parseField("9002=1.2");
			msgMgr.parseField("9003=1.3");
			HashMap<String, InfixFieldInfo> msgMap =
				msgMgr.getInfixMessageMap();
			Assert.assertEquals(msgMap.size(), 9);
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
        }
	}

	/**
	 * Below code represents an error as it leaves out the delimiter of a
	 * repeating group so the result is wrong.
	 */
	@Test
	public void t3() {
		try {
			FixMessageMgr msgMgr = new FixMessageMgr();
			msgMgr.parseField("8=FIX.4.2Aqua");
			msgMgr.parseField("35=D");
			msgMgr.parseField("9000=2");
			msgMgr.parseField("9002=0.2");
			msgMgr.parseField("9003=0.3");
			msgMgr.parseField("9002=1.2");
			msgMgr.parseField("9003=1.3");
			HashMap<String, InfixFieldInfo> msgMap =
				msgMgr.getInfixMessageMap();
			Assert.assertEquals(msgMap.size(), 5);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void t4() {
		try {
			FixMessageMgr msgMgr = new FixMessageMgr();
			msgMgr.parseField("8=FIX.4.2Aqua");
			msgMgr.parseField("35=D");
			msgMgr.parseField("9000=2");
			msgMgr.parseField("9001=0.1");
			msgMgr.parseField("9002=0.2");
			msgMgr.parseField("9003=0.3");
			msgMgr.parseField("9001=1.1");
			msgMgr.parseField("9002=1.2");
			msgMgr.parseField("9003=1.3");
			HashMap<String, InfixFieldInfo> msgMap =
				msgMgr.getInfixMessageMap();
			Assert.assertEquals(msgMap.size(), 9);
			FixMessageMgr newMgr = new FixMessageMgr(msgMap);
			HashMap<String, InfixFieldInfo> newMap =
				newMgr.getInfixMessageMap();
			Assert.assertEquals(newMap.size(), 9);
			String fix1 = msgMgr.toString();
			String fix2 = newMgr.toString();
			Assert.assertEquals(fix1, fix2);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void t5() {
		try {
			FixMessageMgr msgMgr =
				new FixMessageMgr(StaticTestingUtils.FIX_44_NEW_ORDER_CROSS);
			InfixMap infixMap = msgMgr.getInfixMap();
			Assert.assertNotNull(infixMap);
			String tag35 = infixMap.getTagValue(35);
			Assert.assertEquals(tag35, "s");
			String tag448 = infixMap.getTagValue("552[0]->453[0]->448");
			Assert.assertEquals(tag448, "8");
			String tag54 = infixMap.getGroupTagValue(552, 0, 54);
			Assert.assertEquals(tag54, "1");
			String tag54$2 = infixMap.getGroupTagValue(552, 1, 54);
			Assert.assertEquals(tag54$2, "2");
			String tag448$2 = infixMap.getTagValue("552[1]->453[1]->448");
			Assert.assertEquals(tag448$2, "aaa");
			//sampleRule = "&552[0]->&453[0]->&448=\"STARK\"";
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void t6() {
		try {
			String sampleRule = "&49=\"sender\"";
			InfixActions rules = new InfixActions(sampleRule);
			String result = rules
				.transformFIXMsg(StaticTestingUtils.FIX_44_NEW_ORDER_CROSS);
			FixMessageMgr fixMgr = new FixMessageMgr(result);
			InfixMap infixMap = fixMgr.getInfixMap();
			String mapStr = infixMap.toString();
			Assert.assertEquals(result, mapStr);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
