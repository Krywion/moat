package com.moat.company;

import com.moat.api.model.ApiError;
import com.moat.esef.EsefParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CompanyExceptionHandler {

    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(CompanyNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(EsefParseException.class)
    public ResponseEntity<ApiError> handleEsef(EsefParseException ex) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(CompanyMismatchException.class)
    public ResponseEntity<ApiError> handleMismatch(CompanyMismatchException ex) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String message) {
        ApiError body = new ApiError()
                .timestamp(java.time.OffsetDateTime.now().toString())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message);
        return ResponseEntity.status(status).body(body);
    }
}
