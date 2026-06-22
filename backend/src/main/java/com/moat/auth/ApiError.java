package com.moat.auth;

import java.time.OffsetDateTime;

public record ApiError(String timestamp, int status, String error, String message) {

    public static ApiError of(int status, String error, String message) {
        return new ApiError(OffsetDateTime.now().toString(), status, error, message);
    }
}
