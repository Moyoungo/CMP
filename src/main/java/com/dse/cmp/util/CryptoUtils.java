package com.dse.cmp.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

/** Cryptographic helpers (hash/HMAC/PRF/xor/random). */
public final class CryptoUtils {
    private CryptoUtils() {}

    /** H1: SHA-256 over concatenated parts. */
    public static byte[] H1(byte[]... parts) { return sha256(Bytes.concat(parts)); }
    /** H2: SHA-256 over concatenated parts (separate label to reduce misuse risk). */
    public static byte[] H2(byte[]... parts) { return sha256(Bytes.concat(parts)); }

    public static byte[] HmacSHA256(byte[] key, byte[]... parts) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            for (byte[] p : parts) mac.update(p);
            return mac.doFinal();
        } catch (GeneralSecurityException e) { throw new RuntimeException(e); }
    }

    public static byte[] sha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(data);
        } catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }
    }

    public static byte[] str(String s) { return s.getBytes(StandardCharsets.UTF_8); }

    public static byte[] i32(int x) {
        return ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(x).array();
    }

    public static byte[] xor(byte[] a, byte[] b) {
        if (a.length != b.length) throw new IllegalArgumentException("xor length mismatch");
        byte[] r = new byte[a.length];
        for (int i=0;i<a.length;i++) r[i] = (byte)(a[i]^b[i]);
        return r;
    }

    public static byte[] random(int len) {
        byte[] r = new byte[len];
        new SecureRandom().nextBytes(r);
        return r;
    }
}
