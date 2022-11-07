/*
 * Copyright (c) 2022 Konrad Wajs, All rights reserved.
 */

package tech.wajs.reward.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@AllArgsConstructor
@Getter
public class TransactionDTO {
    private BigDecimal cost;
    private ZonedDateTime time;
}
