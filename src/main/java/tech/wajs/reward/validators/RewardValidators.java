/*
 * Copyright (c) 2022 Konrad Wajs, All rights reserved.
 */

package tech.wajs.reward.validators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tech.wajs.reward.dto.TransactionDTO;
import tech.wajs.reward.exceptions.KnownException;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class RewardValidators {

    public final static Validator<TransactionDTO> DATE_OR_COST_MISSING =
            new Validator<>("Cost or Date is missing.",
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    t -> t.getCost() == null || t.getTime() == null);

    public final static Validator<TransactionDTO> NEGATIVE_COST =
            new Validator<>("Cost can not be negative.",
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    t -> t.getCost().compareTo(BigDecimal.ZERO) < 0);

    public final static Validator<TransactionDTO> TOO_OLD =
            new Validator<>("Transaction is older than 3 months.",
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    t -> t.getTime().isBefore(ZonedDateTime.now().minusMonths(3)));
    public final static Validator<List<TransactionDTO>> EMPTY_LIST =
            new Validator<>("List of transactions is empty.",
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    CollectionUtils::isEmpty);

    public <T> void validate(T objectToValidate, List<Validator<T>> validators) {
        for (Validator<T> validator : validators) {
            validate(objectToValidate, validator);
        }
    }

    public <T> void validate(T objectToValidate, Validator<T> validator) {
        if (validator.getPredicate().test(objectToValidate)) {
            KnownException knownException = new KnownException(validator.getMessage(), validator.getResponseCode());
            log.warn(knownException.getErrorId().toString());
            Arrays.stream(Thread.currentThread().getStackTrace()).forEach(System.out::println);
            throw knownException;
        }
    }
}

