package com.dse.cmp.server;

import com.dse.cmp.util.CryptoUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory implementation of StateTableRepository with breakpoint sentinel. */
public final class InMemoryStateTableRepository implements StateTableRepository {

    /** ConcurrentHashMap 不允许 null，这里用 0 长度字节作为“断点(⊥)”哨兵。 */
    private static final byte[] NULL_SENTINEL = new byte[0];

    private final Map<String, byte[]> map = new ConcurrentHashMap<>();

    @Override
    public void put(String contractId, String sid, String ds, byte[] ST_t, byte[] SK_t) {
        final String k = key(contractId, sid, ds, ST_t);
        // 断点用哨兵存；正常值直接存
        map.put(k, (SK_t == null ? NULL_SENTINEL : SK_t));
    }

    @Override
    public byte[] getSK(String contractId, String sid, String ds, byte[] ST_t) {
        final String k = key(contractId, sid, ds, ST_t);
        byte[] v = map.get(k);
        if (v == null) return null;                 // 未发布该 ST 的条目
        if (v == NULL_SENTINEL || v.length == 0)    // 命中断点
            return null;
        return v;
    }

    private String key(String contractId, String sid, String ds, byte[] ST_t) {
        byte[] k = CryptoUtils.H1(
                CryptoUtils.str(contractId), CryptoUtils.str("|"),
                CryptoUtils.str(sid), CryptoUtils.str("|"),
                CryptoUtils.str(ds), CryptoUtils.str("|"), ST_t
        );
        return com.dse.cmp.util.Bytes.toHex(k);
    }
}
