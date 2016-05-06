package com.globalforge.infix.api;

import java.io.Serializable;

/*-
 The MIT License (MIT)

 Copyright (c) 2016 Global Forge LLC

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
 * An encapsulation of a Fix Field including tag number and tag value.
 * @author Michael Starkie
 */
public final class InfixField implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int tagNum;
    private final String tagVal;

    /**
     * Constructs an immutable instance.
     * @param num A Fix tag number.
     * @param val A fix tag value.
     */
    public InfixField(int num, String val) {
        tagNum = num;
        tagVal = val;
    }

    /**
     * Constructs an immutable instance.
     * @param num A Fix tag number.
     * @param val A fix tag value.
     */
    public InfixField(String num, String val) {
        tagNum = Integer.parseInt(num);
        tagVal = val;
    }

    /**
     * return the tag number associated with this field.
     * @return int The tag number.
     */
    public int getTagNum() {
        return tagNum;
    }

    /**
     * Return the tag value associated with this field.
     * @return int The tag value.
     */
    public String getTagVal() {
        return tagVal;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof InfixField) {
            if (this == obj) { return true; }
            return this.hashCode() == obj.hashCode();
        }
        return false;
    }

    /**
     * Return a string representation of this instance.
     */
    @Override
    public String toString() {
        String fieldStr = getTagNum() + "=" + getTagVal();
        return fieldStr;
    }
}
