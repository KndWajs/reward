/*
 * Copyright (c) 2022 Konrad Wajs, All rights reserved.
 */

package tech.wajs.reward.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(KnownException.class)
    public ResponseEntity handleKnownException(KnownException e) {
        return ResponseEntity.status(e.getResponseCode()).body(List.of(e.getMessage(), e.getErrorId().toString()));
    }
}
