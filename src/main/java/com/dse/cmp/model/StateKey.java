package com.dse.cmp.model;

import com.dse.cmp.util.Bytes;
import com.dse.cmp.util.CryptoUtils;
import java.util.Arrays;
import java.util.Objects;

/** Value object representing (contractId, sid, ds, ST), with helpers to compute server map key. */
public final class StateKey {
    private final String contractId;
    private final String sid;
    private final String ds;
    private final byte[] ST; // raw state bytes

    public StateKey(String contractId, String sid, String ds, byte[] ST) {
        this.contractId = Objects.requireNonNull(contractId, "contractId");
        this.sid = Objects.requireNonNull(sid, "sid");
        this.ds  = Objects.requireNonNull(ds, "ds");
        this.ST  = Objects.requireNonNull(ST, "ST");
    }

    public String contractId(){ return contractId; }
    public String sid(){ return sid; }
    public String ds(){ return ds; }
    public byte[] ST(){ return ST; }

    /** Server map key: H1(contractId||sid||ds||ST) encoded as hex. */
    public String serverMapKeyHex() {
        byte[] k = CryptoUtils.H1(CryptoUtils.str(contractId), CryptoUtils.str("|"),
                                  CryptoUtils.str(sid), CryptoUtils.str("|"),
                                  CryptoUtils.str(ds), CryptoUtils.str("|"), ST);
        return Bytes.toHex(k);
    }

    @Override public boolean equals(Object o){
        if (this==o) return true;
        if (!(o instanceof StateKey)) return false;
        StateKey x = (StateKey)o;
        return contractId.equals(x.contractId) && sid.equals(x.sid) && ds.equals(x.ds) && Arrays.equals(ST, x.ST);
    }
    @Override public int hashCode(){
        int h = Objects.hash(contractId, sid, ds);
        h = 31*h + Arrays.hashCode(ST);
        return h;
    }
    @Override public String toString(){
        return "StateKey{contractId='"+contractId+"', sid='"+sid+"', ds='"+ds+"', ST.len="+ST.length+"}";
    }
}
