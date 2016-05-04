package com.globalforge.infix;

import com.globalforge.infix.qfix.FixGroupMgr;
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
 *
 * @author Michael Starkie
 */
public class FixContextMgr {
    private static final FixContextMgr instance = new FixContextMgr();

    /**
     * Only 1 instance allowed.
     *
     * @return FixContextMgr The single static instance.
     */
    public static final FixContextMgr getInstance() {
        return FixContextMgr.instance;
    }

    private FixContextMgr() {
    }

    /**
     * Uses reflaction to create an instance of the FixManager for the version
     * given as an argument.
     *
     * @param fixVersion The fix version that specifies what FixManager class to
     * create an instance of.
     * @return A sub-class of FixGroupMgr
     * @throws ClassNotFoundException If the fix message contains a Fix version
     * in tag 8 that is unrecognized the system will fail when it tries to
     * instantiate a {@link FixGroupMgr} for that version at runtime.
     * @throws IllegalAccessException If the class represented by the fix
     * version or its nullary constructor is not accessible..
     * @throws InstantiationException If the class represented by the fix
     * version represents an abstract class, an interface, an array class, a
     * primitive type, or void; or if the class has no nullary constructor; or
     * if the instantiation fails for some other reason.
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
