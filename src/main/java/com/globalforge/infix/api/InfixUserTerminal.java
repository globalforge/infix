package com.globalforge.infix.api;

/*-
 The MIT License (MIT)

 Copyright (c) 2019-2020 Global Forge LLC

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
 * Allows a user to specify user defined behavior when parsing a fix rule.
 * Contains one method which eturns a single literal value for use in assignment
 * a value to a FIX tag.
 * <p>
 * No control should be passed to any other thread during any operation defined
 * in this interface. Any deviation from this policy will produce undefined
 * results and may lead to data corruption or runtime exceptions.
 * <p>
 * <strong>THREAD SAFETY: NOT SAFE.</strong>
 * @author Michael C. Starkie
 */
public interface InfixUserTerminal {
    /**
     * Visit the user defined class with an API to the internal engine. This
     * class is invoked during an assignment. It is the implementors
     * responsibility to ensure that a non-null/non-empty resut is returned to
     * complete the assignment.
     * @param infixApi A handle to the internal engine.
     * @return String the result that will be used to complete the assignment.
     */
    public String visitTerminal(InfixAPI infixApi);
}
