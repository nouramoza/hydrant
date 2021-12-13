package com.bookiply.interview.assignment.web.controler;


import com.bookiply.interview.assignment.service.NearestFireHosesService;
import com.bookiply.interview.assignment.web.dto.GenericRestResponse;
import com.bookiply.interview.assignment.web.dto.FireInfoDto;
import com.bookiply.interview.assignment.web.dto.NearestHydrantsToFireDto;
import com.bookiply.interview.assignment.web.dto.PointDto;
import com.bookiply.interview.assignment.web.error.BadRequestAlertException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.awt.*;

@RestController
@RequestMapping("/api/v1/nearest-service")
@Api(value = "Find N Nearest fireHoses API")
public class NearestServiceController {
    Logger log = LoggerFactory.getLogger(NearestServiceController.class);
    private NearestFireHosesService nearestFireHosesService;

    public NearestServiceController(NearestFireHosesService nearestFireHosesService) {
        this.nearestFireHosesService = nearestFireHosesService;
    }

    @PostMapping("/findNearestFireHoses")
    @ApiOperation(value = "REST request to Find N Nearest FireHoses and Total FireHoses Length",
            produces = "Application/JSON", response = NearestHydrantsToFireDto.class, httpMethod = "POST")
    public GenericRestResponse findNearestFireHoses(
            @ApiParam(value = "HydrantDto", required = true)
            @RequestBody FireInfoDto fireInfoDto) throws BadRequestAlertException, JsonProcessingException {
        log.debug("REST request to Find N Nearest FireHoses and Total FireHoses Length");
        return nearestFireHosesService.getNearestHydrants(fireInfoDto);
    }

//    @PostMapping("/findNearestFireHoses2")
//    @ApiOperation(value = "REST request to Find N Nearest FireHoses and Total FireHoses Length",
//            produces = "Application/JSON", response = NearestHydrantsToFireDto.class, httpMethod = "POST")
//    public GenericRestResponse cardVerification(
//            @ApiParam(value = "HydrantDto", required = true)
//            @RequestBody HydrantDto hydrantDto) {
//        log.debug("REST request to Find N Nearest FireHoses and Total FireHoses Length");
//        return nearestFireHosesService.getNearestHydrants(hydrantDto);
//    }

    @PostMapping("/findNearestFireHosesJsonOut")
    @ApiOperation(value = "REST request to Find N Nearest FireHoses and Total FireHoses Length",
            produces = "Application/JSON", response = NearestHydrantsToFireDto.class, httpMethod = "POST")
    public NearestHydrantsToFireDto findNearestFireHosesJsonOut(
            @ApiParam(value = "HydrantDto", required = true)
            @RequestBody FireInfoDto fireInfoDto) throws BadRequestAlertException, JsonProcessingException {
        log.debug("REST request to Find N Nearest FireHoses and Total FireHoses Length");
        return nearestFireHosesService.getNearestHydrantsJsonOut(fireInfoDto);
    }

    @GetMapping("/getInputJson")
    @ApiOperation(value = "REST request to getInputJson",
            produces = "Application/JSON", response = FireInfoDto.class, httpMethod = "GET")
    public String getInputJson() throws JsonProcessingException {
        Double[] ss = {40.43, -74.1};
        FireInfoDto fireInfoDto = new FireInfoDto(new PointDto("point", ss), 2L);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(fireInfoDto);
    }
}
