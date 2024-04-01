package com.paperized.shopapi.dto;

import com.paperized.shopapi.dquery.DQueriable;
import com.paperized.shopapi.dquery.DQueryNode;
import com.paperized.shopapi.dquery.pick.DPick;
import com.paperized.shopapi.dquery.sort.DComparable;
import com.paperized.shopapi.dquery.sort.DSort;
import lombok.Data;

import java.util.List;

@Data
public class DQueryRequestWebhook {
    private DQueryNode query;
    private DSort sort;
    private DPick pick;
    private boolean onlyIfDifferent;

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
