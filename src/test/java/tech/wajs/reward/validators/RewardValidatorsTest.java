/*
 * Copyright (c) 2022 Konrad Wajs, All rights reserved.
 */

package tech.wajs.reward.validators;

import org.junit.jupiter.api.Test;
import tech.wajs.reward.dto.TransactionDTO;
import tech.wajs.reward.exceptions.KnownException;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RewardValidatorsTest {

    RewardValidators validators = new RewardValidators();

    @Test
    void shouldThrowExceptionWhenDateIsMissing() {
        //given
        Validator<TransactionDTO> validator = RewardValidators.DATE_OR_COST_MISSING;
        TransactionDTO objectToValidate = new TransactionDTO(new BigDecimal("100"), null);
        //when
        //
        testValidator(validator, objectToValidate);
    }

    @Test
    void shouldThrowExceptionWhenCostIsMissing() {
        //given
        Validator<TransactionDTO> validator = RewardValidators.DATE_OR_COST_MISSING;
        TransactionDTO objectToValidate = new TransactionDTO(null, ZonedDateTime.now());
        //when
        //then
        testValidator(validator, objectToValidate);
    }

    @Test
    void shouldThrowExceptionWhenCostIsNegative() {
        //given
        Validator<TransactionDTO> validator = RewardValidators.NEGATIVE_COST;
        TransactionDTO objectToValidate = new TransactionDTO(new BigDecimal("-100"), ZonedDateTime.now());
        //when
        //then
        testValidator(validator, objectToValidate);
    }

    @Test
    void shouldThrowExceptionWhenTransactionIsTooOld() {
        //given
        Validator<TransactionDTO> validator = RewardValidators.TOO_OLD;
        TransactionDTO objectToValidate = new TransactionDTO(new BigDecimal("100"),
                ZonedDateTime.now().minusMonths(3).minusNanos(1));
        //when
        //then
        testValidator(validator, objectToValidate);
    }

    @Test
    void shouldThrowExceptionWhenListIsEmpty() {
        //given
        Validator<List<TransactionDTO>> validator = RewardValidators.EMPTY_LIST;
        List<TransactionDTO> objectToValidate = List.of();
        //when
        //then
        testValidator(validator, objectToValidate);
    }

    private <T> void testValidator(Validator<T> validator, T objectUnderTest) {
        //when
        KnownException exception = assertThrows(KnownException.class, () -> {
            validators.validate(objectUnderTest, validator);
        });

        //then
        assertThat(exception.getMessage()).contains(validator.getMessage());
        assertThat(exception.getResponseCode()).isSameAs(validator.getResponseCode());
    }
}

