package com.dse.cmp.auth;

import com.dse.cmp.util.CryptoUtils;

/** Centralized derivations for dataset label keys and other PRFs. */
public final class KeyDerivationService {
    public byte[] deriveLabelKey(byte[] labelMasterKey, String sid, String ds) {
        return CryptoUtils.HmacSHA256(labelMasterKey, CryptoUtils.str(sid), CryptoUtils.str("|"), CryptoUtils.str(ds));
    }
}
