package com.bookiply.interview.assignment.service.impl;

import com.bookiply.interview.assignment.service.NearestFireHosesService;
import com.bookiply.interview.assignment.web.dto.*;
import com.bookiply.interview.assignment.web.error.BadRequestAlertException;
import com.bookiply.interview.assignment.web.error.ErrorConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for finding nearest hydrants and total legnth.
 */

@Service
public class NearestFireHosesServiceImpl implements NearestFireHosesService {

    private static final String INPUT_DTO = "inputDto";

    /**
     * get N nearest hydrants and total length of fire hoses
     *
     * @param fireInfoDto consists of fire coorditation and number of trucks
     * @return GenericRestResponse
     */
    @Override
    public GenericRestResponse getNearestHydrants(FireInfoDto fireInfoDto) throws BadRequestAlertException, JsonProcessingException {
        checkValidations(fireInfoDto);
        List<HydrantDto> hydrantDtoList = getAreaFireBrigades(fireInfoDto);
        NearestHydrantsToFireDto nearestHydrantsToFireDto = findNearestHydrant(fireInfoDto, hydrantDtoList);
        return new GenericRestResponse(GenericRestResponse.STATUS.SUCCESS, nearestHydrantsToFireDto.toString());
    }

    /**
     * get N nearest hydrants and total length of fire hoses
     *
     * @param fireInfoDto consists of fire coorditation and number of trucks
     * @return NearestHydrantsToFireDto - total firehoses length in meters
     * - list of N nearest hydrants used by the fire brigade, with its unitId and distance to the fire
     */
    @Override
    public NearestHydrantsToFireDto getNearestHydrantsJsonOut(FireInfoDto fireInfoDto) throws BadRequestAlertException, JsonProcessingException {

        checkValidations(fireInfoDto);
        /** find hydrants in a circular */
        List<HydrantDto> hydrantDtoList = getAreaFireBrigades(fireInfoDto);


//        test(hydrantDtoList);

        List<SelectedHydrantDto> sortedHydrants = sortHydrants(hydrantDtoList);
        return findNearestHydrant(fireInfoDto, hydrantDtoList);
    }


    /**
     * checks input values correctness
     *
     * @param fireInfoDto consists of fire coordination and number of trucks
     */
    private void checkValidations(FireInfoDto fireInfoDto) throws BadRequestAlertException {
        if (fireInfoDto.getNumberOfFireTrucks() < 1) {
            throw new BadRequestAlertException(ErrorConstants.InputValidationMessage.WRONG_TRUCK_NUMBERS_MSG,
                    INPUT_DTO, ErrorConstants.InputValidationMessage.WRONG_TRUCK_NUMBERS_KEY);
        }
    }

    /**
     * get hydrants in a cicular dimension
     *
     * @param fireInfoDto consists of fire coordination and number of trucks
     * @return List<HydrantDto> list of hydrant in the given circular dimension
     */
    private List<HydrantDto> getAreaFireBrigades(FireInfoDto fireInfoDto) throws JsonProcessingException, BadRequestAlertException {
        List<HydrantDto> hydrantDtoList;
//        String uri = "https://data.cityofnewyork.us/resource/5bgh-vtsn.json?$where=within_circle(the_geom,%20"
//                + fireInfoDto.getTheGeom().getCoordinates()[0] + ",%20" + fireInfoDto.getTheGeom().getCoordinates()[1] +
//                ",%20100)";
//
        String uri = "https://data.cityofnewyork.us/resource/5bgh-vtsn.json";

//        String uri = "https://data.cityofnewyork.us/resource/5bgh-vtsn.json?$where=within_circle(the_geom,%2040.71,%20-74.0,%20100)";


        try {
            RestTemplate restTemplate = new RestTemplate();
            String resultStr = restTemplate.getForObject(uri, String.class);
            ObjectMapper mapper = new ObjectMapper();
            hydrantDtoList = mapper.readValue(resultStr, new TypeReference<List<HydrantDto>>() {
            });
        } catch (Exception e) {
            throw new BadRequestAlertException(ErrorConstants.InputValidationMessage.API_EXCEPTION_MSG,
                    INPUT_DTO, ErrorConstants.InputValidationMessage.API_EXCEPTION_KEY);
        }
        return hydrantDtoList;
    }

    /**
     * sort the found hydrant by their distance to the fire coordinate
     *
     * @param hydrantDtoList all the hydrants that found in the given circular dimension
     * @return List<SelectedHydrantDto> list of sorted selected hydrants in the given circular dimension
     */
    private List<SelectedHydrantDto> sortHydrants(List<HydrantDto> hydrantDtoList) {
        int n = hydrantDtoList.size();
        List<SelectedHydrantDto> selectedHydrantDtoList = new ArrayList<>();
        for(int i = 0; i < n; i++) {
            Double x = hydrantDtoList.get(i).getThe_geom().getCoordinates()[0],
                    y = hydrantDtoList.get(i).getThe_geom().getCoordinates()[1];
            SelectedHydrantDto selectedHydrantDto = new SelectedHydrantDto(hydrantDtoList.get(i).getUnitid(),
                    (x * x) + (y * y));
            selectedHydrantDtoList.add(selectedHydrantDto);
        }

        selectedHydrantDtoList = selectedHydrantDtoList.stream()
                .sorted(Comparator.comparing(SelectedHydrantDto::getDistanceToFire))
                .collect(Collectors.toList());

        return selectedHydrantDtoList;
    }


    /**
     * find N nearest hydrants from the list of given hydrants and total length of firehoses. this method is the main business of the app
     *
     * @param fireInfoDto    consists of fire coordination and number of trucks
     * @param hydrantDtoList list of hydrants in the circular dimension
     * @return NearestHydrantsToFireDto - total firehoses length in meters
     * - list of N nearest hydrants used by the fire brigade, with its unitId and distance to the fire
     */
    private NearestHydrantsToFireDto findNearestHydrant(FireInfoDto fireInfoDto, List<HydrantDto> hydrantDtoList) {
        List<SelectedHydrantDto> selectedHydrantDtoList = new ArrayList<>();
        Double totalFirehosesLength = 0.0;
        Double maxDistance = 0.0;
        int maxDistanceIndex = 0;
        int index = 0;

//        hydrantDtoList.stream()
//                .forEach(hydrantDto -> {
//                    Double distanceToFire = Math.sqrt(Math.pow(Math.abs((fireInfoDto.getTheGeom().getCoordinates()[0] -
//                            hydrantDto.getThe_geom().getCoordinates()[0])), 2)
//                            + Math.pow(Math.abs((fireInfoDto.getTheGeom().getCoordinates()[1] - hydrantDto.getThe_geom().getCoordinates()[1])), 2));
//
//                    if (selectedHydrantDtoList.size() < fireInfoDto.getNumberOfFireTrucks()) {
//                        totalFirehosesLength[0].updateAndGet(v -> v + distanceToFire);
//                        selectedHydrantDtoList.add(new SelectedHydrantDto(hydrantDto.getUnitid(), distanceToFire));
//                        if (maxDistance[0] < distanceToFire) {
//                            maxDistance[0] = distanceToFire;
//                            maxDistanceIndex[0] = index[0];
//                        }
//                    } else if (maxDistance[0] > distanceToFire) {
//                        totalFirehosesLength[0].updateAndGet(v -> v - (maxDistance[0] - distanceToFire));
//                        selectedHydrantDtoList.remove(maxDistanceIndex[0]);
//                        selectedHydrantDtoList.add(new SelectedHydrantDto(hydrantDto.getUnitid(), distanceToFire));
//                        maxDistance[0] = distanceToFire;
//                        maxDistanceIndex[0] = index[0];
//                    }
//                    index[0]++;
//
//                });


        for (HydrantDto hydrantDto : hydrantDtoList) {
            Double distanceToFire = Math.sqrt(Math.pow(Math.abs((fireInfoDto.getTheGeom().getCoordinates()[0] -
                    hydrantDto.getThe_geom().getCoordinates()[0])), 2)
                    + Math.pow(Math.abs((fireInfoDto.getTheGeom().getCoordinates()[1] - hydrantDto.getThe_geom().getCoordinates()[1])), 2));
            if (selectedHydrantDtoList.size() < fireInfoDto.getNumberOfFireTrucks()) {
                totalFirehosesLength += distanceToFire;
                selectedHydrantDtoList.add(new SelectedHydrantDto(hydrantDto.getUnitid(), distanceToFire));
                if (maxDistance < distanceToFire) {
                    maxDistance = distanceToFire;
                    maxDistanceIndex = index;
                }
            } else if (maxDistance > distanceToFire) {
                totalFirehosesLength -= (maxDistance - distanceToFire);
                selectedHydrantDtoList.remove(maxDistanceIndex);
                selectedHydrantDtoList.add(new SelectedHydrantDto(hydrantDto.getUnitid(), distanceToFire));
                maxDistance = distanceToFire;
                maxDistanceIndex = index;
            }
            index++;
        }
        return new NearestHydrantsToFireDto(totalFirehosesLength * 1000, selectedHydrantDtoList);
    }

    private void test(List<HydrantDto> hydrantDtos) {
        HydrantDto minX = hydrantDtos.stream()
                .min(Comparator.comparingDouble(HydrantDto::getLatitude))
                .get();
        HydrantDto maxX = hydrantDtos.stream()
                .max(Comparator.comparingDouble(HydrantDto::getLatitude))
                .get();
        HydrantDto minY = hydrantDtos.stream()
                .min(Comparator.comparingDouble(HydrantDto::getLongitude))
                .get();
        HydrantDto maxY = hydrantDtos.stream()
                .max(Comparator.comparingDouble(HydrantDto::getLongitude))
                .get();

        System.out.println("minX ==> " + minX.toString());
        System.out.println("maxX ==> " + maxX.toString());
        System.out.println("minY ==> " + minY.toString());
        System.out.println("maxY ==> " + maxY.toString());


    }
}
