package com.dse.cmp.cmp;

import com.dse.cmp.model.UpdateBatch;
import com.dse.cmp.model.StatsRecord;
import com.dse.cmp.model.SimilarityResult;
import com.dse.cmp.rff.RFF;
import com.dse.cmp.server.StatsRepository;
import com.dse.cmp.policy.TriggerPolicy;
import com.dse.cmp.policy.SystemTriggerClock;
import com.dse.cmp.similarity.SimilarityConfig;
import com.dse.cmp.similarity.SimilarityEngine;

import java.util.List;

/** CMP core service: ingest updates, apply trigger policy, and compute similarity. */
public final class CMPService {
    private final int d, m;
    private final RFF rff; // optional
    private final StatsRepository statsRepo;
    private final TriggerPolicy triggerPolicy;
    private final SystemTriggerClock clock;
    private final SimilarityEngine engine;

    public CMPService(int d, int m, float[] gammaList, long rffSeed,
                      StatsRepository statsRepo, TriggerPolicy triggerPolicy, SimilarityConfig cfg) {
        this.d = d; this.m = m;
        this.rff = (m>0? new RFF(d, m, gammaList, rffSeed): null);
        this.statsRepo = statsRepo;
        this.triggerPolicy = triggerPolicy;
        this.clock = new SystemTriggerClock();
        this.engine = new SimilarityEngine(cfg);
    }

    /** Apply a batch update to (sid,ds), creating record if needed. */
    public void applyUpdate(UpdateBatch batch) {
        StatsRecord st = statsRepo.getOrCreate(batch.sid(), batch.ds(), d, m);
        st.applyBatchUpdate(batch.adds(), batch.dels(), rff);
        // Trigger policy is evaluated lazily via maybeTriggerRecompute
    }

    /** Evaluate and, if needed, refresh baselines for (sid,ds). */
    public boolean maybeTriggerRecompute(String sid, String ds) {
        StatsRecord st = statsRepo.getOrCreate(sid, ds, d, m);
        boolean fire = triggerPolicy.shouldRecompute(st, clock);
        if (fire) triggerPolicy.onRecompute(st, clock);
        return fire;
    }

    /** Compute similarity between two datasets. */
    public SimilarityResult similarity(String sidA, String dsA, String sidB, String dsB) {
        StatsRecord A = statsRepo.getOrCreate(sidA, dsA, d, m);
        StatsRecord B = statsRepo.getOrCreate(sidB, dsB, d, m);
        return engine.compare(A, B);
    }

    public SimilarityEngine engine(){ return engine; }

    public java.util.List<PairwiseSim> pairwise(java.util.List<DatasetRef> refs){
        java.util.List<PairwiseSim> out = new java.util.ArrayList<>();
        for (int i=0;i<refs.size();i++){
            for (int j=i+1;j<refs.size();j++){
                DatasetRef A = refs.get(i), B = refs.get(j);
                out.add(new PairwiseSim(A, B, similarity(A.sid(), A.ds(), B.sid(), B.ds())));
            }
        }
        return out;
    }

    /** 对 refs 这组数据集做“两两比较”，返回所有成对结果（如 A,B,C → AB、AC、BC）。 */
    public java.util.List<PairwiseSim> pairwiseAll(java.util.List<DatasetRef> refs){
        java.util.List<PairwiseSim> out = new java.util.ArrayList<>();
        for (int i=0;i<refs.size();i++){
            for (int j=i+1;j<refs.size();j++){
                DatasetRef A = refs.get(i), B = refs.get(j);
                var r = similarity(A.sid(), A.ds(), B.sid(), B.ds());
                out.add(new PairwiseSim(A, B, r));
            }
        }
        return out;
    }

    /** 只比较你点名的这些“数据集对”。 */
    public java.util.List<PairwiseSim> pairwisePairs(java.util.List<DatasetPair> pairs){
        java.util.List<PairwiseSim> out = new java.util.ArrayList<>();
        for (DatasetPair p : pairs) {
            DatasetRef A = p.a(), B = p.b();
            var r = similarity(A.sid(), A.ds(), B.sid(), B.ds());
            out.add(new PairwiseSim(A, B, r));
        }
        return out;
    }




}
