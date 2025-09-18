package com.dse.cmp.rff;

import java.util.Random;

/** Random Fourier Features for RBF kernel with multiple bandwidths. */
public final class RFF {
    public final int d;
    public final int m;
    public final int bands;
    public final float[][] omega; // [bands][m_per_band * d]
    public final float[][] bias;  // [bands][m_per_band]
    public final float[] scale;   // sqrt(2/m) per band
    public final float[] gamma;   // bandwidths
    public final int mPerBand;
    final Random rng;

    public RFF(int d, int m, float[] gammaList, long seed) {
        this.d=d; this.m=m; this.bands=gammaList.length; this.gamma=gammaList.clone();
        if (m % bands != 0) throw new IllegalArgumentException("m must be divisible by number of bands");
        this.mPerBand = m / bands;
        this.omega = new float[bands][];
        this.bias  = new float[bands][];
        this.scale = new float[bands];
        this.rng = new Random(seed);
        for (int b=0;b<bands;b++) {
            omega[b] = new float[mPerBand * d];
            bias[b]  = new float[mPerBand];
            double std = Math.sqrt(2.0*gammaList[b]); // ω ~ N(0, 2γ I)
            for (int j=0;j<mPerBand*d;j++) omega[b][j] = (float)(rng.nextGaussian()*std);
            for (int j=0;j<mPerBand;j++) bias[b][j] = (float)(rng.nextDouble()*2*Math.PI);
            scale[b] = (float)Math.sqrt(2.0/m);
        }
    }

    /** φ(x) size = m (concatenating all bands). */
    public float[] phi(float[] x) {
        float[] out = new float[m];
        int pos=0;
        for (int b=0;b<bands;b++) {
            float[] wb = omega[b]; float[] bb = bias[b]; float sc = scale[b];
            for (int j=0;j<mPerBand;j++) {
                int base = j*d; double dot=0;
                for (int k=0;k<d;k++) dot += wb[base+k]*x[k];
                out[pos++] = (float)(sc*Math.cos(dot + bb[j]));
            }
        }
        return out;
    }
}
