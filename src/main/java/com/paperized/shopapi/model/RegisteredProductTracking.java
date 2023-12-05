package com.paperized.shopapi.model;

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

    @Column(name = "INTERVAL_DURATION", nullable = false)
    private long intervalDuration;

    @OneToOne(mappedBy = "registeredProductTracking")
    private ProductTracking productTracking;
}
