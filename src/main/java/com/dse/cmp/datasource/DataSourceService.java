package com.dse.cmp.datasource;

import com.dse.cmp.server.StateTableRepository;
import com.dse.cmp.util.CryptoUtils;

/** 数据源侧：本地生成下一个状态，并把“当前状态”的表项提交给服务端。 */
public final class DataSourceService {

    /** 数据源本地生成一个新状态 ST_{t+1}（32字节随机）。 */
    public byte[] newState() { return CryptoUtils.random(32); }

    /**
     * 提交“当前状态 ST_t 的表项”。若 ST_nextOrNull == null，表示断点（值为空）。
     * 否则写入 SK_t = ST_{t+1} XOR H2(alpha || sid || ds || ST_t)。
     * 其中 alpha 为“每合同一个 α”，提供 FP-with-server 的掩码能力。
     */
    public void commitNext(String contractId, byte[] alpha, String sid, String ds,
                           byte[] ST_t, byte[] ST_nextOrNull, StateTableRepository writer) {
        byte[] SK_t = (ST_nextOrNull == null)
                ? null   // 断点：哈希表“键=ST_t”的值为空（内存实现用哨兵存储）
                : CryptoUtils.xor(
                ST_nextOrNull,
                CryptoUtils.H2(alpha,
                        CryptoUtils.str(sid), CryptoUtils.str("|"),
                        CryptoUtils.str(ds),  CryptoUtils.str("|"), ST_t)
        );
        writer.put(contractId, sid, ds, ST_t, SK_t);
    }

    /** 生成索引标签/查询令牌：H( sid||ds||HMAC(K_label,w)||H1(ST_t) )。 */
    public byte[] buildLabel(String sid, String ds, byte[] ST_t, byte[] K_label, String keyword) {
        byte[] kw = CryptoUtils.HmacSHA256(K_label, CryptoUtils.str(keyword));
        byte[] stPtr = CryptoUtils.H1(ST_t);
        return CryptoUtils.H1(
                CryptoUtils.str(sid), CryptoUtils.str("|"), CryptoUtils.str(ds),
                CryptoUtils.str("|"), kw, CryptoUtils.str("|"), stPtr);
    }
}
