package com.globalforge.infix.api;

import java.io.Serializable;
import java.math.BigDecimal;

/*-
 The MIT License (MIT)

 Copyright (c) 2019-2022 Global Forge LLC

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
/**
 * A means to bind a tag number to it's position in the input message. A tag
 * number may appear more than once in a fix message within a repeating group.
 * All tag numbers have a distinct context string that both indicates the tag
 * number and provides information about it's nested position within a message.
 * The infix application maintains the order of tags in the input message. It
 * accomplishes this by mapping a number associated with the tag context in the
 * original message. The FieldContext class is a temporary holder of this
 * information before the member fields are referenced by the mapping event.
 * Examples of tag contexts for tags 35 and 375: {@literal &}35, {@literal &}382[0]-{@literal >}{@literal &}375,
 * {@literal &}382[1]-{@literal >}{@literal &}375 
 * 
 * @author Michael Starkie
 */
public class InfixFieldInfo implements Serializable, Comparable<InfixFieldInfo> {
   private static final long serialVersionUID = 1L;
   /**
    * A unique value describing a tag number and any nesting information if the
    * tag is part of a repeating group. Used as a key in a hash of tag numbers..
    */
   private final InfixField field;
   /**
    * A value representing the relative position of a tag context in the
    * original message
    */
   private final BigDecimal position;
   
   /**
    * Placeholder for an optional tagName.  Tag name will only exist in the context for which the parsed data dictionaries are used.
    * This means that if any *Simple* class are used, like InfixSimpleActions, the tagName will not exist because the general concept behind
    * using the *Simple* classes are that they are not dependent on the fix library.
    */
   private String tagName = "";

   /**
    * Constructor
    * 
    * @param tag FIX Tag number
    * @param val FIX Tag value
    * @param pos The position of the field within a FIX message
    */
   public InfixFieldInfo(String tag, String val, BigDecimal pos) {
      field = new InfixField(tag, val);
      position = pos;
   }

   /**
    * return the tag associated with this field (may not be an int)
    * 
    * @return String The tag
    */
   public String getTag() {
      return field.getTag();
   }

   /**
    * return the tag associated with this field (may not be an int)
    * 
    * @return String The tag
    */
   public int getTagNum() {
      return field.getTagNum();
   }

   /**
    * Return the tag value associated with this field.
    * 
    * @return String The tag value.
    */
   public String getTagVal() {
      return field.getTagVal();
   }

   /**
    * Get the Field Object
    * 
    * @return InfixField The field as an InfixField type
    */
   public InfixField getField() {
      return field;
   }

   /**
    * The value associated with the tag context.
    * 
    * @return BigDecimal The position of the field within a FIX message
    */
   public BigDecimal getPosition() {
      return position;
   }
   
   public void setTagName(String tagName) {
      this.tagName = tagName;
   }

   public String getTagName() {
      return tagName;
   }

   @Override
   public String toString() {
      return "[" + field + ", pos=" + position + "]";
   }

   @Override
   public int compareTo(InfixFieldInfo o) {
      return position.compareTo(o.getPosition());
   }


}
