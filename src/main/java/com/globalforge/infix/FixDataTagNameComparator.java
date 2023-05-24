package com.globalforge.infix;

import java.util.Comparator;

/**
 * This will sort FIX data instances by tag name (e.g., ExecType).
 * @author mstarkie
 *
 */
public class FixDataTagNameComparator implements Comparator<FixData> {
   @Override
   public int compare(FixData o1, FixData o2) {
      return o1.getTagName().compareTo(o2.getTagName());
   }
}
