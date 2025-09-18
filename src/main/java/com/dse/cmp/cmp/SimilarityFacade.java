package com.dse.cmp.cmp;

import com.dse.cmp.model.SimilarityResult;

/** Optional facade for external callers, wrapping CMPService. */
public final class SimilarityFacade {
    private final CMPService cmp;
    public SimilarityFacade(CMPService cmp){ this.cmp = cmp; }
    public SimilarityResult compare(String sidA, String dsA, String sidB, String dsB) {
        return cmp.similarity(sidA, dsA, sidB, dsB);
    }
}
