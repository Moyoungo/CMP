package com.dse.cmp.util;

/** Minimal preconditions utility for argument/state validation. */
public final class Preconditions {
    private Preconditions(){}
    public static <T> T checkNotNull(T ref, String msg) {
        if (ref == null) throw new IllegalArgumentException(msg);
        return ref;
    }
    public static void checkArgument(boolean cond, String msg) {
        if (!cond) throw new IllegalArgumentException(msg);
    }
    public static void checkState(boolean cond, String msg) {
        if (!cond) throw new IllegalStateException(msg);
    }
}
