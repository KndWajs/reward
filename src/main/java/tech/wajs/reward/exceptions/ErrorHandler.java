/*
 * Copyright (c) 2022 Konrad Wajs, All rights reserved.
 */

package tech.wajs.reward.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler(KnownException.class)
    public ResponseEntity handleKnownException(KnownException e) {
        return ResponseEntity.status(e.getResponseCode()).body(List.of(e.getMessage(), e.getErrorId().toString()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleKnownException(Exception e) {
        KnownException knownException = new KnownException("Internal error.", HttpStatus.INTERNAL_SERVER_ERROR);
        log.error(knownException.getErrorId().toString());
        e.printStackTrace();
        return ResponseEntity.status(knownException.getResponseCode())
                             .body(List.of(knownException.getMessage(), knownException.getErrorId().toString()));
    }
}
