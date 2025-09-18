package com.dse.cmp.app;

import com.dse.cmp.auth.*;
import com.dse.cmp.datasource.*;
import com.dse.cmp.server.*;
import com.dse.cmp.client.*;
import com.dse.cmp.cmp.*;
import com.dse.cmp.policy.*;
import com.dse.cmp.similarity.*;
import com.dse.cmp.model.*;

import java.util.*;

/** Demo main to exercise the Pro-version system end-to-end. */
public final class DemoMainPro {
    public static void main(String[] args) {
        final int d = 8;
        final int m = 6;
        float[] gamma = new float[]{0.5f, 1.0f, 2.0f};

        StateTableRepository stateRepo = new InMemoryStateTableRepository();
        IndexRepository indexRepo = new InMemoryIndexRepository();
        StatsRepository statsRepo = new InMemoryStatsRepository();

        TriggerPolicy policy = new TriggerPolicy(0.01, 0.005, 0.05, 0L);
        SimilarityConfig simCfg = SimilarityConfig.defaultsWithMMD();
        CMPService cmp = new CMPService(d, m, gamma, 42L, statsRepo, policy, simCfg);

        Contract contract = new Contract("contract-1");
        KeyDerivationService kdf = new KeyDerivationService();
        AuthorizationService authz = new AuthorizationService(kdf);

        DataSource dsA = new DataSource("A");
        String dsName = "cat";

        byte[] ST5 = authz.issueStartState();
        byte[] K_label = authz.issueLabelKey(contract, dsA.sid(), dsName);

        ClientService client = new ClientService("client-1", contract.contractId(), contract.alpha(), new InMemoryClientStateStore());
        client.registerDataset(dsA.sid(), dsName, K_label, ST5);

        DataSourceService dss = new DataSourceService();
        int t = 5;

        byte[] ST6 = dss.newState();
        dss.commitNext(contract.contractId(), contract.alpha(), dsA.sid(), dsName, ST5, ST6, stateRepo);
        Map<String, List<String>> p6 = Map.of("rabbit", List.of("docA6-1","docA6-2"), "drug", List.of("docA6-3"));
        for (var e : p6.entrySet()) {
            byte[] label = dss.buildLabel(dsA.sid(), dsName, ST6, K_label, e.getKey());
            for (String docId : e.getValue()) indexRepo.addPosting(dsA.sid(), dsName, t+1, label, docId);
        }
        boolean adv6 = client.advance(dsA.sid(), dsName, stateRepo);
        System.out.println("Advance to 6 -> " + adv6);
        byte[] token6 = client.buildToken(dsA.sid(), dsName, "rabbit");
        var hits6 = indexRepo.search(dsA.sid(), dsName, t+1, token6);
        System.out.println("Hits at 6: " + hits6.size());

        byte[] ST7 = dss.newState();
        dss.commitNext(contract.contractId(), contract.alpha(), dsA.sid(), dsName, ST6, ST7, stateRepo);
        Map<String, List<String>> p7 = Map.of("rabbit", List.of("docA7-1"));
        for (var e : p7.entrySet()) {
            byte[] label = dss.buildLabel(dsA.sid(), dsName, ST7, K_label, e.getKey());
            for (String docId : e.getValue()) indexRepo.addPosting(dsA.sid(), dsName, t+2, label, docId);
        }
        boolean adv7 = client.advance(dsA.sid(), dsName, stateRepo);
        System.out.println("Advance to 7 -> " + adv7);
        byte[] token7 = client.buildToken(dsA.sid(), dsName, "rabbit");
        var hits7 = indexRepo.search(dsA.sid(), dsName, t+2, token7);
        System.out.println("Hits at 7: " + hits7.size());

        byte[] ST8 = dss.newState();
        dss.commitNext(contract.contractId(), contract.alpha(), dsA.sid(), dsName, ST7, ST8, stateRepo);
        Map<String, List<String>> p8 = Map.of("rabbit", List.of("docA8-1"));
        for (var e : p8.entrySet()) {
            byte[] label = dss.buildLabel(dsA.sid(), dsName, ST8, K_label, e.getKey());
            for (String docId : e.getValue()) indexRepo.addPosting(dsA.sid(), dsName, t+3, label, docId);
        }
        dss.commitNext(contract.contractId(), contract.alpha(), dsA.sid(), dsName, ST8, null, stateRepo);

        boolean adv8 = client.advance(dsA.sid(), dsName, stateRepo);
        System.out.println("Advance to 8 -> " + adv8);
        byte[] token8 = client.buildToken(dsA.sid(), dsName, "rabbit");
        var hits8 = indexRepo.search(dsA.sid(), dsName, t+3, token8);
        System.out.println("Hits at 8: " + hits8.size());

        boolean adv9 = client.advance(dsA.sid(), dsName, stateRepo);
        System.out.println("Advance beyond 8 -> " + adv9 + " (expected false)");

        List<float[]> addA = new ArrayList<>();
        List<float[]> addB = new ArrayList<>();
        for (int i=0;i<500;i++) {
            float[] vA = new float[d], vB = new float[d];
            for (int k=0;k<d;k++){ vA[k] = (float)Math.random(); vB[k] = (float)Math.random(); }
            addA.add(vA); addB.add(vB);
        }
        cmp.applyUpdate(UpdateBatch.addOnly("A", dsName, addA, System.currentTimeMillis(), t+1));
        cmp.applyUpdate(UpdateBatch.addOnly("B", dsName, addB, System.currentTimeMillis(), t+1));
        boolean fired = cmp.maybeTriggerRecompute("A", dsName);
        System.out.println("Trigger recompute for A? " + fired);
        SimilarityResult sim = cmp.similarity("A", dsName, "B", dsName);
        System.out.println("Similarity: " + sim);
    }
}
