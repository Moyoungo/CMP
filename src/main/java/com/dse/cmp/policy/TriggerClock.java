package com.dse.cmp.policy;

/** Abstraction for time used by TriggerPolicy, allowing deterministic tests. */
public interface TriggerClock {
    long nowMs();
}
