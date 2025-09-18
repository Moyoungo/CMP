package com.dse.cmp.auth;

import com.dse.cmp.util.CryptoUtils;

/** Per-contract secret alpha + master key for dataset label keys. */
public final class Contract {
    private final String contractId;
    private final byte[] alpha;                 // per-contract secret (CMP + datasource + client)
    private final byte[] labelMasterKey;       // derive per-(sid,ds) K_label

    public Contract(String contractId) {
        this.contractId = contractId;
        this.alpha = CryptoUtils.random(32);
        this.labelMasterKey = CryptoUtils.random(32);
    }

    public String contractId(){ return contractId; }
    public byte[] alpha(){ return alpha; }
    public byte[] labelMasterKey(){ return labelMasterKey; }
}
