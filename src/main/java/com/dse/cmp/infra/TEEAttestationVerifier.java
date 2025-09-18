package com.dse.cmp.infra;

/** Interface for verifying TEE remote attestations. */
public interface TEEAttestationVerifier {
    /** Returns true if attestation is valid and policy-compliant. */
    boolean verify(byte[] attestationReport);
}
