package com.warehouse.exception;

import java.time.Instant;

//error body returned to clients when a request fails.
 //status  HTTP status code
 //error   short reason phrase
//message human-readable detail
 //timestamp when the error occurred

public record ApiError(int status, String error, String message, Instant timestamp) {

    public static ApiError of(int status, String error, String message) {
        return new ApiError(status, error, message, Instant.now());
    }
}
