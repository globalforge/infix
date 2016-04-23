package com.globalforge.infix.api;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InfixMessageMap
    implements Map<String, InfixFieldInfo>, Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, InfixFieldInfo> ctxMap =
        new HashMap<String, InfixFieldInfo>();

    public void clear() {
        ctxMap.clear();
    }

    public boolean containsKey(String ctx) {
        return ctxMap.containsKey(ctx);
    }

    public InfixFieldInfo put(String key, InfixFieldInfo value) {
        return ctxMap.put(key, value);
    }

    public InfixFieldInfo get(String key) {
        return ctxMap.get(key);
    }

    public InfixFieldInfo remove(String key) {
        return ctxMap.remove(key);
    }

    public Set<String> keySet() {
        return ctxMap.keySet();
    }

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
