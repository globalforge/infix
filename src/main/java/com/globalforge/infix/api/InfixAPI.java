package com.globalforge.infix.api;

import java.util.LinkedHashMap;
import java.util.Map;

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
 * An interface that provides a limited number of operations on the in-memory
 * state of a FIX message as actions are being parsed. This class should never
 * be called by an application thread or any thread other than the engine thread
 * making the callback. <br>
 * No control should be passed to any other thread during any operation defined
 * in this interface. Any deviation from this policy will produce undefined
 * results and may lead to data corruption or runtime exceptions. <br>
 * <strong>THREAD SAFETY: NOT SAFE.</strong>
 * @author Michael C. Starkie
 */
public interface InfixAPI {
    /**
     * Removes a FIX field from the in-memory message.
     * @param ctx The tag number in action syntax (e.g., &44).
     */
    public void removeContext(String ctx);

    /**
     * Inserts a FIX field.
     * @param key The tag number in Infix syntax (e.g., &44)
     * @param value The tag value (e.g., 42.0000)
     */
    public void putContext(String key, String value);

    /**
     * Returns an object containing the Field field, including tag number and
     * value as well as it's relative order within the FIX message
     * @param ctx The tag number in action syntax (e.g., &44).
     * @return InfixField Tag number and tag value.
     * @see InfixField
     */
    public InfixFieldInfo getContext(String ctx);

    /**
     * Insert FIX fields into the parsed message. Keys are tag numbers in action
     * syntax and values are the tag values associated with the keys. This
     * method will replace any fields already parsed. Insert order must be
     * preserved for the integrity of repeating groups but it's up to the caller
     * to ensure order of keys.
     * @param msgDict LinkedHashMap<String, String> The FIX fields to insert in
     * the form of a dictionary of tag numbers in action syntax to tag values.
     */
    public void putMessageDict(LinkedHashMap<String, String> msgDict);

    /**
     * Get the runtime data dictionary
     * @return Map<String, InfixFieldInfo>
     */
    public Map<String, InfixFieldInfo> getMessageDict();

    /**
     * Obtain a fully formatted FIX message representing the memory state as it
     * currently exists during a action parse.
     * @return String a fully formatted FIX message.
     */
    public String getMessage();
}
