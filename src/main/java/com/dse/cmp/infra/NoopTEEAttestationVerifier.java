package com.dse.cmp.infra;

/** No-op verifier that always accepts (placeholder). */
public final class NoopTEEAttestationVerifier implements TEEAttestationVerifier {
    @Override public boolean verify(byte[] attestationReport){ return true; }
}
