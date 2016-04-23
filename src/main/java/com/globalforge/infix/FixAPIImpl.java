package com.globalforge.infix;

import java.util.LinkedHashMap;
import java.util.Map;
import com.globalforge.infix.api.InfixAPI;
import com.globalforge.infix.api.InfixFieldInfo;

/*-
 The MIT License (MIT)

 

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
    private final FixMessageMgr msgMgr;

    public FixAPIImpl(FixMessageMgr mgr) {
        msgMgr = mgr;
    }

    /**
     * Remove a fix context from memory during rule parse.
     * 
     * @see InfixAPI#removeContext(String)
     */
    @Override
    public void removeContext(String ctx) {
        msgMgr.removeContext(ctx);
    }

    /**
     * Insert a fix context into memory during a rule parse. FixVersion and
     * MsgType are not permitted to be modified.
     * 
     * @see InfixAPI#putContext(String, String)
     */
    @Override
    public void putContext(String ctx, String value) {
        if (!ctx.startsWith("&")) { throw new IllegalArgumentException(
            "field must start with '&' in putContext()."); }
        if (ctx.equals("&8")) { throw new IllegalArgumentException(
            "Invalid context change.  Can't change FIX Version."); }
        if (ctx.equals("&35")) { throw new IllegalArgumentException(
            "Invalid context change.  Can't change Msg Type."); }
        msgMgr.putContext(ctx, value);
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
    public void putMessageDict(LinkedHashMap<String, String> msgDict) {
        if (msgMgr.getContext("&8") == null) {
            if (!msgDict
                .containsKey("&8")) { throw new RuntimeException("Can't find tag 8 anywhere!"); }
        }
        if (msgMgr.getContext("&35") == null) {
            if (!msgDict
                .containsKey("&35")) { throw new RuntimeException("Can't find tag 35 anywhere!"); }
        }
        String[] keys = msgDict.keySet().toArray(new String[msgDict.size()]);
        for (String k : keys) {
            String v = msgDict.get(k);
            msgMgr.putContext(k, v);
        }
    }

    /**
     * Obtain the fix data associated with a fix context.
     * 
     * @see InfixAPI#getContext(String)
     */
    @Override
    public InfixFieldInfo getContext(String ctx) {
        return msgMgr.getContext(ctx);
    }

    /**
     * Obtain a fully formatted fix message representing the memory state as it
     * currently exists during a rule parse.
     * 
     * @see InfixAPI#getMessage()
     */
    @Override
    public String getMessage() {
        return msgMgr.toString();
    }

    @Override
    public Map<String, InfixFieldInfo> getMessageDict() {
        return msgMgr.getInfixMessageMap();
    }
}
