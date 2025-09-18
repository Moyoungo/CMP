package com.dse.cmp.model;

/** DTO for similarity results (individual metrics + fused score). */
public final class SimilarityResult {
    public final double cosineAvg;     // [-1,1]
    public final double expSqEuclidean;
    public final double symmetricKL;
    public final double mmd2;          // ≈ MMD^2 (RFF approximation)
    public final double fused;         // [0,1]

    public SimilarityResult(double cosineAvg, double expSqEuclidean, double symmetricKL, double mmd2, double fused) {
        this.cosineAvg = cosineAvg;
        this.expSqEuclidean = expSqEuclidean;
        this.symmetricKL = symmetricKL;
        this.mmd2 = mmd2;
        this.fused = fused;
    }

    public double fusedPercent(){ return fused * 100.0; }

    @Override public String toString() {
        return String.format("cos=%.4f euc=%.4f kl=%.4f mmd2=%.4f fused=%.4f",
                cosineAvg, expSqEuclidean, symmetricKL, mmd2, fused);
    }
}
