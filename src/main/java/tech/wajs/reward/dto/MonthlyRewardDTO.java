/*
 * Copyright (c) 2022 Konrad Wajs, All rights reserved.
 */

package tech.wajs.reward.dto;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class MonthlyRewardDTO {
    Integer year;
    Integer month;
    Integer points;

    public MonthlyRewardDTO(Integer year, Integer month) {
        this.year = year;
        this.month = month;
    }
}
