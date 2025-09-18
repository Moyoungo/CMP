package com.dse.cmp.model;

import com.dse.cmp.util.VecOps;
import com.dse.cmp.rff.RFF;
import java.util.Arrays;
import java.util.List;

/** Per-(sid,ds) statistics with incremental updates and caches for fast similarity computation. */
public final class StatsRecord {
    public final String sid;
    public final String ds;
    public final int d;
    public final int m;

    public long n;                  // count
    public final float[] s;         // sum x
    public final float[] S;         // sum (x ⊙ x)
    public final float[] tildeS;    // sum (x / ||x||)
    public final float[] sPhi;      // sum φ(x) [optional]

    // caches
    public final float[] mu;        // s / n
    public float muNorm2;           // ||mu||^2
    public float sumS_over_n;       // sum(S) / n
    public final float[] sigma2;    // diag variance
    public final float[] sigma2_shrink, log_sigma2_shrink, inv_sigma2_shrink;

    // baselines for trigger (not used here, but kept for completeness)
    public final float[] muBaseline;
    public long n0;

    public StatsRecord(String sid, String ds, int d, int m) {
        this.sid=sid; this.ds=ds; this.d=d; this.m=m;
        this.n=0;
        this.s = VecOps.zeros(d);
        this.S = VecOps.zeros(d);
        this.tildeS = VecOps.zeros(d);
        this.sPhi = (m>0? VecOps.zeros(m): null);

        this.mu = VecOps.zeros(d);
        this.muNorm2 = 0f;
        this.sumS_over_n = 0f;
        this.sigma2 = VecOps.zeros(d);
        this.sigma2_shrink = VecOps.zeros(d);
        this.log_sigma2_shrink = VecOps.zeros(d);
        this.inv_sigma2_shrink = VecOps.zeros(d);

        this.muBaseline = VecOps.zeros(d);
        this.n0 = 0;
    }

    /** Apply a batch update with optional RFF projection for MMD. O(k·(d+m)) */
    public void applyBatchUpdate(List<float[]> adds, List<float[]> dels, RFF rff) {
        int addsN = adds==null? 0: adds.size();
        int delsN = dels==null? 0: dels.size();
        long dN = (long)addsN - (long)delsN;

        if (adds!=null) for (float[] v: adds) {
            addVector(v);
            if (sPhi!=null && rff!=null) VecOps.axpy(sPhi, 1f, rff.phi(v));
        }
        if (dels!=null) for (float[] u: dels) {
            subVector(u);
            if (sPhi!=null && rff!=null) VecOps.axpy(sPhi, -1f, rff.phi(u));
        }
        n += dN; if (n<0) n=0;
        recomputeCaches();
    }
    public void applyBatchUpdate(UpdateBatch batch, RFF rff) {
        if (!this.sid.equals(batch.sid()) || !this.ds.equals(batch.ds())) {
            throw new IllegalArgumentException("UpdateBatch (sid,ds) mismatch");
        }
        applyBatchUpdate(batch.adds(), batch.dels(), rff);
    }

    private void addVector(float[] v) {
        if (v.length!=d) throw new IllegalArgumentException("vector dim mismatch");
        VecOps.addInPlace(s, v);
        VecOps.elemSqrAddInPlace(S, v);
        float[] tmp = VecOps.copy(v); VecOps.l2normalizeInPlace(tmp); VecOps.addInPlace(tildeS, tmp);
    }
    private void subVector(float[] v) {
        if (v.length!=d) throw new IllegalArgumentException("vector dim mismatch");
        VecOps.subInPlace(s, v);
        VecOps.elemSqrSubInPlace(S, v);
        float[] tmp = VecOps.copy(v); VecOps.l2normalizeInPlace(tmp); VecOps.subInPlace(tildeS, tmp);
    }

    /** Recompute cached statistics; O(d). */
    public void recomputeCaches() {
        if (n<=0) {
            Arrays.fill(mu, 0);
            Arrays.fill(sigma2, 0);
            Arrays.fill(sigma2_shrink, 0);
            Arrays.fill(log_sigma2_shrink, 0);
            Arrays.fill(inv_sigma2_shrink, 0);
            muNorm2=0; sumS_over_n=0; return;
        }
        for (int i=0;i<d;i++) mu[i] = s[i]/(float)n;
        muNorm2 = VecOps.norm2(mu);
        float sumS=0f; for (int i=0;i<d;i++) sumS += S[i];
        sumS_over_n = sumS/(float)n;
        for (int i=0;i<d;i++) sigma2[i] = S[i]/(float)n - mu[i]*mu[i];
        for (int i=0;i<d;i++) if (sigma2[i] < 1e-6f) sigma2[i]=1e-6f;
        float meanVar= VecOps.sum(sigma2)/d;
        float lambda = (float)d / (float)(d + n); // shrinkage
        for (int i=0;i<d;i++) {
            float v = (1f-lambda)*sigma2[i] + lambda*meanVar;
            sigma2_shrink[i]=v;
            log_sigma2_shrink[i]=(float)Math.log(v);
            inv_sigma2_shrink[i]=1f/v;
        }
    }
}
