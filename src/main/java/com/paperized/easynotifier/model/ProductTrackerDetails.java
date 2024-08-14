package com.paperized.easynotifier.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.paperized.easynotifier.model.webhookfilter.DQueryRequestScheduled;
import com.paperized.easynotifier.model.converter.DQueryRequestConverter;
import com.paperized.easynotifier.utils.AppUtils;
import jakarta.persistence.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Entity
@Table(name = "PRODUCT_TRACKER_DETAILS")
@Data
public class ProductTrackerDetails {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "WEBHOOK_URL", nullable = false)
    private String webhookUrl;

    @Column(name = "WS_ENABLED", nullable = false)
    private boolean wsEnabled;

    @Column(name = "INTERVAL_DURATION", nullable = false)
    private long intervalDuration;

    @Column(name = "REQUEST_QUERY", nullable = true)
    @Convert(converter = DQueryRequestConverter.class)
    private DQueryRequestScheduled filters;

    @Column(name = "LAST_DATA", nullable = true)
    private String lastScrapedDataJson;

    @OneToOne(mappedBy = "productTrackerDetails")
    private ProductTracker productTracker;

    public Object getLastScrapedDataAs(TrackerAction trackerAction) throws JsonProcessingException {
        if(StringUtils.isBlank(lastScrapedDataJson)) {
            return null;
        }

        return AppUtils.fromJson(lastScrapedDataJson, trackerAction);
    }
}
