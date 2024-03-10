package com.paperized.shopapi.dquery;

import com.paperized.shopapi.dquery.sort.DComparable;
import com.paperized.shopapi.dquery.sort.DSort;
import lombok.Data;

import java.util.List;

@Data
public class DQueryRequest {
    private DQueryNode query;
    private DSort sort;

    public <T extends DQueriable> void filterQueriables(List<T> queriables) {
        if(query != null) {
            queriables.removeIf(curr -> !query.evaluate(curr));
        }

        if(sort != null) {
            queriables.sort(new DComparable(sort));
        }
    }
}
