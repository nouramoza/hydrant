package com.bookiply.interview.assignment.web.controler;

import com.bookiply.interview.assignment.service.NearestFireHosesService;
import com.bookiply.interview.assignment.web.dto.NearestHydrantsToFireDto;
import com.bookiply.interview.assignment.web.dto.GenericRestResponse;
import com.bookiply.interview.assignment.web.dto.FireInfoDto;
import com.bookiply.interview.assignment.web.error.BadRequestAlertException;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/findHydrant-service")
@Api(value = "Find N Nearest fireHoses API")
public class NearestServiceController {
    Logger log = LoggerFactory.getLogger(NearestServiceController.class);
    private NearestFireHosesService nearestFireHosesService;

    public NearestServiceController(NearestFireHosesService nearestFireHosesService) {
        this.nearestFireHosesService = nearestFireHosesService;
    }

    @PostMapping("/findNearestFireHosesCodeBase")
    @ApiOperation(value = "REST request to Find N Nearest FireHoses and Total FireHoses Length",
            produces = "Application/JSON", response = NearestHydrantsToFireDto.class, httpMethod = "POST")
    public GenericRestResponse findNearestFireHoses(
            @ApiParam(value = "HydrantDto", required = true)
            @RequestBody FireInfoDto fireInfoDto) throws BadRequestAlertException, JsonProcessingException {
        log.debug("REST request to Find N Nearest FireHoses and Total FireHoses Length");
        return nearestFireHosesService.getNearestHydrantsCodeBase(fireInfoDto);
    }

    @PostMapping("/findNearestFireHosesQueryBase")
    @ApiOperation(value = "REST request to Find N Nearest FireHoses and Total FireHoses Length",
            produces = "Application/JSON", response = NearestHydrantsToFireDto.class, httpMethod = "POST")
    public GenericRestResponse findNearestFireHosesQueryBase(
            @ApiParam(value = "HydrantDto", required = true)
            @RequestBody FireInfoDto fireInfoDto) throws BadRequestAlertException, JsonProcessingException {
        log.debug("REST request to Find N Nearest FireHoses and Total FireHoses Length");
        return nearestFireHosesService.getNearestHydrantsQueryBase(fireInfoDto);
    }

}
