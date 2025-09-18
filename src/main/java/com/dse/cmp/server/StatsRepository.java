package com.dse.cmp.server;

import com.dse.cmp.model.StatsRecord;

/** Repository for per-(sid,ds) StatsRecord. */
public interface StatsRepository {
    StatsRecord getOrCreate(String sid, String ds, int d, int m);
}
