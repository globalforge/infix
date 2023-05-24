package com.globalforge.infix;

import java.util.Comparator;

/**
 * This will sort FIX data instances by tag value.
 * @author mstarkie
 *
 */
public class FixDataTagValComparator implements Comparator<FixData> {
   @Override
   public int compare(FixData o1, FixData o2) {
      return o1.getTagVal().compareTo(o2.getTagVal());
   }
}
