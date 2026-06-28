package com.moat.company;

import com.moat.api.model.ApiError;
import com.moat.esef.EsefParseException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

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

    @ExceptionHandler(DuplicateFinancialReportException.class)
    public ResponseEntity<ApiError> handleDuplicateReport(DuplicateFinancialReportException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(DuplicateCompanyException.class)
    public ResponseEntity<ApiError> handleDuplicateCompany(DuplicateCompanyException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex) {
        if (isReportYearConflict(ex)) {
            return error(HttpStatus.CONFLICT, "Raport za ten rok obrotowy już istnieje dla tej spółki.");
        }
        if (isCompanyTickerConflict(ex)) {
            return error(HttpStatus.CONFLICT, "Spółka z tym tickerem już istnieje na Twojej liście.");
        }
        return error(HttpStatus.CONFLICT, "Operacja narusza ograniczenia danych.");
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiError> handleMissingPart(MissingServletRequestPartException ex) {
        return error(HttpStatus.BAD_REQUEST, "Brak wymaganego pliku.");
    }

    private static boolean isReportYearConflict(DataIntegrityViolationException ex) {
        Throwable cause = ex.getMostSpecificCause();
        String message = cause != null ? cause.getMessage() : ex.getMessage();
        return message != null && message.contains("uq_report_company_year");
    }

    private static boolean isCompanyTickerConflict(DataIntegrityViolationException ex) {
        Throwable cause = ex.getMostSpecificCause();
        String message = cause != null ? cause.getMessage() : ex.getMessage();
        return message != null && message.contains("uq_company_user_ticker");
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
