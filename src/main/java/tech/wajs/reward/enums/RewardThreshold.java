/*
 * Copyright (c) 2022 Konrad Wajs, All rights reserved.
 */

package tech.wajs.reward.enums;

import lombok.Getter;

@Getter
public enum RewardThreshold {
    FIFTY_DOLLARS(50, 1),
    HUNDRED_DOLLARS(100, 2);

    private final Integer cost;
    private final Integer pointsPerDollar;

    RewardThreshold(Integer cost, Integer pointsPerDollar) {
        this.cost = cost;
        this.pointsPerDollar = pointsPerDollar;
    }
}

