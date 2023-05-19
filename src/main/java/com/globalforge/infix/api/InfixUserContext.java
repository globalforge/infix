package com.globalforge.infix.api;

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
 * Allows a user to specify user defined behavior when parsing a fix rule.
 * @author Michael C. Starkie
 */
public interface InfixUserContext {
    /**
     * Allows a caller to obtain a fully formatted FIX message from memory.
     * Possibly after a series of actions have been applied and before others
     * have yet to be applied.
     * <p>
     * This is useful if you need to make custom modifications that can not be
     * achieved given the current set of supported rules.
     * <p>
     * The resulting FIX message will replace the original inside the InFIX
     * memory cache and subsequent transformatios will be applied until the
     * parser is finished.
     * @param fixMessage A legal FIX protocol message with field values as they
     * exist in memory at the time of the call.
     * @return String A legal FIX protocol message with any changes. It is the
     * collers responsibility to parse the input, make changes, and reformat the
     * message for return.
     * <p>
     * No control should be passed to any other thread during any operation
     * defined in this interface. Any deviation from this policy will produce
     * undefined results and may lead to data corruption or runtime exceptions.
     * <p>
     * <strong>THREAD SAFETY: NOT SAFE.</strong>
     */
    public String visitMessage(String fixMessage);

    /**
     * Obtain a handle to an API for in memory changes to a FIX message before
     * transformation into a FIX string
     * @param infixApi The concrete InfixAPI instance.
     */
    public void visitInfixAPI(InfixAPI infixApi);
}
