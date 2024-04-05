package com.paperized.shopapi.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "PRODUCT_TRACKER")
@Data
public class ProductTracker {
    @Id
    @Column(name = "ID")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "WEBSITE_NAME", nullable = false)
    private WebsiteName websiteName;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACTION", nullable = false)
    private TrackerAction action;

    @Column(name = "URL", nullable = false)
    private String url;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ID", referencedColumnName = "id")
    private ProductTrackerDetails productTrackerDetails;
}
