package com.bookiply.interview.assignment;

import com.bookiply.interview.assignment.web.dto.FireInfoDto;
import com.bookiply.interview.assignment.web.error.ErrorConstants;
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

import java.net.URI;

import static com.bookiply.interview.assignment.CommonMethods.mapToJson;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class ExceptionsTest {

    @Autowired
    MockMvc mockMvc;

    private static final URI FIND_NEAREST_FIREHOSES_QUERY_URI = URI.create("/api/v1/findHydrant-service/findNearestFireHosesQueryBase");

    @Test
    public void latitudeValidationTest() throws Exception {
        FireInfoDto fireInfoDto = new FireInfoDto();
        fireInfoDto.setLatitude(120.3658745);
        fireInfoDto.setLongitude(-74.1);
        fireInfoDto.setNumberOfFireTrucks(2L);

        String inputStr = mapToJson(fireInfoDto);
        RequestBuilder req = post(FIND_NEAREST_FIREHOSES_QUERY_URI)
                .contentType(MediaType.APPLICATION_JSON) // for DTO
                .content(inputStr);

        MvcResult mvcResult = this.mockMvc.perform(req)
                .andExpect(content().string(containsString(ErrorConstants.InputValidationMessage.LAT_RANGE_MSG)))
                .andExpect(status().is(HttpStatus.NOT_ACCEPTABLE.value()))
                .andDo(print())
                .andReturn();
    }

    @Test
    public void longitudeValidationTest() throws Exception {
        FireInfoDto fireInfoDto = new FireInfoDto();
        fireInfoDto.setLatitude(43.3658745);
        fireInfoDto.setLongitude(197.1);
        fireInfoDto.setNumberOfFireTrucks(2L);

        String inputStr = mapToJson(fireInfoDto);
        RequestBuilder req = post(FIND_NEAREST_FIREHOSES_QUERY_URI)
                .contentType(MediaType.APPLICATION_JSON) // for DTO
                .content(inputStr);

        MvcResult mvcResult = this.mockMvc.perform(req)
                .andExpect(content().string(containsString(ErrorConstants.InputValidationMessage.LONG_RANGE_MSG)))
                .andExpect(status().is(HttpStatus.NOT_ACCEPTABLE.value()))
                .andDo(print())
                .andReturn();
    }

    @Test
    public void numberOfTrucksValidationTest() throws Exception {
        FireInfoDto fireInfoDto = new FireInfoDto();
        fireInfoDto.setLatitude(43.3658745);
        fireInfoDto.setLongitude(-45.25);
        fireInfoDto.setNumberOfFireTrucks(1254L);

        String inputStr = mapToJson(fireInfoDto);
        RequestBuilder req = post(FIND_NEAREST_FIREHOSES_QUERY_URI)
                .contentType(MediaType.APPLICATION_JSON) // for DTO
                .content(inputStr);

        MvcResult mvcResult = this.mockMvc.perform(req)
                .andExpect(content().string(containsString(ErrorConstants.InputValidationMessage.WRONG_TRUCK_NUMBERS_MSG)))
                .andExpect(status().is(HttpStatus.NOT_ACCEPTABLE.value()))
                .andDo(print())
                .andReturn();
    }

    @Test
    public void connectionFailedTest() throws Exception {
        FireInfoDto fireInfoDto = new FireInfoDto();
        fireInfoDto.setLatitude(43.3658745);
        fireInfoDto.setLongitude(-45.25);
        fireInfoDto.setNumberOfFireTrucks(1254L);

        String inputStr = mapToJson(fireInfoDto);
        RequestBuilder req = post(FIND_NEAREST_FIREHOSES_QUERY_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputStr);

        MvcResult mvcResult = this.mockMvc.perform(req)
                .andExpect(content().string(containsString(ErrorConstants.InputValidationMessage.API_EXCEPTION_MSG)))
                .andExpect(status().is(HttpStatus.NOT_ACCEPTABLE.value()))
                .andDo(print())
                .andReturn();
    }

    @Test
    public void noHydrantFoundTest() throws Exception {
        FireInfoDto fireInfoDto = new FireInfoDto();
        fireInfoDto.setLatitude(20.43);
        fireInfoDto.setLongitude(54.1);
        fireInfoDto.setNumberOfFireTrucks(2L);


        String inputStr = mapToJson(fireInfoDto);
        RequestBuilder req = post(FIND_NEAREST_FIREHOSES_QUERY_URI)
                .contentType(MediaType.APPLICATION_JSON) // for DTO
                .content(inputStr);
        MvcResult mvcResult = this.mockMvc.perform(req)
                .andExpect(content().string(containsString(ErrorConstants.BusinessError.NO_HYDRANT_MSG)))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andDo(print())
                .andReturn();
    }


}
