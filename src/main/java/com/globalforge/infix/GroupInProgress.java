package com.globalforge.infix;

import com.globalforge.infix.qfix.FixRepeatingGroup;

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
 * Used to keep track of repeating groups as they are being parsed. Instances of
 * this class are kept on a stack at runtime when parsing a fix message. An
 * example stack is shown below. The top of the stack is on the left. <br>
 * <br>
 * Examples: <code>
 * &539[0]->, &555[0]->
 * </code> <br>
 * In the above example we are in the middle of parsing fields that belong to
 * the first nesting ([0]) of the NestedParties repeating group. NestedParties
 * is denoted by the groupId tag 539. The NestedParties repeating group happens
 * to be nested inside the first nesting ([0]) of InstrmtLegExecGrp, another
 * repeating group denoted by the fix tag 555.<br>
 * <br>
 * This information tells the system that the next tag to expect will be a
 * member of NestedParties. If the next tag is not a member of NestedParties the
 * stack is popped and checked if it belongs as a member of InstrmtLegExecGrp.
 * If not, the stack is popped again. Once the stack is empty, subsequent tags
 * do not belong to repeating groups or else they indicate the start of a new
 * group.
 *
 * @author Michael
 */
class GroupInProgress {
    private int levels = 0;
    private int curIdx = -1;
    private String grpIdent = null;
    private FixRepeatingGroup repeatingGroup = null;

    /**
     * A group in prgress is a repeating group with some state management data.
     * It is a temporary object used for accounting purposes only while parsing
     * a fix message.
     *
     * @param rgrp The repeating group currently being parsed.
     * @param numOfGrps The tag vlue of the groupId field (e.g.,
     * NoContraGrps(382=2)).
     * @param tagNum The tag number of the groupId field (e.g., 382).
     */
    GroupInProgress(FixRepeatingGroup rgrp, int numOfGrps, String tagNum) {
        repeatingGroup = rgrp;
        levels = numOfGrps;
        grpIdent = tagNum;
    }

    /**
     * The current group. If repeating, there may be more than 1 set of tags.
     * Each set of tags in a group is referred to here as the group number.
     *
     * @return
     */
    int getCurGroupNumber() {
        return curIdx;
    }

    /**
     * The group number is incremented only when we encounter the delimeter tag
     * of a repeating group. The delimiter tag is always the first tag following
     * the groupId and it indicates that 1) the group is repeating when
     * encountered again or 2) the group has ended (when not encountered
     * anymore).
     *
     * @return
     */
    int incCurGoupNumber() {
        curIdx += 1;
        if (curIdx == levels) {
            curIdx = -1;
        }
        return curIdx;
    }

    /**
     * The groupId tag (e.g., NoContraGrps(382)). The tag number tells us which
     * group and the tag value tells us how many.
     *
     * @return The tag number of the repeating group.
     */
    String getGroupIdentifier() {
        return grpIdent;
    }

    FixRepeatingGroup getRepeatingGroup() {
        return repeatingGroup;
    }

    /**
     * The string value is used to build the tag contexts of tags belonging to a
     * repeating group. <br>
     * <br>
     * <strong> MODIFYING THIS METHOD WILL BREAK THE PROGRAM </strong>
     */
    @Override
    public String toString() {
        return '&' + getGroupIdentifier() + '[' + getCurGroupNumber() + "]->";
    }
}