package com.dse.cmp.server;

import com.dse.cmp.auth.StateTableWriter;

/** Repository for server-side state table: getSK + put(SK or ⊥). */
public interface StateTableRepository extends StateTableWriter {
    byte[] getSK(String contractId, String sid, String ds, byte[] ST_t);
}
