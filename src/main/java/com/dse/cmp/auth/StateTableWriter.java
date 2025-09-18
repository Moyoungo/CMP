package com.dse.cmp.auth;

/** Minimal interface to write SK_t entries. Implemented by server-side repository. */
public interface StateTableWriter {
    void put(String contractId, String sid, String ds, byte[] ST_t, byte[] SK_t);
}
