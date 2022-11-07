/*
 * Copyright (c) 2022 Konrad Wajs, All rights reserved.
 */

package tech.wajs.reward.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tech.wajs.reward.dto.RewardDTO;
import tech.wajs.reward.dto.TransactionDTO;
import tech.wajs.reward.validators.RewardValidators;
import tech.wajs.reward.validators.Validator;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper MAPPER = new JsonMapper();

    @BeforeEach
    void setUp() {
        MAPPER.findAndRegisterModules(); // because: `java.time.ZonedDateTime` not supported by default
    }

    @Test
    void shouldReturn90PointsWhenSpend120() throws Exception {
        //given
        String json = """
                  [
                  {
                    "cost": "120",
                    "time": "%s"
                  }
                  ]
                """.formatted(ZonedDateTime.now().toString());
        //when
        MockHttpServletResponse response = callCalculateRewardEndpoint(json);
        RewardDTO responseObject = MAPPER.readValue(response.getContentAsString(), RewardDTO.class);

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseObject.getTotalPoints()).isEqualTo(90);
    }

    @Test
    void shouldReturn180PointsWhenSpendTwoTimes120() throws Exception {
        //given
        String json = """
                  [
                  {
                    "cost": "120",
                    "time": "%s"
                  },
                  {
                    "cost": "120",
                    "time": "%s"
                  }
                  ]
                """.formatted(ZonedDateTime.now().toString(), ZonedDateTime.now().minusMonths(2).toString());
        //when
        MockHttpServletResponse response = callCalculateRewardEndpoint(json);
        RewardDTO responseObject = MAPPER.readValue(response.getContentAsString(), RewardDTO.class);

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseObject.getTotalPoints()).isEqualTo(2*90);
    }

    @Test
    void shouldReturn422WhenDateIsMissing() throws Exception {
        //given
        Validator<TransactionDTO> expectedError = RewardValidators.DATE_OR_COST_MISSING;
        String json = """
                  [
                  {
                    "cost": "120",
                    "time": ""
                  }
                  ]
                """;

        //when
        MockHttpServletResponse response = callCalculateRewardEndpoint(json);

        //then
        assertThat(response.getStatus()).isEqualTo(expectedError.getResponseCode().value());
        assertThat(response.getContentAsString()).isEqualTo(expectedError.getMessage());
    }

    @Test
    void shouldReturn422WhenCostIsMissing() throws Exception {
        //given
        Validator<TransactionDTO> expectedError = RewardValidators.DATE_OR_COST_MISSING;
        String json = """
                  [
                  {
                    "cost": "",
                    "time": "%s"
                  }
                  ]
                """.formatted(ZonedDateTime.now().toString());

        //when
        MockHttpServletResponse response = callCalculateRewardEndpoint(json);

        //then
        assertThat(response.getStatus()).isEqualTo(expectedError.getResponseCode().value());
        assertThat(response.getContentAsString()).isEqualTo(expectedError.getMessage());
    }

    @Test
    void shouldReturn422WhenCostIsNegative() throws Exception {
        //given
        Validator<TransactionDTO> expectedError = RewardValidators.NEGATIVE_COST;
        String json = """
                  [
                  {
                    "cost": "-200",
                    "time": "%s"
                  }
                  ]
                """.formatted(ZonedDateTime.now().toString());

        //when
        MockHttpServletResponse response = callCalculateRewardEndpoint(json);

        //then
        assertThat(response.getStatus()).isEqualTo(expectedError.getResponseCode().value());
        assertThat(response.getContentAsString()).isEqualTo(expectedError.getMessage());
    }

    @Test
    void shouldReturn422WhenTransactionIsOlderThan3Months() throws Exception {
        //given
        Validator<TransactionDTO> expectedError = RewardValidators.TOO_OLD;
        String json = """
                  [
                  {
                    "cost": "120",
                    "time": "%s"
                  }
                  ]
                """.formatted(ZonedDateTime.now().minusMonths(3).minusNanos(1).toString());

        //when
        MockHttpServletResponse response = callCalculateRewardEndpoint(json);

        //then
        assertThat(response.getStatus()).isEqualTo(expectedError.getResponseCode().value());
        assertThat(response.getContentAsString()).isEqualTo(expectedError.getMessage());
    }

    @Test
    void shouldReturn422WhenNoTransaction() throws Exception {
        //given
        Validator expectedError = RewardValidators.EMPTY_LIST;
        String json = """
                  [
                  ]
                """;

        //when
        MockHttpServletResponse response = callCalculateRewardEndpoint(json);

        //then
        assertThat(response.getStatus()).isEqualTo(RewardValidators.EMPTY_LIST.getResponseCode().value());
        assertThat(response.getContentAsString()).isEqualTo(RewardValidators.EMPTY_LIST.getMessage());
    }

    private TransactionDTO t(BigDecimal cost, ZonedDateTime time) {
        return new TransactionDTO(cost, time);
    }

    private MockHttpServletResponse callCalculateRewardEndpoint(String json) throws Exception {

        return mockMvc.perform(MockMvcRequestBuilders
                              .post("/api/calculate-reward")
                              //                              .with(SecurityMockMvcRequestPostProcessors.csrf())
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(json))
                      .andReturn().getResponse();
    }
}

