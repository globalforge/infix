package com.globalforge.infix;

public class FixData {
   private String tagNum = "";
   private String tagName = "";
   private String tagVal = "";
   private String tagDef = "";

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

   @Override
   public String toString() {
      return "FixDisplayData [tagNum=" + tagNum + ", tagName=" + tagName + ", tagVal=" + tagVal
         + ", tagDef=" + tagDef + "]";
   }
}
