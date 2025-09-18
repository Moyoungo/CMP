package com.dse.cmp.datasource;

/** Simple immutable data source entity (provider identifier). */
public final class DataSource {
    private final String sid;
    public DataSource(String sid){ this.sid=sid; }
    public String sid(){ return sid; }
}
