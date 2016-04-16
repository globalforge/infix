package com.globalforge.infix.qfix.fixt11;

import com.globalforge.infix.qfix.FieldParser;
import com.globalforge.infix.qfix.fix50.FIX50ComponentParser;

public class FIXT11ComponentParser extends FIX50ComponentParser {
    public FIXT11ComponentParser(String f, FieldParser cParser) throws Exception {
        super(f, cParser);
    }
}