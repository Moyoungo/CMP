package com.dse.cmp.client;

import com.dse.cmp.server.StateTableRepository;
import com.dse.cmp.util.CryptoUtils;
import java.util.HashMap;
import java.util.Map;

/** Client-side operations: register datasets, advance states, and build search tokens. */
public final class ClientService {
    private final String clientId;
    private final String contractId;
    private final byte[] alpha; // per-contract secret
    private final ClientStateStore stateStore;
    private final Map<String, byte[]> kLabel = new HashMap<>(); // sid|ds -> K_label

    public ClientService(String clientId, String contractId, byte[] alpha, ClientStateStore stateStore) {
        this.clientId = clientId; this.contractId = contractId; this.alpha = alpha; this.stateStore = stateStore;
    }

    public void registerDataset(String sid, String ds, byte[] K_label, byte[] initialST) {
        kLabel.put(sid+"|"+ds, K_label);
        if (initialST != null) stateStore.setState(sid, ds, initialST);
    }

    /** Advance ST for (sid,ds) using server SK; returns true if advanced, false if hit breakpoint. */
    public boolean advance(String sid, String ds, StateTableRepository repo) {
        byte[] ST_t = stateStore.getState(sid, ds);
        if (ST_t == null) throw new IllegalStateException("No current state for (sid,ds); registerDataset first");
        byte[] SK_t = repo.getSK(contractId, sid, ds, ST_t);
        if (SK_t == null) return false; // breakpoint
        byte[] mask = CryptoUtils.H2(alpha, CryptoUtils.str(sid), CryptoUtils.str("|"),
                                     CryptoUtils.str(ds), CryptoUtils.str("|"), ST_t);
        byte[] ST_next = CryptoUtils.xor(SK_t, mask);
        stateStore.setState(sid, ds, ST_next);
        return true;
    }

    /** Build search token for keyword under current ST for (sid,ds). */
    public byte[] buildToken(String sid, String ds, String keyword) {
        byte[] K = kLabel.get(sid+"|"+ds);
        if (K==null) throw new IllegalStateException("No K_label registered for (sid,ds)");
        byte[] ST_t = stateStore.getState(sid, ds);
        if (ST_t == null) throw new IllegalStateException("No current state for (sid,ds)");
        byte[] kw = CryptoUtils.HmacSHA256(K, CryptoUtils.str(keyword));
        byte[] stPtr = CryptoUtils.H1(ST_t);
        return CryptoUtils.H1(CryptoUtils.str(sid), CryptoUtils.str("|"), CryptoUtils.str(ds),
                              CryptoUtils.str("|"), kw, CryptoUtils.str("|"), stPtr);
    }

    public String clientId(){ return clientId; }
    public String contractId(){ return contractId; }
}
