package com.paperized.shopapi.dquery.pick;

import com.paperized.shopapi.dquery.DQueriable;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DPick implements Serializable {
    private Integer limit;

    public <T extends DQueriable> void pickQueriables(List<T> queriables) {
        if(queriables == null)
            throw new NullPointerException("DPick cannot have null list as input");

        if(limit != null) {
            var it = queriables.iterator();
            int curr = 0;
            while (it.hasNext()) {
                if (curr + 1 >= limit) {
                    it.remove();
                }

                curr++;
            }
        }
    }
}
