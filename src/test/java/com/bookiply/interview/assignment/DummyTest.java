package com.bookiply.interview.assignment;

import com.bookiply.interview.assignment.web.dto.NearestHydrantsToFireDto;
import com.bookiply.interview.assignment.web.dto.FireInfoDto;
import com.bookiply.interview.assignment.web.dto.PointDto;
import com.bookiply.interview.assignment.web.dto.SelectedHydrantDto;
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
import java.util.ArrayList;
import java.util.List;

import static com.bookiply.interview.assignment.CommonMethods.mapToJson;
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

    private static final URI FIND_NEAREST_FIREHOSES_QUERY_URI = URI.create("/api/v1/findHydrant-service/findNearestFireHosesQueryBase");
    private static final URI FIND_NEAREST_FIREHOSES_CODE_URI = URI.create("/api/v1/findHydrant-service/findNearestFireHosesCodeBase");

    @Test
    public void normalBusinessTest1() throws Exception {
        FireInfoDto fireInfoDto = new FireInfoDto();
        fireInfoDto.setLatitude(40.7822168);
        fireInfoDto.setLongitude(-73.79157092);
        fireInfoDto.setNumberOfFireTrucks(3L);

        String inputStr = mapToJson(fireInfoDto);
        RequestBuilder req = post(FIND_NEAREST_FIREHOSES_QUERY_URI)
                .contentType(MediaType.APPLICATION_JSON) // for DTO
                .content(inputStr);

        List<SelectedHydrantDto> hydrants = new ArrayList<>();
        hydrants.add(new SelectedHydrantDto("H425653", 0.18432794606921546));
        hydrants.add(new SelectedHydrantDto("H425649", 0.4864869987983226));
        hydrants.add(new SelectedHydrantDto("H426172", 0.773112095685409));
        NearestHydrantsToFireDto nearestHydrantsToFireDto = new NearestHydrantsToFireDto(1.443927040552947, hydrants);
        String outputExpectedStr = mapToJson(nearestHydrantsToFireDto);

        MvcResult mvcResult = this.mockMvc.perform(req)
                .andExpect(content().string(containsString(outputExpectedStr)))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andDo(print())
                .andReturn();
    }

    @Test
    public void normalBusinessTest2() throws Exception {
        FireInfoDto fireInfoDto = new FireInfoDto();
        fireInfoDto.setLatitude(40.8822168);
        fireInfoDto.setLongitude(-73.89157092);
        fireInfoDto.setNumberOfFireTrucks(10L);

        String inputStr = mapToJson(fireInfoDto);
        RequestBuilder req = post(FIND_NEAREST_FIREHOSES_QUERY_URI)
                .contentType(MediaType.APPLICATION_JSON) // for DTO
                .content(inputStr);

        List<SelectedHydrantDto> hydrants = new ArrayList<>();
        hydrants.add(new SelectedHydrantDto("H201544", 0.1839098183946411));
        hydrants.add(new SelectedHydrantDto("H201542", 0.2676649952778256));
        hydrants.add(new SelectedHydrantDto("H201543", 0.9663342322885403));
        hydrants.add(new SelectedHydrantDto("H201535", 1.0004476363187245));
        hydrants.add(new SelectedHydrantDto("H201524", 1.290329944970233));
        hydrants.add(new SelectedHydrantDto("H201525", 1.3442455833934286));
        hydrants.add(new SelectedHydrantDto("H201519", 1.476889076566181));
        hydrants.add(new SelectedHydrantDto("H201526", 1.5214115804047723));
        hydrants.add(new SelectedHydrantDto("H201545", 1.8451007083647661));
        hydrants.add(new SelectedHydrantDto("H201905", 1.8855676890037039));

        NearestHydrantsToFireDto nearestHydrantsToFireDto = new NearestHydrantsToFireDto(11.781901264982816, hydrants);
        String outputExpectedStr = mapToJson(nearestHydrantsToFireDto);

        MvcResult mvcResult = this.mockMvc.perform(req)
                .andExpect(content().string(containsString(outputExpectedStr)))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andDo(print())
                .andReturn();
    }

    @Test
    public void noHydrantFoundTest() throws Exception {
        FireInfoDto fireInfoDto = new FireInfoDto();
        fireInfoDto.setLatitude(41.8822168);
        fireInfoDto.setLongitude(-73.59157092);
        fireInfoDto.setNumberOfFireTrucks(3L);

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

    @Test
    public void notEnoughHydrantsTest() throws Exception {
        FireInfoDto fireInfoDto = new FireInfoDto();
        fireInfoDto.setLatitude(41.9022168);
        fireInfoDto.setLongitude(-74.04157092);
        fireInfoDto.setNumberOfFireTrucks(10L);

        String inputStr = mapToJson(fireInfoDto);
        RequestBuilder req = post(FIND_NEAREST_FIREHOSES_QUERY_URI)
                .contentType(MediaType.APPLICATION_JSON) // for DTO
                .content(inputStr);

        List<SelectedHydrantDto> hydrants = new ArrayList<>();
        hydrants.add(new SelectedHydrantDto("H200491", 998.4490847378547));
        hydrants.add(new SelectedHydrantDto("H200444", 998.8254381124846));
        hydrants.add(new SelectedHydrantDto("H200431", 999.0427717059548));
        hydrants.add(new SelectedHydrantDto("H200432", 999.124828283077));
        hydrants.add(new SelectedHydrantDto("H200445", 999.1502459461211));
        hydrants.add(new SelectedHydrantDto("H200456", 999.4744731559955));
        hydrants.add(new SelectedHydrantDto("H200437", 999.6420718983815));
        hydrants.add(new SelectedHydrantDto("H200457", 999.7586430780082));
        hydrants.add(new SelectedHydrantDto("H200434", 999.8647435028711));
        NearestHydrantsToFireDto nearestHydrantsToFireDto = new NearestHydrantsToFireDto(8993.332300420749, hydrants);
        String outputExpectedStr = mapToJson(nearestHydrantsToFireDto);


        MvcResult mvcResult = this.mockMvc.perform(req)
                .andExpect(content().string(containsString(outputExpectedStr)))
                .andExpect(content().string(containsString(ErrorConstants.BusinessError.NOT_ENOUGH_HYDRANTS_MSG)))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andDo(print())
                .andReturn();


    }

    @Test
    public void normalBusinessTest3() throws Exception {
        FireInfoDto fireInfoDto = new FireInfoDto();
        PointDto pointDto = new PointDto("point", new Double[]{-73.78156804377382,40.7422177771488});
        fireInfoDto.setTheGeom(pointDto);
        fireInfoDto.setNumberOfFireTrucks(3L);

        String inputStr = mapToJson(fireInfoDto);
        RequestBuilder req = post(FIND_NEAREST_FIREHOSES_CODE_URI)
                .contentType(MediaType.APPLICATION_JSON) // for DTO
                .content(inputStr);

        List<SelectedHydrantDto> hydrants = new ArrayList<>();
        hydrants.add(new SelectedHydrantDto("H421018", 0.4182424459892279));
        hydrants.add(new SelectedHydrantDto("H421029", 0.49217610735438394));
        hydrants.add(new SelectedHydrantDto("H421028", 0.5402413377683098));
        NearestHydrantsToFireDto nearestHydrantsToFireDto = new NearestHydrantsToFireDto(1.4506598911119217, hydrants);
        String outputExpectedStr = mapToJson(nearestHydrantsToFireDto);

        MvcResult mvcResult = this.mockMvc.perform(req)
                .andExpect(content().string(containsString(outputExpectedStr)))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andDo(print())
                .andReturn();
    }


}
