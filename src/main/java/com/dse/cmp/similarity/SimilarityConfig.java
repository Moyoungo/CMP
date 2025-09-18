package com.dse.cmp.similarity;

import com.dse.cmp.util.Preconditions;

/** Tunable weights/decays for fusion of metrics. */
public final class SimilarityConfig {
    public final double wCos, wEuc, wKL, wMMD;
    public final boolean useMMD;
    public final double alphaEuc, alphaKL, alphaMMD; // decay scales in exp(-alpha * dist)

    public SimilarityConfig(double wCos, double wEuc, double wKL, double wMMD,
                            boolean useMMD, double alphaEuc, double alphaKL, double alphaMMD) {
        Preconditions.checkArgument(wCos>=0 && wEuc>=0 && wKL>=0 && wMMD>=0, "weights must be >=0");
        Preconditions.checkArgument(alphaEuc>0 && alphaKL>0 && alphaMMD>0, "alphas must be >0");
        this.wCos=wCos; this.wEuc=wEuc; this.wKL=wKL; this.wMMD=wMMD;
        this.useMMD=useMMD; this.alphaEuc=alphaEuc; this.alphaKL=alphaKL; this.alphaMMD=alphaMMD;
    }

    public static SimilarityConfig defaultsWithMMD() {
        return new SimilarityConfig(0.4, 0.25, 0.25, 0.10, true, 1.0, 1.0, 1.0);
    }
    public static SimilarityConfig defaultsNoMMD() {
        return new SimilarityConfig(0.5, 0.3, 0.2, 0.0, false, 1.0, 1.0, 1.0);
    }
}
