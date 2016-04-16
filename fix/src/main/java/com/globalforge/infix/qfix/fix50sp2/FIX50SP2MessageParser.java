package com.globalforge.infix.qfix.fix50sp2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.globalforge.infix.qfix.ComponentParser;
import com.globalforge.infix.qfix.FieldParser;
import com.globalforge.infix.qfix.HeaderParser;
import com.globalforge.infix.qfix.fix50.FIX50MessageParser;

public class FIX50SP2MessageParser extends FIX50MessageParser {
    /** logger */
    final static Logger logger = LoggerFactory.getLogger(FIX50SP2MessageParser.class);

    public FIX50SP2MessageParser(String f, FieldParser cParser, HeaderParser h,
        ComponentParser c) throws Exception {
        super(f, cParser, h, c);
    }
}
