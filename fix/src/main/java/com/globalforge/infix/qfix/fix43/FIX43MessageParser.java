package com.globalforge.infix.qfix.fix43;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.globalforge.infix.qfix.ComponentParser;
import com.globalforge.infix.qfix.FieldParser;
import com.globalforge.infix.qfix.HeaderParser;
import com.globalforge.infix.qfix.fix44.FIX44MessageParser;

public class FIX43MessageParser extends FIX44MessageParser {
    /** logger */
    final static Logger logger = LoggerFactory
        .getLogger(FIX43MessageParser.class);

    public FIX43MessageParser(String f, FieldParser cParser, HeaderParser h,
        ComponentParser c) throws Exception {
        super(f, cParser, h, c);
    }
}
