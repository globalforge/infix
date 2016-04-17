package com.globalforge.infix.qfix.fix40;

import javax.xml.stream.XMLStreamException;
import com.globalforge.infix.qfix.ComponentParser;
import com.globalforge.infix.qfix.FieldParser;

public class FIX40ComponentParser extends ComponentParser {
    public FIX40ComponentParser(String f, FieldParser cParser)
        throws Exception {
    }

    @Override
    public void parse() throws XMLStreamException, Exception {
        // No components in FIX40 to parse
    }
}
