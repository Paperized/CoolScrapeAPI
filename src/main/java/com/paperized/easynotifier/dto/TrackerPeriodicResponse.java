package com.paperized.easynotifier.dto;

import lombok.Data;

@Data
public class TrackerPeriodicResponse {
    private String action;
    private String trackerId;
    private Object data;
}
