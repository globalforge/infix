package com.globalforge.infix;

import java.util.Comparator;

/**
 * This will sort FIX data instances by position within the FIX message.
 * @author mstarkie
 *
 */
public class FixDataTagPosComparator implements Comparator<FixData> {
   @Override
   public int compare(FixData o1, FixData o2) {
      return o1.getTagPos().compareTo(o2.getTagPos());
   }
}
