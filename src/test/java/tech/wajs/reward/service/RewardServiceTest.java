/*
 * Copyright (c) 2022 Konrad Wajs, All rights reserved.
 */

package tech.wajs.reward.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.wajs.reward.dto.MonthlyRewardDTO;
import tech.wajs.reward.dto.RewardDTO;
import tech.wajs.reward.dto.TransactionDTO;
import tech.wajs.reward.validators.RewardValidators;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RewardServiceTest {

    @InjectMocks
    RewardService rewardService;

    @Mock
    RewardValidators validator;

    @Test
    void shouldReturn90PointsWhenSpend120() {
        //given
        int expectedPoints = 90;
        List<TransactionDTO> transactions = List.of(t(new BigDecimal(120), ZonedDateTime.now()));

        //when
        RewardDTO rewardDTO = rewardService.calculateReward(transactions);

        //then
        assertThat(rewardDTO.getMonthlyRewards().size()).isEqualTo(1);
        assertThat(rewardDTO.getMonthlyRewards().stream().findFirst().orElseThrow().getPoints()).isEqualTo(expectedPoints);
        assertThat(rewardDTO.getTotalPoints()).isEqualTo(expectedPoints);
    }

    @Test
    void shouldReturnCorrectNumberOfPointsWhenOneTransaction() {
        testValues(0, new BigDecimal(0));
        testValues(0, new BigDecimal(45));
        testValues(0, new BigDecimal(50));
        testValues(15, new BigDecimal(65));
        testValues(49, new BigDecimal(99));
        testValues(50, new BigDecimal(100));
        testValues(50 + 40, new BigDecimal(120));
        testValues(50 + 240, new BigDecimal(220));
    }

    private void testValues(int expectedPoints, BigDecimal cost) {
        //given
        List<TransactionDTO> transactions = List.of(t(cost, ZonedDateTime.now()));

        //when
        RewardDTO rewardDTO = rewardService.calculateReward(transactions);

        //then
        try {
            assertThat(rewardDTO.getMonthlyRewards().size()).isEqualTo(1);
            assertThat(rewardDTO.getMonthlyRewards().stream().findFirst().orElseThrow().getPoints()).isEqualTo(expectedPoints);
            assertThat(rewardDTO.getTotalPoints()).isEqualTo(expectedPoints);
        } catch (AssertionError e) {
            System.out.println("expected points: " + expectedPoints);
            System.out.println("cost: " + cost);
            throw e;
        }
    }

    @Test
    void shouldReturn180PointsWhenSpendTwoTimes120() {
        //given
        int expectedPoints = 90 * 2;
        List<TransactionDTO> transactions = List.of(
                t(new BigDecimal(120), ZonedDateTime.now()),
                t(new BigDecimal(120), ZonedDateTime.now())
        );

        //when
        RewardDTO rewardDTO = rewardService.calculateReward(transactions);

        //then
        assertThat(rewardDTO.getMonthlyRewards().size()).isEqualTo(1);
        assertThat(rewardDTO.getMonthlyRewards().stream().findFirst().orElseThrow().getPoints()).isEqualTo(expectedPoints);
        assertThat(rewardDTO.getTotalPoints()).isEqualTo(expectedPoints);
    }

    @Test
    void shouldReturnTwoMonthsWith90PointsEach() {
        //given
        int expectedPoints = 90;
        List<TransactionDTO> transactions = List.of(
                t(new BigDecimal(120), ZonedDateTime.now().minusMonths(1)),
                t(new BigDecimal(120), ZonedDateTime.now())
        );

        //when
        RewardDTO rewardDTO = rewardService.calculateReward(transactions);

        //then
        assertThat(rewardDTO.getMonthlyRewards().size()).isEqualTo(2);
        assertThat(rewardDTO.getMonthlyRewards().stream().findFirst().orElseThrow().getPoints()).isEqualTo(expectedPoints);
    }

    @Test
    void shouldSumPointsFromAllMonths() {
        //given
        List<TransactionDTO> transactions = List.of(
                t(new BigDecimal(340), ZonedDateTime.now().minusMonths(2)),
                t(new BigDecimal(120), ZonedDateTime.now().minusMonths(1)),
                t(new BigDecimal(20), ZonedDateTime.now())
        );

        //when
        RewardDTO rewardDTO = rewardService.calculateReward(transactions);

        //then
        assertThat(rewardDTO.getMonthlyRewards().size()).isEqualTo(3);
        int sum = rewardDTO.getMonthlyRewards().stream().mapToInt(MonthlyRewardDTO::getPoints).sum();
        assertThat(rewardDTO.getTotalPoints()).isEqualTo(sum);
    }

    private TransactionDTO t(BigDecimal cost, ZonedDateTime time) {
        return new TransactionDTO(cost, time);
    }
}

