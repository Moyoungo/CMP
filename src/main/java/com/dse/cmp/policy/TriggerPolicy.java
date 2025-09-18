package com.dse.cmp.policy;

import com.dse.cmp.model.StatsRecord;
import com.dse.cmp.util.VecOps;

/** Recompute triggers based on scale (count change) and distribution drift (mean shift). */
public final class TriggerPolicy {
    private final double scaleThresh;   // e.g., 0.01  (1% relative change)
    private final double tauRel;        // e.g., 0.005 (0.5% relative mean shift)
    private final double tauAbs;        // e.g., 0.05  (absolute floor for mean shift)
    private final long minIntervalMs;   // e.g., 30min
    private long lastRecomputeAt = 0L;

    public TriggerPolicy(double scaleThresh, double tauRel, double tauAbs, long minIntervalMs) {
        this.scaleThresh = scaleThresh;
        this.tauRel = tauRel;
        this.tauAbs = tauAbs;
        this.minIntervalMs = minIntervalMs;
    }

    public boolean shouldRecompute(StatsRecord st, TriggerClock clock) {
        long now = clock.nowMs();
        if (now - lastRecomputeAt < minIntervalMs) return false;
        boolean scale = st.n0>0 && Math.abs(st.n - st.n0) >= st.n0*scaleThresh;
        float[] dmu = com.dse.cmp.util.VecOps.copy(st.mu);
        VecOps.subInPlace(dmu, st.muBaseline);
        double l2 = Math.sqrt(VecOps.norm2(dmu));
        double base = Math.sqrt(Math.max(VecOps.norm2(st.muBaseline), 1e-24));
        boolean drift = (l2/base) >= tauRel || l2 >= tauAbs;
        return scale || drift;
    }

    public void onRecompute(StatsRecord st, TriggerClock clock) {
        System.arraycopy(st.mu, 0, st.muBaseline, 0, st.d);
        st.n0 = st.n;
        lastRecomputeAt = clock.nowMs();
    }
}
