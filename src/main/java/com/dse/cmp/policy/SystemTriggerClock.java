package com.dse.cmp.policy;

/** System clock implementation of TriggerClock. */
public final class SystemTriggerClock implements TriggerClock {
    @Override public long nowMs() { return System.currentTimeMillis(); }
}
