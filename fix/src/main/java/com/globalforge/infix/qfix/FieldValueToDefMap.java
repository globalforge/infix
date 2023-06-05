package com.globalforge.infix.qfix;

import java.util.Map;

public abstract class FieldValueToDefMap {
	public Map<String, Map<String, String>> map = null;

	public Map<String, String> getFieldValueDefMap(String tagName) {
		return map.get(tagName);
	}
}
