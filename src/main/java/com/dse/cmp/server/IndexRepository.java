package com.dse.cmp.server;

import com.dse.cmp.model.Posting;
import java.util.List;

/** Repository for encrypted index postings keyed by (sid, ds, t, labelHex). */
public interface IndexRepository {
    void addPosting(String sid, String ds, int t, byte[] label, String docId);
    List<Posting> search(String sid, String ds, int t, byte[] token);
}
