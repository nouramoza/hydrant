package com.bookiply.interview.assignment;

import com.bookiply.interview.assignment.web.dto.FireInfoDto;
import com.bookiply.interview.assignment.web.dto.NearestHydrantsToFireDto;
import com.bookiply.interview.assignment.web.dto.PointDto;
import com.bookiply.interview.assignment.web.dto.SelectedHydrantDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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

    private static final URI FIND_NEAREST_FIREHOSES_URI = URI.create("/api/v1/findHydrant-service/findNearestFireHosesJsonOut");

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


        List<SelectedHydrantDto> hydrants = new ArrayList<>();
        hydrants.add(new SelectedHydrantDto("H415472", 161.83913805999595));
        hydrants.add(new SelectedHydrantDto("H415833", 161.83942225079014));
        NearestHydrantsToFireDto nearestHydrantsToFireDto = new NearestHydrantsToFireDto(323.6785603107861, hydrants);
        String outputExpectedStr = mapToJson(nearestHydrantsToFireDto);

        MvcResult mvcResult = this.mockMvc.perform(req)
                .andExpect(content().string(containsString(outputExpectedStr)))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andDo(print())
                .andReturn();
//        Assert.assertEquals(HttpStatus.OK.value(), response.getStatus());

    }

    protected static String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
}
