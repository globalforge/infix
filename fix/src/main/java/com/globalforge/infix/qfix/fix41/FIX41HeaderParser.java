package com.globalforge.infix.qfix.fix41;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.globalforge.infix.qfix.DataStore;
import com.globalforge.infix.qfix.FieldParser;
import com.globalforge.infix.qfix.HeaderParser;

/*-
 The MIT License (MIT)

 Copyright (c) 2017 Global Forge LLC

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
 * @see HeaderParser
 * @author Michael C. Starkie
 */
public class FIX41HeaderParser extends HeaderParser {
    /** logger */
    final static Logger logger = LoggerFactory.getLogger(FIX41HeaderParser.class);

    public FIX41HeaderParser(String f, FieldParser cParser, DataStore c) throws Exception {
        super(f, cParser, c);
    }
}
