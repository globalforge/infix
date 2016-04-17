package com.globalforge.infix.qfix.fix42;

import javax.xml.stream.XMLStreamException;
import com.globalforge.infix.qfix.ComponentParser;
import com.globalforge.infix.qfix.FieldParser;

public class FIX42ComponentParser extends ComponentParser {
    public FIX42ComponentParser(String f, FieldParser cParser)
        throws Exception {
    }

    @Override
    public void parse() throws XMLStreamException, Exception {
        // No components in FIX42 to parse
    }
}
