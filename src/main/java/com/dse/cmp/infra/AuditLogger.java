package com.dse.cmp.infra;

/** Audit logger interface for security-/billing-relevant events. */
public interface AuditLogger {
    void info(String event, String details);
    void warn(String event, String details);
    void error(String event, String details);
}
