package com.globalforge.infix;

import java.math.BigDecimal;
import java.util.Map;
import com.globalforge.infix.api.InfixAPI;
import com.globalforge.infix.api.InfixField;

/*-
 The MIT License (MIT)

 Copyright (c) 2015 Global Forge LLC

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
 * Implementation of an API allowing a user a handle into the internal engine
 * during a rule parse.
 * 
 * @see InfixAPI
 * @author Michael C. Starkie
 */
class FixAPIImpl implements InfixAPI {
    private final FixMessageMgr fldMgr;

    public FixAPIImpl(FixMessageMgr mgr) {
        fldMgr = mgr;
    }

    /**
     * Remove a fix context from memory during rule parse.
     * 
     * @see InfixAPI#removeContext(String)
     */
    @Override
    public void removeContext(String ctx) {
        fldMgr.removeContext(ctx);
    }

    /**
     * Insert a fix context into memory during a rule parse. FixVersion and
     * MsgType are not permitted to be modified.
     * 
     * @see InfixAPI#putContext(String, String)
     */
    @Override
    public void putContext(String ctx, String tagVal) {
        if (ctx.equals("&8")) { throw new IllegalArgumentException(
            "Invalid context change.  Can't change FIX Version."); }
        if (ctx.equals("&35")) { throw new IllegalArgumentException(
            "Invalid context change.  Can't change Msg Type."); }
        FixContextBuilder ctxBldr = new FixContextBuilder();
        ctxBldr.parseContext(ctx);
        fldMgr.putContext(ctxBldr.getContexts(), ctxBldr.getTagNum(), tagVal);
    }

    /**
     * A map of immutable objects. The key is the tag number in rule syntax. The
     * value is the unique decimal which describes the order the fix field
     * appears in the fix message.
     * 
     * @see InfixAPI#getCtxDict()
     */
    @Override
    public Map<String, BigDecimal> getCtxToOrderDict() {
        return fldMgr.getCtxToOrderDict();
    }

    /**
     * A map of immutable objects. They key is the unique place or order in
     * which the fix field appears in the fix message and value represents the
     * fix field containing both tag number and tag value.
     * 
     * @see InfixAPI#getFieldDict()
     */
    @Override
    public Map<BigDecimal, InfixField> getOrderToFieldDict() {
        return fldMgr.getOrderToFieldDict();
    }

    /**
     * Obtain a mapping of tag number to tag value
     * 
     * @return Map<String, FixField>. The key is the tag number in rule syntax
     * and the value is the fix data wrapped in {@link InfixField}.
     */
    @Override
    public Map<String, InfixField> getCtxToFieldDict() {
        return fldMgr.getCtxToFieldDict();
    }

    /**
     * A copy of the runtime map of immutable FixFields.
     * <p>
     * Obtain a mapping of tag num to tag value. Key and value are immutable.
     * 
     * @return Map<Integer, FixField>. The key is the tag number and the value
     * is the FIX data wrapped in {@link InfixField}.
     */
    @Override
    public Map<Integer, InfixField> getTagNumToFieldDict() {
        return fldMgr.getTagNumToFieldDict();
    }

    /**
     * Insert fix fields into the parsed message. Keys are tag numbers in rule
     * syntax and values are the tag values associated with the keys. This
     * method will replace any fields already parsed.
     * 
     * @see InfixAPI#putMessageDict(Map)
     * @param msgDict Map<String, String> The fix fields to insert in the form
     * of a dictionary of tag numbers in rule syntax to tag values.
     */
    @Override
    public void putMessageDict(Map<String, String> msgDict) {
        String[] keys = msgDict.keySet().toArray(new String[msgDict.size()]);
        for (String k : keys) {
            String v = msgDict.get(k);
            putContext(k, v);
        }
    }

    /**
     * Obtain the fix data associated with a fix context.
     * 
     * @see InfixAPI#getContext(String)
     */
    @Override
    public InfixField getContext(String ctx) {
        return fldMgr.getContext(ctx);
    }

    /**
     * Obtain a fully formatted fix message representing the memory state as it
     * currently exists during a rule parse.
     * 
     * @see InfixAPI#getMessage()
     */
    @Override
    public String getMessage() {
        return fldMgr.toString();
    }
}
