package com.paperized.shopapi.model.webhookfilter;

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
    private DOnChanges previousDataChecks;

    public DQueryRequestWebhook(DQueryRequest queryRequest) {
        setQuery(queryRequest.getQuery());
        setSort(queryRequest.getSort());
        setPick(queryRequest.getPick());
    }
}
