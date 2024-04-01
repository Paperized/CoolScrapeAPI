package com.paperized.shopapi.model;

import com.paperized.shopapi.dquery.DQueryRequest;
import com.paperized.shopapi.model.converter.DQueryRequestConverter;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "REGISTERED_PRODUCT_TRACKING")
@Data
public class RegisteredProductTracking {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "WEBHOOK_URL", nullable = false)
    private String webhookUrl;

    @Column(name = "REQUEST_QUERY", nullable = true)
    @Convert(converter = DQueryRequestConverter.class)
    private DQueryRequest request;

    @Column(name = "INTERVAL_DURATION", nullable = false)
    private long intervalDuration;

    @OneToOne(mappedBy = "registeredProductTracking")
    private ProductTracking productTracking;
}
