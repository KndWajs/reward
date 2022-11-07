/*
 * Copyright (c) 2022 Konrad Wajs, All rights reserved.
 */

package tech.wajs.reward.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tech.wajs.reward.dto.MonthlyRewardDTO;
import tech.wajs.reward.dto.RewardDTO;
import tech.wajs.reward.dto.TransactionDTO;
import tech.wajs.reward.enums.RewardThreshold;
import tech.wajs.reward.validators.RewardValidators;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.groupingBy;

@Service
@AllArgsConstructor
public class RewardService {
    private final int NO_POINTS = 0;
    private RewardValidators validator;

    public RewardDTO calculateReward(List<TransactionDTO> transactions) {
        validator.validate(transactions, RewardValidators.EMPTY_LIST);
        Set<MonthlyRewardDTO> monthlyRewards = calculateRewardsForEachMonth(transactions);

        return new RewardDTO(monthlyRewards, calculatePointsTotal(monthlyRewards));
    }

    private Set<MonthlyRewardDTO> calculateRewardsForEachMonth(List<TransactionDTO> transactions) {

        Map<MonthlyRewardDTO, List<TransactionDTO>> monthToTransactions =
                transactions.stream().collect(groupingBy(this::createMonthlyReward));
        monthToTransactions.forEach((month, trans) -> month.setPoints(calculateRewardFromTransactions(trans)));

        return monthToTransactions.keySet();
    }

    private MonthlyRewardDTO createMonthlyReward(TransactionDTO transaction) {
        validator.validate(transaction, List.of(
                RewardValidators.DATE_OR_COST_MISSING,
                RewardValidators.NEGATIVE_COST,
                RewardValidators.TOO_OLD));
        return new MonthlyRewardDTO(transaction.getTime().getYear(), transaction.getTime().getMonth().getValue());
    }

    private Integer calculateRewardFromTransactions(List<TransactionDTO> transactions) {
        return transactions.stream().mapToInt(t -> calculatePoints(t.getCost())).sum();
    }

    private Integer calculatePoints(BigDecimal costWithFractal) {
        Integer cost = costWithFractal.intValue();
        RewardThreshold firstThreshold = RewardThreshold.FIFTY_DOLLARS;
        RewardThreshold secondThreshold = RewardThreshold.HUNDRED_DOLLARS;

        if (cost <= firstThreshold.getCost()) {
            return NO_POINTS;
        }
        if (cost <= secondThreshold.getCost()) {
            return calculatePointsForThreshold(cost, firstThreshold);
        }
        Integer pointsFromFirstThreshold = firstThreshold.getPointsPerDollar() *
                (secondThreshold.getCost() - firstThreshold.getCost());

        return pointsFromFirstThreshold + calculatePointsForThreshold(cost, secondThreshold);
    }

    private Integer calculatePointsForThreshold(Integer cost, RewardThreshold threshold) {
        Integer dollarsAboveThreshold = cost - threshold.getCost();
        return dollarsAboveThreshold * threshold.getPointsPerDollar();
    }

    private Integer calculatePointsTotal(Set<MonthlyRewardDTO> monthlyRewards) {
        return monthlyRewards.stream().mapToInt(MonthlyRewardDTO::getPoints).sum();
    }
}

