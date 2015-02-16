package com.globalforge.infix;

import java.math.BigDecimal;
import java.math.MathContext;

/*-
 The MIT License (MIT)

 Copyright (c) 2015 Global Forge LLC

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
 * num and provides information about it's nested position within a message. The
 * infix application maintains the order of tags in the input message. It
 * accomplishes this by mapping a number associated with the tag context in the
 * original message. The FieldContext class is a temporary holder of this
 * information before the member fields are referenced by the mapping event.
 * Examples of tag contexts for tags 35 and 375: &35, &382[0]->&375,
 * &382[1]->&375
 * 
 * @author Michael Starkie
 */
public class FixFieldContext {
    /**
     * A unique value describing a tag num and any nesting information if the
     * tag is part of a repeating group. Used as a key in a hash of tag
     * numbers..
     */
    private final String key;
    /**
     * A value representing the relative position of a tag context in the
     * original message
     */
    private final BigDecimal value;

    /**
     * An imutable instance of this class.
     * 
     * @param ctx A fix tag number in rule context. Tag numbers that appear more
     * than once will each have a unique context string. The context value is
     * the tag number coupled with information about it's location in a fix
     * message.
     * @param val A fix tag value.
     */
    public FixFieldContext(String ctx, String val) {
        key = ctx;
        value = new BigDecimal(val, MathContext.DECIMAL128);
    }

    /**
     * @return String The tag context describing it's location index.
     */
    public String getKey() {
        return key;
    }

    /**
     * The value associated with the tag context.
     * 
     * @return
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Each tag context (or key) is a unique string and refers to a specific
     * field in a fix message regardless of tags numbers that may appear more
     * than once.
     */
    @Override
    public int hashCode() {
        return key.hashCode();
    }

    /**
     * Equality is defined as two instance with the same tag context (key).
     */
    @Override
    public boolean equals(Object anObject) {
        if (anObject instanceof FixFieldContext) {
            FixFieldContext that = (FixFieldContext) anObject;
            return this.getKey().equals(that.getKey());
        }
        return false;
    }

    @Override
    public String toString() {
        return key.toString() + "|" + value;
    }
}
