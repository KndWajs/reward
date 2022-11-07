/*
 * Copyright (c) 2022 Konrad Wajs, All rights reserved.
 */

package tech.wajs.reward.validators;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.function.Predicate;

@Getter
public class Validator <T> {
    private final String message;
    private final HttpStatus responseCode;
    private final Predicate<T> predicate;

    public Validator(String message, HttpStatus responseCode, Predicate<T> predicate) {
        this.predicate = predicate;
        this.message = message;
        this.responseCode = responseCode;
    }
}

