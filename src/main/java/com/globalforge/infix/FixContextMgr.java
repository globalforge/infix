package com.globalforge.infix;

import com.globalforge.infix.qfix.MessageData;

/*-
 The MIT License (MIT)

 Copyright (c) 2016 Global Forge LLC

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
 * Dynamically instantiates a FixGroupMgr implementation given a proper Fix
 * version string.
 * @author Michael Starkie
 */
public class FixContextMgr {
    private static final FixContextMgr instance = new FixContextMgr();

    /**
     * Only 1 instance allowed.
     * @return FixContextMgr The single static instance.
     */
    public static final FixContextMgr getInstance() {
        return FixContextMgr.instance;
    }

    /**
     * Singleton only
     */
    private FixContextMgr() {
    }

    /**
     * Return an instance of MessageData. This is all the runtime data parsed
     * from the data dictionary.
     * @param fixVersion The FIX version whose data dictionary you want.
     * @return MessageData
     * @throws ClassNotFoundException reflection error
     * @throws InstantiationException reflection error
     * @throws IllegalAccessException reflection error
     */
    public MessageData getMessageData(String fixVersion)
        throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String upperCaseVer = fixVersion.replaceAll("[\",.]", "");
        String lowerCaseVer = upperCaseVer.toLowerCase();
        String messageDataStr = "com.globalforge.infix.qfix." + lowerCaseVer + ".auto."
            + upperCaseVer + "DynamicMessageData";
        Class<?> c = FixContextMgr.class.getClassLoader().loadClass(messageDataStr);
        return (MessageData) c.newInstance();
    }
}
