package com.dse.cmp.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/** In-memory implementation of ClientStateStore. */
public final class InMemoryClientStateStore implements ClientStateStore {
    private final Map<String, byte[]> map = new ConcurrentHashMap<>();

    @Override public byte[] getState(String sid, String ds) { return map.get(key(sid,ds)); }
    @Override public void setState(String sid, String ds, byte[] ST) { map.put(key(sid,ds), ST); }

    private String key(String sid, String ds){ return sid+"|"+ds; }
}
