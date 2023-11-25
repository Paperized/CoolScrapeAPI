package com.paperized.shopapi.controller.advice;

import lombok.Data;

@Data
public class ErrorResponse {
    private int httpStatus;
    private String errorCode;
    private String errorDescription;
}
