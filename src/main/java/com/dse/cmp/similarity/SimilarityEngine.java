package com.dse.cmp.similarity;

import com.dse.cmp.model.StatsRecord;
import com.dse.cmp.model.SimilarityResult;
import com.dse.cmp.util.VecOps;

/** Computes individual metrics and produces a fused similarity score. */
public final class SimilarityEngine {
    private final SimilarityConfig cfg;
    public SimilarityEngine(SimilarityConfig cfg){ this.cfg = cfg; }

    /** Average pairwise cosine using sum of normalized vectors. */
    public static double avgCosine(StatsRecord A, StatsRecord B) {
        if (A.n==0 || B.n==0) return 0.0;
        double dot = VecOps.dot(A.tildeS, B.tildeS);
        return dot / ((double)A.n * (double)B.n);
    }

    /** E[||X-Y||^2] under independent draws from A,B. */
    public static double expectedSqEuclidean(StatsRecord A, StatsRecord B) {
        if (A.n==0 || B.n==0) return Double.POSITIVE_INFINITY;
        double termA = A.sumS_over_n; double termB = B.sumS_over_n;
        double cross = VecOps.dot(A.mu, B.mu);
        return termA + termB - 2.0*cross;
    }

    /** Symmetric KL between shrinkage-diagonal Gaussians fitted to A and B. */
    public static double symmetricKL(StatsRecord A, StatsRecord B) {
        if (A.n==0 || B.n==0) return Double.POSITIVE_INFINITY;
        return 0.5*(kl(A,B) + kl(B,A));
    }
    private static double kl(StatsRecord P, StatsRecord Q) {
        double s=0;
        for (int i=0;i<P.d;i++) {
            double logRatio = Q.log_sigma2_shrink[i] - P.log_sigma2_shrink[i];
            double ratio = P.sigma2_shrink[i] * Q.inv_sigma2_shrink[i];
            double meanDiff = (P.mu[i]-Q.mu[i]);
            double quad = (meanDiff*meanDiff) * Q.inv_sigma2_shrink[i];
            s += logRatio - 1.0 + ratio + quad;
        }
        return 0.5*s;
    }

    /** MMD^2 via Random Fourier Features means, if present. */
    public static double mmdRFF(StatsRecord A, StatsRecord B) {
        if (A.sPhi==null || B.sPhi==null || A.n==0 || B.n==0) return 0.0;
        double ss=0; for (int i=0;i<A.sPhi.length;i++) {
            double v = (double)A.sPhi[i]/Math.max(A.n,1) - (double)B.sPhi[i]/Math.max(B.n,1);
            ss += v*v;
        }
        return ss;
    }

    /** Map metrics to [0,1] and fuse by weighted average. */
    public double fuse(double cos, double eucDist, double klDist, Double mmd2Opt) {
        double simCos = (cos+1.0)/2.0; // [-1,1] -> [0,1]
        double simEuc = Math.exp(-cfg.alphaEuc * eucDist);
        double simKL  = Math.exp(-cfg.alphaKL  * klDist);
        double simMMD = cfg.useMMD && mmd2Opt!=null? Math.exp(-cfg.alphaMMD * mmd2Opt) : 0.0;
        double wSum = cfg.wCos + cfg.wEuc + cfg.wKL + (cfg.useMMD? cfg.wMMD: 0.0);
        double total = cfg.wCos*simCos + cfg.wEuc*simEuc + cfg.wKL*simKL + (cfg.useMMD? cfg.wMMD*simMMD: 0.0);
        return total / wSum;
    }

    /** Convenience: compute all metrics and return DTO. */
    public SimilarityResult compare(StatsRecord A, StatsRecord B) {
        double cos = avgCosine(A,B);
        double euc = expectedSqEuclidean(A,B);
        double kl  = symmetricKL(A,B);
        Double mmd = (A.sPhi!=null && B.sPhi!=null? mmdRFF(A,B): null);
        double fused = fuse(cos, euc, kl, mmd);
        return new SimilarityResult(cos, euc, kl, mmd==null?0.0:mmd, fused);
    }
}
