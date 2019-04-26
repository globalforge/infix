package com.globalforge.infix;

import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.FailedPredicateException;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;

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
 * An extension to the Antlr class allowing for more detailed error handling.
 * * @see DefaultErrorStrategy
 * @author Michael C. Starkie
 */
public class FixRulesErrorStrategy extends DefaultErrorStrategy {
    @Override
    public void reportError(Parser recognizer, RecognitionException e) {
        super.reportError(recognizer, e);
    }

    /**
     * @see DefaultErrorStrategy#reportFailedPredicate
     */
    @Override
    protected void reportFailedPredicate(Parser recognizer, FailedPredicateException e) {
        super.reportFailedPredicate(recognizer, e);
    }

    /**
     * @see DefaultErrorStrategy#reportInputMismatch
     */
    @Override
    protected void reportInputMismatch(Parser recognizer, InputMismatchException e) {
        super.reportInputMismatch(recognizer, e);
    }

    /**
     * @see DefaultErrorStrategy#reportNoViableAlternative
     */
    @Override
    protected void reportNoViableAlternative(Parser parser, NoViableAltException e) {
        String msg = "can't choose between alternatives"; // nonstandard msg
        parser.notifyErrorListeners(e.getOffendingToken(), msg, e);
    }
}
