/*
 * Copyright (c) 2022 Konrad Wajs, All rights reserved.
 */

package tech.wajs.reward.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tech.wajs.reward.dto.RewardDTO;
import tech.wajs.reward.dto.TransactionDTO;

import java.util.List;

@Service
@AllArgsConstructor
public class RewardService {
    public RewardDTO calculateReward(List<TransactionDTO> transactions) {
        return null;
    }
}
