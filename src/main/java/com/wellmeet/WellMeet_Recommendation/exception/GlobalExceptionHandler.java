package com.wellmeet.WellMeet_Recommendation.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(WellMeetException.class)
    public ResponseEntity<ErrorResponse> handleOAuthClientException(WellMeetException exception) {
        return toResponse(exception.getStatus(), exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception, HttpServletRequest request) {
        log.error("Unexpected error occurred - Path: {}, Method: {}, Message: {}",
                request.getRequestURI(),
                request.getMethod(),
                exception.getMessage(),
                exception);
        return toResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> toResponse(ErrorCode errorCode) {
        return toResponse(errorCode.getStatus(), errorCode.getMessage());
    }

    private ResponseEntity<ErrorResponse> toResponse(HttpStatus httpStatus, String message) {
        ErrorResponse errorResponse = new ErrorResponse(message);
        return ResponseEntity.status(httpStatus)
                .body(errorResponse);
    }
}
