package com.dse.cmp.model;

import java.util.Objects;

/** Immutable posting entry (document identifier and optional metadata). */
public final class Posting {
    private final String docId;
    public Posting(String docId) { this.docId = Objects.requireNonNull(docId, "docId"); }
    public String docId(){ return docId; }
    @Override public boolean equals(Object o){
        if (this==o) return true;
        if (!(o instanceof Posting)) return false;
        Posting p = (Posting)o;
        return docId.equals(p.docId);
    }
    @Override public int hashCode(){ return docId.hashCode(); }
    @Override public String toString(){ return "Posting{docId='"+docId+"'}"; }
}
