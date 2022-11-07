/*
 * Copyright (c) 2022 Konrad Wajs, All rights reserved.
 */

package tech.wajs.reward.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.wajs.reward.dto.RewardDTO;
import tech.wajs.reward.dto.TransactionDTO;
import tech.wajs.reward.service.RewardService;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class RewardController {

    private RewardService rewardService;

    @PostMapping(value = "/calculate-reward", consumes = MediaType.APPLICATION_JSON_VALUE)
    public RewardDTO getReward(@RequestBody List<TransactionDTO> transactions) {

        return rewardService.calculateReward(transactions);
    }
}
