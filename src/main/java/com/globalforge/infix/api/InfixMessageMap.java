package com.globalforge.infix.api;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
public class InfixMessageMap implements Map<String, InfixFieldInfo>, Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, InfixFieldInfo> ctxMap = new HashMap<String, InfixFieldInfo>();

    @Override
    public void clear() {
        ctxMap.clear();
    }

    public boolean containsKey(String ctx) {
        return ctxMap.containsKey(ctx);
    }

    @Override
    public InfixFieldInfo put(String key, InfixFieldInfo value) {
        return ctxMap.put(key, value);
    }

    public InfixFieldInfo get(String key) {
        return ctxMap.get(key);
    }

    public InfixFieldInfo remove(String key) {
        return ctxMap.remove(key);
    }

    @Override
    public Set<String> keySet() {
        return ctxMap.keySet();
    }

    @Override
    public int size() {
        return ctxMap.size();
    }

    @Override
    public boolean isEmpty() {
        return ctxMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return ctxMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return ctxMap.containsValue(value);
    }

    @Override
    public InfixFieldInfo get(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends InfixFieldInfo> m) {
        ctxMap.putAll(m);
    }

    @Override
    public Collection<InfixFieldInfo> values() {
        return ctxMap.values();
    }

    @Override
    public Set<java.util.Map.Entry<String, InfixFieldInfo>> entrySet() {
        return ctxMap.entrySet();
    }

    @Override
    public InfixFieldInfo remove(Object key) {
        return ctxMap.remove(key);
    }
}
