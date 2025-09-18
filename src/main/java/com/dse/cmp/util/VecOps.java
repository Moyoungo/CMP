package com.dse.cmp.util;

import java.util.Arrays;

/** Vector operations (float-based) optimized for clarity and stability. */
public final class VecOps {
    private VecOps(){}
    public static float dot(float[] a, float[] b) {
        if (a.length!=b.length) throw new IllegalArgumentException("dot: dim mismatch");
        double s=0; for (int i=0;i<a.length;i++) s+=(double)a[i]*b[i]; return (float)s;
    }
    public static void addInPlace(float[] a, float[] b) { for (int i=0;i<a.length;i++) a[i]+=b[i]; }
    public static void subInPlace(float[] a, float[] b) { for (int i=0;i<a.length;i++) a[i]-=b[i]; }
    public static float norm2(float[] a) { return dot(a,a); }
    public static float[] copy(float[] a) { return Arrays.copyOf(a, a.length); }
    public static float[] zeros(int d) { return new float[d]; }
    public static void axpy(float[] y, float alpha, float[] x) { for (int i=0;i<y.length;i++) y[i]+=alpha*x[i]; }
    public static float sum(float[] a) { double s=0; for (float v:a) s+=v; return (float)s; }
    public static void elemSqrAddInPlace(float[] S, float[] x) { for (int i=0;i<S.length;i++) S[i] += x[i]*x[i]; }
    public static void elemSqrSubInPlace(float[] S, float[] x) { for (int i=0;i<S.length;i++) S[i] -= x[i]*x[i]; }
    public static void l2normalizeInPlace(float[] x) { double n = Math.sqrt(Math.max(dot(x,x), 0)); if (n>0) for (int i=0;i<x.length;i++) x[i]/=n; }
    public static void clipMinInPlace(float[] x, float lo) { for (int i=0;i<x.length;i++) if (x[i]<lo) x[i]=lo; }
}
