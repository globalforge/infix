package com.globalforge.infix.api;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

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
 * An interface that provides a limited number of operations on the in-memory
 * state of a FIX message as actions are being parsed. This class should never
 * be called by an application thread or any thread other than the engine thread
 * making the callback. <br>
 * No control should be passed to any other thread during any operation defined
 * in this interface. Any deviation from this policy will produce undefined
 * results and may lead to data corruption or runtime exceptions. <br>
 * <strong>THREAD SAFETY: NOT SAFE.</strong>
 * 
 * @author Michael C. Starkie
 */
public interface InfixAPI {
    /**
     * Removes a FIX tag from the in-memory message.
     * 
     * @param ctx The tag number in action syntax (e.g., &44).
     */
    public void removeContext(String ctx);

    /**
     * Adds a FIX tag to the in-memory message and it's corresponding value.
     * 
     * @param ctx The tag number in action syntax (e.g., &44).
     * @param tagVal The tag value to assign to the tag number.
     */
    public void putContext(String ctx, String tagVal);

    /**
     * Returns an object containing the tag num and tag value associated with
     * tag number.
     * 
     * @param ctx The tag number in action syntax (e.g., &44).
     * @return InfixField Tag number and tag value.
     * @see InfixField
     */
    public InfixField getContext(String ctx);

    /**
     * A copy of the runtime map of immutable objects. <br>
     * The key is the tag number in action syntax. The value returned is not the
     * tag value associated with the tag number but rather a unique decimal
     * which describes the relative order of the field in the FIX message. You
     * can use this value as a key in the OrderToFieldDict dictionary to obtain
     * the value of the FIX field associated with the tag number. <br>
     * This map is useful if you want to know the relative position of a FIX tag
     * number in a FIX message.
     * 
     * @return Map<String, BigDecimal> A map of tag number in action context to
     * a unique order value.
     */
    public Map<String, BigDecimal> getCtxToOrderDict();

    /**
     * A copy of the runtime map of immutable objects. <br>
     * They key is the unique place or order in which the FIX field appears in
     * the FIX message (obtained from getCtxToOrderDict()) and value represents
     * the FIX field containing both tag number and tag value. <br>
     * This map is useful if you want to be able to retrieve the relative
     * position of a FIX tag value in a FIX message.
     * 
     * @return Map<BigDecimal, FixField> A map of the unique order value to the
     * FIX data.
     */
    public Map<BigDecimal, InfixField> getOrderToFieldDict();

    /**
     * A copy of the runtime map of immutable objects. <br>
     * Obtain a mapping of tag num in rule syntax to tag value. Key and value
     * are immutable.
     * 
     * @return Map<String, InfixField>. The key is the tag number in action
     * syntax and the value is the FIX data wrapped in {@link InfixField}.
     */
    public Map<String, InfixField> getCtxToFieldDict();

    /**
     * A copy of the runtime map of immutable InfixField. <br>
     * Obtain a mapping of tag num to tag value. Key and value are immutable.
     * 
     * @return Map<Integer, InfixField>. The key is the tag number and the value
     * is the FIX data wrapped in {@link InfixField}.
     */
    public Map<Integer, InfixField> getTagNumToFieldDict();

    /**
     * Insert FIX fields into the parsed message. Keys are tag numbers in action
     * syntax and values are the tag values associated with the keys. This
     * method will replace any fields already parsed. Insert order must be
     * preserved for the integrity of repeating groups.
     * 
     * @param msgDict LinkedHashMap<String, String> The FIX fields to insert in
     * the form of a dictionary of tag numbers in action syntax to tag values.
     */
    public void putMessageDict(LinkedHashMap<String, String> msgDict);

    /**
     * Obtain a fully formatted FIX message representing the memory state as it
     * currently exists during a action parse.
     * 
     * @return String a fully formatted FIX message.
     */
    public String getMessage();
}
