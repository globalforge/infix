package com.globalforge.infix.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
 * A convenience class for passing around serialized versions of an infix map.
 * Should only be used for serializing infix maps and reading tag values at the
 * receiving end.
 * 
 * @author mstarkie
 */
public class InfixMap implements Serializable {
	private static final long serialVersionUID = 1L;
	private final Map<String, InfixFieldInfo> infixMap;

	public InfixMap(HashMap<String, InfixFieldInfo> iMap) {
		infixMap = Collections.unmodifiableMap(iMap);
	}

	/**
	 * Obtain the unmodifiable map backing this class. Useful if you want to
	 * modify the data. If that is your intention then create an instance of
	 * FixMessageMgr giving this map to the constructor, then add fields and
	 * obtain the new map from FixMessageMgr and call InfixMap with the new map.
	 * <br>
	 * <code>
	 *     // for simple tags
	 *     Map<String, InfixFieldInfo> unmodifiableMap = infixMap.getUnmodifiableMap();
	 *     FixMessageMgr fixMsgMgr = new FixMessageMgr(unmodifiableMap);
	 *     fixMsgMgr.parseField(76="FOO"); //add tag 76
	 *     InfixMap infixMap = fixMsgMgr.getInfixMessageMap();
	 * </code>
	 * <p>
	 * OR
	 * <p>
	 * <code> 
	 *    // if you want to apply Infix actions
	 *    Map<String, InfixFieldInfo> unmodifiableMap = infixMap.getUnmodifiableMap();
	 *    FixMessageMgr fixMsgMgr = new FixMessageMgr(unmodifiableMap);
	 *    String fixString = fixMsgMgr.toString();
	 *    String rule = "&552[0]->&453[0]->&448=\"STARK\""; 
	 *    InfixActions rules = new InfixActions(sampleRule); 
	 *    String result = rules.transformFIXMsg(fixString);
	 *    fixMsgMgr = new FixMessageMgr(result);
	 *    infixMap = fixMsgMgr.getInfixMap();
	 * </code>
	 * 
	 * @return Map<String, InfixFieldInfo>
	 */
	public Map<String, InfixFieldInfo> getUnmodifiableMap() {
		return infixMap;
	}

	/**
	 * Given a context string, return the tag value associated with the context.
	 * <br>
	 * May be used with non repeating tags, or repeating tags at any level of
	 * nesting withing repeating groups. <code>
	 *    Example ctx:
	 *       35
	 *       382[0]->375
	 *       552[0]->453[1]->448
	 * </code>
	 * 
	 * @param ctx Infix Context
	 * @return String a FIX tag value associated with the context.
	 */
	public String getTagValue(String ctx) {
		String tagVal = null;
		InfixFieldInfo info = infixMap.get(ctx);
		if (info != null) {
			tagVal = info.getTagVal();
		}
		return tagVal;
	}

	/**
	 * Given a tag number, return the tag value associated with the tag num. If
	 * used with a repeating group the results are undefined probably NULL;
	 * 
	 * @param tagNum FIX tag number.
	 * @return String a FIX tag value associated with the context.
	 */
	public String getTagValue(int tagNum) {
		String tagVal = null;
		InfixFieldInfo info = infixMap.get(tagNum + "");
		if (info != null) {
			tagVal = info.getTagVal();
		}
		return tagVal;
	}

	/**
	 * @see getGroupTagValue(String grpIdTagNum, int grpIdx, String
	 * grpMemberTagNum)
	 */
	public String getGroupTagValue(int grpIdTagNum, int grpIdx,
		int grpMemberTagNum) {
		return getGroupTagValue(grpIdTagNum + "", grpIdx, grpMemberTagNum + "");
	}

	/**
	 * Looks up the tag value within a repeating group.
	 * <p>
	 * THIS METHOD ONLY WORKS FOR REPEATING GROUPS WHICH ARE NOT NESTED INSIDE
	 * OTHER REPEATING GROUPS. <br>
	 * 
	 * @param grpIdTagNum Every repeating group has an identifier, the tag
	 * containing the number of repeating groups, e.g.(NoContraBrokers(382)).
	 * <p>
	 * @param grpIdx The nesting level of the group. Repeating groups start at
	 * zero. (e.g., The first group, the second group, the third, etc.). The
	 * number of levels is determined by the value of the grpIdTagNum in the
	 * original message. For example if 382=3, there are 3 tags whose tagNum is
	 * 375. grpIdx tells the method which one you want.
	 * <p>
	 * @param grpMemberTagNum The specific member tag in the group (e.g.,
	 * ContraBroker(375)).
	 * <p>
	 * @return String The value of the grpMemberTagNum
	 */
	public String getGroupTagValue(String grpIdTagNum, int grpIdx,
		String grpMemberTagNum) {
		String tagVal = null;
		String grpCtx = grpIdTagNum + "[" + grpIdx + "]->" + grpMemberTagNum;
		InfixFieldInfo info = infixMap.get(grpCtx);
		if (info != null) {
			tagVal = info.getTagVal();
		}
		return tagVal;
	}

	/**
	 * Produces a valid FIX string from the state of this instance. The validaty
	 * of the FIX string is only as good as the map this instance was
	 * constructed with.
	 * 
	 * @return String A valid FIX string.
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		ArrayList<InfixFieldInfo> orderedFields =
			new ArrayList<InfixFieldInfo>(infixMap.values());
		Collections.sort(orderedFields);
		String fieldStr = null;
		for (InfixFieldInfo fieldInfo : orderedFields) {
			InfixField field = fieldInfo.getField();
            fieldStr = field.toString() + '\u0001';
			str.append(fieldStr);
		}
		return str.toString();
	}

    /**
     * Produces a human readable FIX message for logging purposes
     * 
     * @return String A human-readable FIX string.
     */
    public String toLogString() {
        String result = toString();
        return result.replaceAll("\u0001", "|");
    }
}
