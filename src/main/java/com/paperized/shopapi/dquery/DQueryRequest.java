package com.paperized.shopapi.dquery;

import com.paperized.shopapi.dquery.pick.DPick;
import com.paperized.shopapi.dquery.sort.DComparable;
import com.paperized.shopapi.dquery.sort.DSort;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

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
