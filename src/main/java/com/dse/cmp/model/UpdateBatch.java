package com.dse.cmp.model;

import java.util.List;
import java.util.Objects;

/** A batch update consisting of added and/or deleted embedding vectors for a given (sid, ds). */
public final class UpdateBatch {
    private final String sid;
    private final String ds;
    private final List<float[]> adds;
    private final List<float[]> dels;
    private final long timestampMs;
    private final Integer t; // optional logical epoch

    public UpdateBatch(String sid, String ds, List<float[]> adds, List<float[]> dels, long timestampMs, Integer t) {
        this.sid = Objects.requireNonNull(sid, "sid");
        this.ds  = Objects.requireNonNull(ds, "ds");
        this.adds = adds==null? List.of() : List.copyOf(adds);
        this.dels = dels==null? List.of() : List.copyOf(dels);
        this.timestampMs = timestampMs;
        this.t = t;
        if (this.adds.isEmpty() && this.dels.isEmpty()) {
            throw new IllegalArgumentException("UpdateBatch must have at least one add or delete");
        }
    }

    public static UpdateBatch addOnly(String sid, String ds, List<float[]> adds, long ts, Integer t) {
        return new UpdateBatch(sid, ds, adds, List.of(), ts, t);
    }
    public static UpdateBatch delOnly(String sid, String ds, List<float[]> dels, long ts, Integer t) {
        return new UpdateBatch(sid, ds, List.of(), dels, ts, t);
    }

    public String sid(){ return sid; }
    public String ds(){ return ds; }
    public List<float[]> adds(){ return adds; }
    public List<float[]> dels(){ return dels; }
    public long timestampMs(){ return timestampMs; }
    public Integer epoch(){ return t; }

    public int addCount(){ return adds.size(); }
    public int delCount(){ return dels.size(); }
}
