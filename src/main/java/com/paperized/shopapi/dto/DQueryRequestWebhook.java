package com.paperized.shopapi.dto;

import com.paperized.shopapi.dquery.DQueryRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class DQueryRequestWebhook extends DQueryRequest implements Serializable {
    private boolean onlyIfDifferent;
    private boolean sendOnlyDifferences;

    public DQueryRequestWebhook(DQueryRequest queryRequest) {
        setQuery(queryRequest.getQuery());
        setSort(queryRequest.getSort());
        setPick(queryRequest.getPick());
    }

    //TODO: use onlyIfDifferent to calculate the diff and send back only if different from previous scrape
}
