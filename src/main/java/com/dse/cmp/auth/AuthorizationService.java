package com.dse.cmp.auth;

import com.dse.cmp.util.CryptoUtils;
import com.dse.cmp.util.Preconditions;

/** Issues initial state to client and provides per-(sid,ds) label key via KDF. */
public final class AuthorizationService {
    private final KeyDerivationService kdf;

    public AuthorizationService(KeyDerivationService kdf) {
        this.kdf = Preconditions.checkNotNull(kdf, "kdf");
    }

    /** Issue a fresh starting state ST_t0 for a dataset to a client under a contract. */
    public byte[] issueStartState() {
        return CryptoUtils.random(32);
    }

    /** Derive K_label(sid,ds) under the contract's label master key. */
    public byte[] issueLabelKey(Contract contract, String sid, String ds) {
        return kdf.deriveLabelKey(contract.labelMasterKey(), sid, ds);
    }
}
