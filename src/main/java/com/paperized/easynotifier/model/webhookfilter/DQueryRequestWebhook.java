package com.paperized.easynotifier.model.webhookfilter;

import com.paperized.easynotifier.dquery.DQueryRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
