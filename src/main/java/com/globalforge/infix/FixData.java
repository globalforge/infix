package com.globalforge.infix;

import java.math.BigDecimal;

/**
 * Holds all the known data in relation to a parsed FIX message.
 * 
 * @author mstarkie
 */
public class FixData {
   /** 150 */
   private String tagNum = "";
   /** (ExecType) */
   private String tagName = "";
   /** =2 */
   private String tagVal = "";
   /** (Filled) */
   private String tagDef = "";
   /** Position of the tag within the FIX message */
   private BigDecimal tagPos = new BigDecimal(Long.MAX_VALUE);

   public String getTagNum() {
      return tagNum;
   }

   public void setTagNum(String param) {
      if (param == null) {
         return;
      }
      this.tagNum = param;
   }

   public String getTagName() {
      return tagName;
   }

   public void setTagName(String param) {
      if (param == null) {
         return;
      }
      this.tagName = param;
   }

   public String getTagVal() {
      return tagVal;
   }

   public void setTagVal(String param) {
      if (param == null) {
         return;
      }
      this.tagVal = param;
   }

   public String getTagDef() {
      return tagDef;
   }

   public void setTagDef(String param) {
      if (param == null) {
         return;
      }
      this.tagDef = param;
   }

   public BigDecimal getTagPos() {
      return tagPos;
   }

   public void setTagPos(BigDecimal param) {
      if (param == null) {
         return;
      }
      this.tagPos = param;
   }

   @Override
   public String toString() {
      return "FixData [tagNum=" + tagNum + ", tagName=" + tagName + ", tagVal=" + tagVal
         + ", tagDef=" + tagDef + ", tagPos=" + tagPos + "]";
   }
}
