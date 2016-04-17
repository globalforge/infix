package com.globalforge.infix.qfix.fix41;

import javax.xml.stream.XMLStreamException;
import com.globalforge.infix.qfix.ComponentParser;
import com.globalforge.infix.qfix.FieldParser;

public class FIX41ComponentParser extends ComponentParser {
    public FIX41ComponentParser(String f, FieldParser cParser)
        throws Exception {
    }

    @Override
    public void parse() throws XMLStreamException, Exception {
        // No components in FIX41 to parse
    }
}
