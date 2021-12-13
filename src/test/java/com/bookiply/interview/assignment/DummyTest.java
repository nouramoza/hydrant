package com.bookiply.interview.assignment;

import com.bookiply.interview.assignment.web.dto.FireInfoDto;
import com.bookiply.interview.assignment.web.dto.PointDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import java.awt.*;
import java.net.URI;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class DummyTest {

    @Autowired
    MockMvc mockMvc;

    private static final URI FIND_NEAREST_FIREHOSES_URI = URI.create("/api/v1/nearest-service/findNearestFireHoses");

    @Test
    void contextLoads() {
    }

    @Test
    public void test1() throws Exception {

        Double[] point = {40.43, -74.1};

        FireInfoDto fireInfoDto = new FireInfoDto(new PointDto("point", point), 2L);

        String inputStr = mapToJson(fireInfoDto);
        RequestBuilder req = post(FIND_NEAREST_FIREHOSES_URI)
                .contentType(MediaType.APPLICATION_JSON) // for DTO
                .content(inputStr);

        String outputExpectedStr = "kjh";

        MvcResult mvcResult = this.mockMvc.perform(req)
                .andExpect(content().string(containsString(outputExpectedStr)))
                .andExpect(status().is(409))
                .andDo(print())
                .andReturn();

    }

    protected static String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
}
