package com.paperized.shopapi.controller.advice;

import com.paperized.shopapi.exceptions.ScraperFailedConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionController {
    Logger logger = LoggerFactory.getLogger(GlobalExceptionController.class);

    @ExceptionHandler(ScraperFailedConnectionException.class)
    public ResponseEntity<ErrorResponse> handleScraperFailedConnectionException(ScraperFailedConnectionException ex) {
        logger.info("ControllerAdvice: {}", ex.getMessage());

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setHttpStatus(httpStatus.value());
        errorResponse.setErrorCode("E_001");
        errorResponse.setErrorDescription("Failed to connect to the target website!");

        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        logger.info("ControllerAdvice: {}", ex.getMessage());

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setHttpStatus(httpStatus.value());
        errorResponse.setErrorCode("E_000");
        errorResponse.setErrorDescription("Unexpected error: " + ex.getMessage());

        return new ResponseEntity<>(errorResponse, httpStatus);
    }
}
