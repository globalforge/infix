package com.globalforge.infix.qfix;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/*-
The MIT License (MIT)

Copyright (c) 2019-2022 Global Forge LLC

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
 * The base class of all the various FieldToNameMaps. There will be 1 derived
 * class of this type for each FIX version.
 * @author Michael C. Starkie
 */
public abstract class FieldToNameMap {
    private final static Logger logger = LoggerFactory
        .getLogger(FieldToNameMap.class);
    protected static Map<String, String> map = new ConcurrentHashMap<>();
    public static String getTagName(String tagValue) {
        if (map.containsKey(tagValue)) {
            return map.get(tagValue);
        } else {
            return "";
        }
    }

    public static void putTagName(String tagValue, String tagName) {
        if ((tagValue != null && !tagValue.isEmpty())
            && (tagName != null && !tagName.isEmpty())) {
            map.put(tagValue, tagName);
        }
    }

    public static void putMap(Map<String, String> numToNameMap) {
        map = new ConcurrentHashMap<>(numToNameMap);
    }
}
