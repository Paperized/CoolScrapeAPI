package com.paperized.easynotifier.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ErrorResponse implements Serializable {
    private int httpStatus;
    private String errorCode;
    private String errorDescription;
}
