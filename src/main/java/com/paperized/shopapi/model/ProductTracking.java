package com.paperized.shopapi.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "PRODUCT_TRACKING")
@Data
public class ProductTracking {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "WEBSITE_NAME", nullable = false)
    private WebsiteName websiteName;

    @Column(name = "ACTION", nullable = false)
    private TrackingAction action;

    @Column(name = "URL", nullable = false)
    private String url;

    @Column(name = "REGISTER_EXPIRES_AT", nullable = false)
    private LocalDateTime webhookRegisterExpiresAt;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ID", referencedColumnName = "id")
    private RegisteredProductTracking registeredProductTracking;
}
