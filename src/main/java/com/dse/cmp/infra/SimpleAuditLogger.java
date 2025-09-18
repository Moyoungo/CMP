package com.dse.cmp.infra;

/** Simple stdout audit logger. Replace with production logger as needed. */
public final class SimpleAuditLogger implements AuditLogger {
    @Override public void info(String event, String details){ System.out.println("[AUDIT][INFO] "+event+" | "+details); }
    @Override public void warn(String event, String details){ System.out.println("[AUDIT][WARN] "+event+" | "+details); }
    @Override public void error(String event, String details){ System.err.println("[AUDIT][ERROR] "+event+" | "+details); }
}
