package com.paperized.easynotifier.dquery;

import com.paperized.easynotifier.dquery.pick.DPick;
import com.paperized.easynotifier.dquery.sort.DComparable;
import com.paperized.easynotifier.dquery.sort.DSort;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DQueryRequest implements Serializable {
    private DQueryNode query;
    private DSort sort;
    private DPick pick;

    public <T extends DQueriable> void filterQueriables(List<T> queriables) {
        if(query != null) {
            queriables.removeIf(curr -> !query.evaluate(curr));
        }

        if(sort != null) {
            queriables.sort(new DComparable(sort));
        }

        if(pick != null) {
            pick.pickQueriables(queriables);
        }
    }
}
