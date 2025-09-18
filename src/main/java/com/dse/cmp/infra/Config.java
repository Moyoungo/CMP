package com.dse.cmp.infra;

/** Central configuration values for CMP. */
public final class Config {
    public final int d;           // embedding dimension
    public final int m;           // RFF feature size (0 disables MMD)
    public final float[] gamma;   // RBF gammas
    public final double scaleThresh;
    public final double tauRel;
    public final double tauAbs;
    public final long minIntervalMs;

    public Config(int d, int m, float[] gamma,
                  double scaleThresh, double tauRel, double tauAbs, long minIntervalMs) {
        this.d=d; this.m=m; this.gamma=gamma;
        this.scaleThresh=scaleThresh; this.tauRel=tauRel; this.tauAbs=tauAbs; this.minIntervalMs=minIntervalMs;
    }

    public static Config defaults(int d) {
        return new Config(d, 256, new float[]{0.5f,1.0f,2.0f}, 0.01, 0.005, 0.05, 10*60*1000L);
    }
}
