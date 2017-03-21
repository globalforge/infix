package com.globalforge.infix.qfix.fix40;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.globalforge.infix.qfix.ComponentParser;
import com.globalforge.infix.qfix.FieldParser;
import com.globalforge.infix.qfix.HeaderParser;
import com.globalforge.infix.qfix.fix44.FIX44MessageParser;

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
 * FIX 4.0 is same as FIX 4.4 parser
 * @see FIX44MessageParser
 * @author Michael C. Starkie
 */
public class FIX40MessageParser extends FIX44MessageParser {
    /** logger */
    final static Logger logger = LoggerFactory.getLogger(FIX40MessageParser.class);

    public FIX40MessageParser(String f, FieldParser cParser, HeaderParser h, ComponentParser c)
        throws Exception {
        super(f, cParser, h, c);
    }
}
