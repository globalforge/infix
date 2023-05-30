package com.globalforge.infix;

import java.util.Comparator;

/**
 * This will sort FIX data instances by tag number (e.g., 35, 150, etc.).
 * 
 * @author mstarkie
 *
 */
public class FixDataTagNumComparator implements Comparator<FixData> {
	@Override
	public int compare(FixData o1, FixData o2) {
		try {
			Integer o1TagNum = Integer.valueOf(o1.getTagNum());
			Integer o2TagNum = Integer.valueOf(o2.getTagNum());
			return o1TagNum.compareTo(o2TagNum);
		} catch (NumberFormatException e) {
			return o1.getTagNum().compareTo(o2.getTagNum());
		}
	}
}
