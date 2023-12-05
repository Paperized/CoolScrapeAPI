package com.paperized.shopapi.model;

import com.paperized.shopapi.dquery.DQueriable;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "REGISTERED_PRODUCT_TRACKING")
@Data
public class RegisteredProductTracking implements DQueriable {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "WEBHOOK_URL", nullable = false)
    private String webhookUrl;

    @Column(name = "INTERVAL_DURATION", nullable = false)
    private long intervalDuration;

    @OneToOne(mappedBy = "registeredProductTracking")
    private ProductTracking productTracking;

    @Override
    public Object getVariableValue(String name) {
        return switch (name) {
            case "id" -> id;
            case "webhookUrl" -> webhookUrl;
            case "intervalDuration" -> intervalDuration;
            case "productTracking" -> productTracking;
            default -> throw new RuntimeException(name + " is not a properties of " + getClass().getSimpleName());
        };
    }
}
