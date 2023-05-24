package com.globalforge.infix;

import java.util.Comparator;

/**
 * This will sort FIX data instances by tag number (e.g., 35, 150, etc.).
 * @author mstarkie
 *
 */
public class FixDataTagNumComparator implements Comparator<FixData> {
   @Override
   public int compare(FixData o1, FixData o2) {
      return o1.getTagNum().compareTo(o2.getTagNum());
   }
}
