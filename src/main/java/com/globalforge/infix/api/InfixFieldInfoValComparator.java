package com.globalforge.infix.api;

import java.util.Comparator;

/**
 * This will sort a FIX message by tag value
 * @author mstarkie
 *
 */
public class InfixFieldInfoValComparator implements Comparator<InfixFieldInfo> {
   @Override
   public int compare(InfixFieldInfo o1, InfixFieldInfo o2) {
      return Integer.compare(o1.getTagNum(), o2.getTagNum());
   }
}
