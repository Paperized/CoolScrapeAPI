package com.paperized.shopapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.OffsetDateTime;

@Entity
@Table(name = "PRODUCT_TRACKING")
@Data
public class ProductTracking {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "action", nullable = false)
    private TrackingAction action;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;
}
