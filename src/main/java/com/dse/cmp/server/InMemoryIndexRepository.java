package com.dse.cmp.server;

import com.dse.cmp.model.Posting;
import com.dse.cmp.util.CryptoUtils;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory implementation of IndexRepository. */
public final class InMemoryIndexRepository implements IndexRepository {
    private static final class KeyTrip {
        final String sid, ds; final int t;
        KeyTrip(String sid, String ds, int t){ this.sid=sid; this.ds=ds; this.t=t; }
        @Override public boolean equals(Object o){ if(!(o instanceof KeyTrip)) return false; KeyTrip k=(KeyTrip)o; return t==k.t && sid.equals(k.sid) && ds.equals(k.ds); }
        @Override public int hashCode(){ return Objects.hash(sid,ds,t); }
    }
    private final Map<KeyTrip, Map<String, List<Posting>>> map = new ConcurrentHashMap<>();

    @Override
    public void addPosting(String sid, String ds, int t, byte[] label, String docId) {
        KeyTrip k = new KeyTrip(sid,ds,t);
        map.computeIfAbsent(k, kk-> new ConcurrentHashMap<>())
           .computeIfAbsent(com.dse.cmp.util.Bytes.toHex(label), kk-> Collections.synchronizedList(new ArrayList<>()))
           .add(new Posting(docId));
    }

    @Override
    public List<Posting> search(String sid, String ds, int t, byte[] token) {
        KeyTrip k = new KeyTrip(sid,ds,t);
        Map<String, List<Posting>> bucket = map.get(k);
        if (bucket==null) return Collections.emptyList();
        return bucket.getOrDefault(com.dse.cmp.util.Bytes.toHex(token), Collections.emptyList());
    }
}
