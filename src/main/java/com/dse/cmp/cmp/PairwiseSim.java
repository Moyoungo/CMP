package com.dse.cmp.cmp;

import com.dse.cmp.model.SimilarityResult;

public final class PairwiseSim {
    public final DatasetRef a, b;
    public final SimilarityResult result;
    public PairwiseSim(DatasetRef a, DatasetRef b, SimilarityResult result){
        this.a=a; this.b=b; this.result=result;
    }
    @Override public String toString(){
        return "PairwiseSim{" + a + " vs " + b + " -> " + result + "}";
    }
}