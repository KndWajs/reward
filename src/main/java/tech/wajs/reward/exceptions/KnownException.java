/*
 * Copyright (c) 2022 Konrad Wajs, All rights reserved.
 */

package tech.wajs.reward.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Getter
public class KnownException extends RuntimeException {
    private final String message;
    private final UUID errorId;
    private final HttpStatus responseCode;

    public KnownException(String message, HttpStatus responseCode) {
        this(message, responseCode, UUID.randomUUID());
    }
    public KnownException(String message, HttpStatus responseCode, UUID errorId) {
        this.message = message;
        this.responseCode = responseCode;
        this.errorId = errorId;
    }
}
