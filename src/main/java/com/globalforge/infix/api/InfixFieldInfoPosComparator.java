package com.globalforge.infix.api;

import java.util.Comparator;

/**
 * This will sort a FIX message by position.
 * @author mstarkie
 *
 */
public class InfixFieldInfoPosComparator implements Comparator<InfixFieldInfo> {
   @Override
   public int compare(InfixFieldInfo o1, InfixFieldInfo o2) {
      return o1.compareTo(o2);
   }
}
