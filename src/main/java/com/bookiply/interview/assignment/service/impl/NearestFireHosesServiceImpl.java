package com.bookiply.interview.assignment.service.impl;

import com.bookiply.interview.assignment.service.NearestFireHosesService;
import com.bookiply.interview.assignment.utils.ConstantsUtil;
import com.bookiply.interview.assignment.web.dto.*;
import com.bookiply.interview.assignment.web.error.BadRequestAlertException;
import com.bookiply.interview.assignment.web.error.ErrorConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Service Implementation for finding nearest hydrants and total length.
 */

@Service
public class NearestFireHosesServiceImpl implements NearestFireHosesService {

    private static final String FIRE_INFO_DTO = "fireInfoDto";

    /**
     * get N nearest hydrants and total length of fire hoses
     *
     * @param fireInfoDto consists of fire coordination and number of trucks
     * @return NearestHydrantsToFireDto - total fire hoses length in meters
     * - list of N nearest hydrants used by the fire brigade, with its unitId and distance to the fire
     */
    @Override
    public GenericRestResponse getNearestHydrantsCodeBase(FireInfoDto fireInfoDto) throws BadRequestAlertException {
        checkValidations(fireInfoDto);

        List<HydrantDto> hydrantDtoList = getAreaFireBrigades(fireInfoDto);

        List<SelectedHydrantDto> sortedHydrants = sortHydrants(fireInfoDto, hydrantDtoList);

        NearestHydrantsToFireDto nearestHydrantsToFireDto = findNearestHydrant(fireInfoDto, sortedHydrants);

        return new GenericRestResponse(GenericRestResponse.STATUS.SUCCESS, "SUCCESS", nearestHydrantsToFireDto);
    }

    /**
     * get N Nearest hydrants and total length of fire hoses
     *
     * @param fireInfoDto consists of fire coordination and number of trucks
     * @return NearestHydrantsToFireDto - total fire hoses length in meters
     * - list of N Nearest hydrants used by the fire brigade, with its unitId and distance to the fire
     */
    @Override
    public GenericRestResponse getNearestHydrantsQueryBase(FireInfoDto fireInfoDto) throws BadRequestAlertException {

        checkValidations(fireInfoDto);

        List<HydrantDto> hydrantDtoList = test2(fireInfoDto);

        List<SelectedHydrantDto> sortedHydrants = generateSelectedHydrants(fireInfoDto, hydrantDtoList);

        for (int i =0; i < sortedHydrants.size(); i++) {
            System.out.println(hydrantDtoList.get(i).toString());
            System.out.println("distance to fire ==> " + sortedHydrants.get(i).getDistanceToFire());
        }

        NearestHydrantsToFireDto nearestHydrantsToFireDto = generateNearestHydrantsDto(sortedHydrants);

        return new GenericRestResponse(GenericRestResponse.STATUS.SUCCESS, "SUCCESS", nearestHydrantsToFireDto);

    }

    /**
     * checks input values correctness
     *
     * @param fireInfoDto consists of fire coordination and number of trucks
     */
    private void checkValidations(FireInfoDto fireInfoDto) throws BadRequestAlertException {
        if (fireInfoDto.getNumberOfFireTrucks() < 1) {
            throw new BadRequestAlertException(ErrorConstants.InputValidationMessage.WRONG_TRUCK_NUMBERS_MSG,
                    FIRE_INFO_DTO, ErrorConstants.InputValidationMessage.WRONG_TRUCK_NUMBERS_KEY);
        }
    }

    /**
     * get hydrants in a circular dimension
     *
     * @param fireInfoDto consists of fire coordination and number of trucks
     * @return List<HydrantDto> list of hydrant in the given circular dimension
     */
    private List<HydrantDto> getAreaFireBrigades(FireInfoDto fireInfoDto) throws BadRequestAlertException {

        StringBuilder builder = new StringBuilder("https://data.cityofnewyork.us/resource/5bgh-vtsn.json?$order=latitude");
        builder.append(fireInfoDto.getTheGeom().getCoordinates()[0]).append(",");
        builder.append(fireInfoDto.getTheGeom().getCoordinates()[1]).append(", 1000");

        URI uri = URI.create(builder.toString());

        return callApi(uri);
    }

    /**
     * get N nearest hydrants based on our input data
     *
     * @param fireInfoDto consists of fire coordination and number of trucks
     * @return List<HydrantDto> list of n nearest hydrants based on fires coordinate and number of trucks
     */
    private List<HydrantDto> getSortedNearestFireBrigades(FireInfoDto fireInfoDto) throws BadRequestAlertException {

        String url = "https://data.cityofnewyork.us/resource/5bgh-vtsn.json";

        StringBuilder whereStr = new StringBuilder("distance_in_meters(the_geom,'POINT(");
        whereStr.append(fireInfoDto.getTheGeom().getCoordinates()[0]);
        whereStr.append(" ");
        whereStr.append(fireInfoDto.getTheGeom().getCoordinates()[1]);
        whereStr.append(")') <= 1000000");


        StringBuilder orderStr = new StringBuilder("distance_in_meters(the_geom,'POINT(");
        orderStr.append(fireInfoDto.getTheGeom().getCoordinates()[0]);
        orderStr.append(" ");
        orderStr.append(fireInfoDto.getTheGeom().getCoordinates()[1]);
        orderStr.append(")')");

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
//                .queryParam("$where", whereStr)
                .queryParam("$order", orderStr)
                .queryParam("$limit", fireInfoDto.getNumberOfFireTrucks())
                ;

//        URI uri = builder.encode().build(false).toUri();

        URI uri = UriComponentsBuilder.fromUri(builder.encode().build(false).toUri())
                .build(true).toUri();
        return callApi(uri);
    }

    private List<HydrantDto> test2(FireInfoDto fireInfoDto) throws BadRequestAlertException {

        Double fireX = fireInfoDto.getTheGeom().getCoordinates()[0];
        Double fireY = fireInfoDto.getTheGeom().getCoordinates()[1];

        StringBuilder orderStr = new StringBuilder("((latitude+(");
        orderStr.append(-fireY);
        orderStr.append("))*(latitude+(");
        orderStr.append(-fireY);
        orderStr.append(")))+((longitude+(");
        orderStr.append(-fireX);
        orderStr.append("))*(longitude+(");
        orderStr.append(-fireX);
        orderStr.append(")))");

        StringBuilder whereStr = new StringBuilder(orderStr.toString());
        whereStr.append("<=");
        whereStr.append(ConstantsUtil.DefaultValues.MAX_LENGTH_OF_FIRE_HOSE);

        String url = "https://data.cityofnewyork.us/resource/5bgh-vtsn.json";
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("$where", whereStr)
                .queryParam("$order", orderStr)
                .queryParam("$limit", fireInfoDto.getNumberOfFireTrucks());
        URI uriPlus = builder.encode().build(false).toUri();

        String strictlyEscapedQuery = StringUtils.replace(uriPlus.getRawQuery(), "+", "%2B");
        URI uri = UriComponentsBuilder.fromUri(uriPlus)
                .replaceQuery(strictlyEscapedQuery)
                .build(true).toUri();
        return callApi(uri);
    }

    private List<HydrantDto> callApi(URI uri) throws BadRequestAlertException {
        List<HydrantDto> hydrantDtoList = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String resultStr = restTemplate.getForObject(uri, String.class);
            ObjectMapper mapper = new ObjectMapper();
            hydrantDtoList = mapper.readValue(resultStr, new TypeReference<List<HydrantDto>>() {
            });
        } catch (Exception e) {
            throw new BadRequestAlertException(e.getMessage(),
                    FIRE_INFO_DTO, ErrorConstants.InputValidationMessage.API_EXCEPTION_KEY);
        }
        return hydrantDtoList;
    }

    /**
     * sort the found hydrants by their distance to the fire coordinate
     *
     * @param fireInfoDto    consists of fire coordination and number of trucks
     * @param hydrantDtoList all the hydrants that found in the given circular dimension
     * @return List<SelectedHydrantDto> list of sorted selected hydrants in the given circular dimension
     */
    private List<SelectedHydrantDto> sortHydrants(FireInfoDto fireInfoDto, List<HydrantDto> hydrantDtoList) {
        List<SelectedHydrantDto> sortedHydrantDtoList = new ArrayList<>();
        Double fireX = fireInfoDto.getTheGeom().getCoordinates()[0],
                fireY = fireInfoDto.getTheGeom().getCoordinates()[1];

        List<SelectedHydrantDto> finalSelectedHydrantDtoList = sortedHydrantDtoList;
        IntStream.range(0, hydrantDtoList.size())
                .forEach(i -> {
                    Double xDistance = hydrantDtoList.get(i).getThe_geom().getCoordinates()[0] - fireX,
                            yDistance = hydrantDtoList.get(i).getThe_geom().getCoordinates()[1] - fireY;
                    SelectedHydrantDto selectedHydrantDto = new SelectedHydrantDto(hydrantDtoList.get(i).getUnitid(),
                            Math.sqrt((xDistance * xDistance) + (yDistance * yDistance)));
                    finalSelectedHydrantDtoList.add(selectedHydrantDto);
                });
        sortedHydrantDtoList = sortedHydrantDtoList.stream()
                .sorted(Comparator.comparing(SelectedHydrantDto::getDistanceToFire))
                .collect(Collectors.toList());

        return sortedHydrantDtoList;
    }

    private List<SelectedHydrantDto> generateSelectedHydrants(FireInfoDto fireInfoDto, List<HydrantDto> hydrantDtoList) {
        Double fireX = fireInfoDto.getTheGeom().getCoordinates()[0],
                fireY = fireInfoDto.getTheGeom().getCoordinates()[1];
        List<SelectedHydrantDto> finalSelectedHydrantDtoList = new ArrayList<>();
        IntStream.range(0, hydrantDtoList.size())
                .forEach(i -> {
                    Double xDistance = hydrantDtoList.get(i).getLongitude() - fireX,
                            yDistance = hydrantDtoList.get(i).getLatitude() - fireY;
                    SelectedHydrantDto selectedHydrantDto = new SelectedHydrantDto(hydrantDtoList.get(i).getUnitid(),
                            (Math.sqrt((xDistance * xDistance) + (yDistance * yDistance))) * 1000);
                    finalSelectedHydrantDtoList.add(selectedHydrantDto);
                });
        return finalSelectedHydrantDtoList;
    }

    /**
     * find N nearest hydrants from the list of given hydrants and total length of firehoses. this method is the main business of the app
     *
     * @param fireInfoDto          consists of fire coordination and number of trucks
     * @param sortedHydrantDtoList list of hydrants in the circular dimension
     * @return NearestHydrantsToFireDto - total firehoses length in meters
     * - list of N nearest hydrants used by the fire brigade, with its unitId and distance to the fire
     */
    private NearestHydrantsToFireDto findNearestHydrant(FireInfoDto fireInfoDto, List<SelectedHydrantDto> sortedHydrantDtoList) {
        sortedHydrantDtoList = sortedHydrantDtoList.subList(0, Math.toIntExact(fireInfoDto.getNumberOfFireTrucks()));
        return generateNearestHydrantsDto(sortedHydrantDtoList);
    }

    private NearestHydrantsToFireDto generateNearestHydrantsDto(List<SelectedHydrantDto> sortedHydrantDtoList) {
        Double totalFirehosesLength = sortedHydrantDtoList.stream()
                .mapToDouble(SelectedHydrantDto::getDistanceToFire)
                .sum();
        return new NearestHydrantsToFireDto(totalFirehosesLength, sortedHydrantDtoList);
    }
}
