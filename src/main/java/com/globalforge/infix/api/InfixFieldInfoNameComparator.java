package com.globalforge.infix.api;

import java.util.Comparator;

/**
 * This will sort a FIX message by tag name
 * 
 * @author mstarkie
 */
public class InfixFieldInfoNameComparator implements Comparator<InfixFieldInfo> {
   /**
    * To-do: This doesn't work properly. Must look up the tagName from the proper
    * FieldValueToDefMap implementation in order to sort on it.
    */
   @Deprecated
   @Override
   public int compare(InfixFieldInfo o1, InfixFieldInfo o2) {
      return o1.getTag().toLowerCase().compareTo(o2.getTag().toLowerCase());
   }
}
