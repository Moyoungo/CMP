package com.dse.cmp.client;

/** Local store for per-(sid,ds) client state bytes ST_t. */
public interface ClientStateStore {
    byte[] getState(String sid, String ds);
    void setState(String sid, String ds, byte[] ST);
}
