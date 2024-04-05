package com.paperized.shopapi.dto;

import com.paperized.shopapi.dquery.DQueriable;
import com.paperized.shopapi.dquery.DQueryNode;
import com.paperized.shopapi.dquery.pick.DPick;
import com.paperized.shopapi.dquery.sort.DComparable;
import com.paperized.shopapi.dquery.sort.DSort;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DQueryRequestWebhook implements Serializable {
    private DQueryNode query;
    private DSort sort;
    private DPick pick;
    private boolean onlyIfDifferent;
    private boolean sendOnlyDifferences;

    //TODO: use onlyIfDifferent to calculate the diff and send back only if different from previous scrape
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
