package com.paperized.shopapi.controller.advice;

import com.paperized.shopapi.dto.ErrorResponse;
import com.paperized.shopapi.exceptions.ScraperFailedConnectionException;
import com.paperized.shopapi.exceptions.TrackingExpiredException;
import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import jakarta.persistence.EntityNotFoundException;
import org.jsoup.HttpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionController {
    Logger logger = LoggerFactory.getLogger(GlobalExceptionController.class);

    @ExceptionHandler(UnsuccessfulScrapeException.class)
    public ResponseEntity<ErrorResponse> handleUnsuccessfulScrapeException(UnsuccessfulScrapeException ex) {
        return getErrorResponseResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "E_005", "The page loaded but we were unable to scrape data", ex);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        return getErrorResponseResponseEntity(HttpStatus.NOT_FOUND, "E_004", "This resource does not exists!", ex);
    }

    @ExceptionHandler(TrackingExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTrackingExpiredException(TrackingExpiredException ex) {
        return getErrorResponseResponseEntity(HttpStatus.BAD_REQUEST, "E_003", "This tracing already expired!", ex);
    }

    @ExceptionHandler(HttpStatusException.class)
    public ResponseEntity<ErrorResponse> handleScraperHttpStatusException(HttpStatusException ex) {
        return getErrorResponseResponseEntity(HttpStatus.BAD_REQUEST, "E_002",
                "Website failed to load with the provided filters, error: " + ex.getStatusCode(), ex);
    }

    @ExceptionHandler(ScraperFailedConnectionException.class)
    public ResponseEntity<ErrorResponse> handleScraperFailedConnectionException(ScraperFailedConnectionException ex) {
        return getErrorResponseResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "E_001", "Failed to connect to the target website!", ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        return getErrorResponseResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "E_000", "Unexpected error: " + ex.getMessage(), ex);
    }

    private ResponseEntity<ErrorResponse> getErrorResponseResponseEntity(HttpStatus httpStatus, String errorCode, String errorDescription, Exception ex) {
        logger.info("ControllerAdvice: {}", ex.getMessage());

        //useful for debug right now
        //noinspection CallToPrintStackTrace
        ex.printStackTrace();

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setHttpStatus(httpStatus.value());
        errorResponse.setErrorCode(errorCode);
        errorResponse.setErrorDescription(errorDescription);

        return new ResponseEntity<>(errorResponse, httpStatus);
    }
}
