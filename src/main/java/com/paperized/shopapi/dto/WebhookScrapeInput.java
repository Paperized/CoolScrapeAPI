package com.paperized.shopapi.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class WebhookScrapeInput implements Serializable {
    private Object result;
    private String trackingId;
}
