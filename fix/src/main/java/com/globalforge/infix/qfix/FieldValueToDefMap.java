package com.globalforge.infix.qfix;

import java.util.Map;

public abstract class FieldValueToDefMap {
	public static Map<String, Map<String, String>> map = null;

	public static Map<String, String> getFieldValueDefMap(String tagName) {
		return map.get(tagName);
	}
}
