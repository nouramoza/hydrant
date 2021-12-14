package com.bookiply.interview.assignment.service.impl;

import com.bookiply.interview.assignment.service.NearestFireHosesService;
import com.bookiply.interview.assignment.web.dto.*;
import com.bookiply.interview.assignment.web.error.BadRequestAlertException;
import com.bookiply.interview.assignment.web.error.ErrorConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
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
     * @return GenericRestResponse
     */
    @Override
    public GenericRestResponse getNearestHydrants(FireInfoDto fireInfoDto) throws BadRequestAlertException, JsonProcessingException {
        checkValidations(fireInfoDto);

        List<HydrantDto> hydrantDtoList = getAreaFireBrigades(fireInfoDto);

        List<SelectedHydrantDto> sortedHydrants = sortHydrants(fireInfoDto, hydrantDtoList);
        NearestHydrantsToFireDto nearestHydrantsToFireDto = findNearestHydrant(fireInfoDto, sortedHydrants);

        return new GenericRestResponse(GenericRestResponse.STATUS.SUCCESS, "SUCCESS", nearestHydrantsToFireDto);
    }

    /**
     * get N nearest hydrants and total length of fire hoses
     *
     * @param fireInfoDto consists of fire coordination and number of trucks
     * @return NearestHydrantsToFireDto - total fire hoses length in meters
     *                                  - list of N nearest hydrants used by the fire brigade, with its unitId and distance to the fire
     */
    @Override
    public NearestHydrantsToFireDto getNearestHydrantsCodeBase(FireInfoDto fireInfoDto) throws BadRequestAlertException, JsonProcessingException {

        checkValidations(fireInfoDto);

        List<HydrantDto> hydrantDtoList = getAreaFireBrigades(fireInfoDto);

        List<SelectedHydrantDto> sortedHydrants = sortHydrants(fireInfoDto, hydrantDtoList);

        return findNearestHydrant(fireInfoDto, sortedHydrants);
    }

    /**
     * get N Nearest hydrants and total length of fire hoses
     *
     * @param fireInfoDto consists of fire coordination and number of trucks
     * @return NearestHydrantsToFireDto - total fire hoses length in meters
     *                                  - list of N Nearest hydrants used by the fire brigade, with its unitId and distance to the fire
     */
    @Override
    public NearestHydrantsToFireDto getNearestHydrantsQueryBase(FireInfoDto fireInfoDto) throws BadRequestAlertException, JsonProcessingException {

        checkValidations(fireInfoDto);

        List<HydrantDto> hydrantDtoList = getSortedNearestFireBrigades(fireInfoDto);

        List<SelectedHydrantDto> sortedHydrants = generateSelectedHydrants(fireInfoDto, hydrantDtoList);

        return generateNearestHydrantsDto(sortedHydrants);

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
    private List<HydrantDto> getAreaFireBrigades(FireInfoDto fireInfoDto) throws JsonProcessingException, BadRequestAlertException {
        List<HydrantDto> hydrantDtoList;

        StringBuilder builder = new StringBuilder("https://data.cityofnewyork.us/resource/5bgh-vtsn.json?$order=latitude");
//        builder.append(fireInfoDto.getTheGeom().getCoordinates()[0]);
//        builder.append(",%20");
//        builder.append(fireInfoDto.getTheGeom().getCoordinates()[1]);
//        builder.append(",%20100)");
        URI uri = URI.create(builder.toString());
//        String uri = "https://data.cityofnewyork.us/resource/5bgh-vtsn.json?$where=within_circle(the_geom,%20"
//                + fireInfoDto.getTheGeom().getCoordinates()[0] + ",%20" + fireInfoDto.getTheGeom().getCoordinates()[1] +
//                ",%20100)";

//        String uri = "https://data.cityofnewyork.us/resource/5bgh-vtsn.json";

//        String uri = "https://data.cityofnewyork.us/resource/5bgh-vtsn.json?$where=within_circle(the_geom,%2040.71,%20-74.0,%20100)";

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

    private List<HydrantDto> getSortedNearestFireBrigades(FireInfoDto fireInfoDto) throws JsonProcessingException, BadRequestAlertException {
        List<HydrantDto> hydrantDtoList;

        Double fireX = fireInfoDto.getTheGeom().getCoordinates()[0];
        Double fireY = fireInfoDto.getTheGeom().getCoordinates()[1];

        StringBuilder str = new StringBuilder("SELECT unitid,latitude,longitude ORDER BY ((latitude+(");
        str.append(-fireX);
        str.append("))*(latitude+(");
        str.append(-fireX);
        str.append(")))+((longitude+(");
        str.append(-fireY);
        str.append("))*(longitude+(");
        str.append(-fireY);
        str.append("))) limit ");
        str.append(fireInfoDto.getNumberOfFireTrucks());

        String url = "https://data.cityofnewyork.us/resource/5bgh-vtsn.json";
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("$query", str);
        URI uriPlus = builder.encode().build(false).toUri();

        String strictlyEscapedQuery = StringUtils.replace(uriPlus.getRawQuery(), "+", "%2B");
        URI uri = UriComponentsBuilder.fromUri(uriPlus)
                .replaceQuery(strictlyEscapedQuery)
                .build(true).toUri();

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
     * @param fireInfoDto consists of fire coordination and number of trucks
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
                    Double xDistance = hydrantDtoList.get(i).getLatitude() - fireX,
                            yDistance = hydrantDtoList.get(i).getLongitude() - fireY;
                    SelectedHydrantDto selectedHydrantDto = new SelectedHydrantDto(hydrantDtoList.get(i).getUnitid(),
                            (Math.sqrt((xDistance * xDistance) + (yDistance * yDistance))) * 1000);
                    finalSelectedHydrantDtoList.add(selectedHydrantDto);
                });
        return finalSelectedHydrantDtoList;
    }

    /**
     * find N nearest hydrants from the list of given hydrants and total length of firehoses. this method is the main business of the app
     *
     * @param fireInfoDto            consists of fire coordination and number of trucks
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
