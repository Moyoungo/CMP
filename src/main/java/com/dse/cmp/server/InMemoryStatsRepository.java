package com.dse.cmp.server;

import com.dse.cmp.model.StatsRecord;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/** In-memory StatsRepository keyed by "sid|ds". */
public final class InMemoryStatsRepository implements StatsRepository {
    private final Map<String, StatsRecord> map = new ConcurrentHashMap<>();

    @Override
    public StatsRecord getOrCreate(String sid, String ds, int d, int m) {
        String key = sid+"|"+ds;
        return map.computeIfAbsent(key, k -> new StatsRecord(sid, ds, d, m));
    }
}
