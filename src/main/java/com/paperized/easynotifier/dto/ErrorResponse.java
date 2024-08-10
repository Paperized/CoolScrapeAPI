package com.paperized.easynotifier.dto;

import lombok.Data;

@Data
public class ErrorResponse {
    private int httpStatus;
    private String errorCode;
    private String errorDescription;
}
