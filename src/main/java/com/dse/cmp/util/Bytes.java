package com.dse.cmp.util;

/** Bytes helpers: hex encode/decode and concatenation. */
public final class Bytes {
    private Bytes(){}
    public static String toHex(byte[] x) {
        StringBuilder sb = new StringBuilder();
        for (byte b : x) sb.append(String.format("%02x", b));
        return sb.toString();
    }
    public static byte[] fromHex(String hex) {
        String h = hex.startsWith("0x")? hex.substring(2): hex;
        int len = h.length();
        if (len % 2 != 0) throw new IllegalArgumentException("hex length must be even");
        byte[] out = new byte[len/2];
        for (int i=0;i<out.length;i++) {
            int hi = Character.digit(h.charAt(2*i),16);
            int lo = Character.digit(h.charAt(2*i+1),16);
            if (hi<0 || lo<0) throw new IllegalArgumentException("non-hex char");
            out[i] = (byte)((hi<<4)|lo);
        }
        return out;
    }
    public static byte[] concat(byte[]... parts) {
        int len=0; for (byte[] p: parts) len += p.length;
        byte[] out = new byte[len];
        int pos=0; for (byte[] p: parts) { System.arraycopy(p,0,out,pos,p.length); pos+=p.length; }
        return out;
    }
}
