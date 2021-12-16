package com.bookiply.interview.assignment.service.impl;

import com.bookiply.interview.assignment.service.NearestFireHosesService;
import com.bookiply.interview.assignment.utils.CalculationsHelper;
import com.bookiply.interview.assignment.utils.ConstantsUtil;
import com.bookiply.interview.assignment.web.dto.*;
import com.bookiply.interview.assignment.web.error.BadRequestAlertException;
import com.bookiply.interview.assignment.web.error.BusinessAlertException;
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

import static com.bookiply.interview.assignment.utils.CalculationsHelper.getDistanceFromLatLonInMeters;

/**
 * Service Implementation for finding nearest hydrants and total length.
 */

@Service
public class NearestFireHosesServiceImpl implements NearestFireHosesService {

    private static final String FIRE_INFO_DTO = "fireInfoDto";

    /**
     * get N nearest hydrants and total length of fire hoses, all business done in code
     *
     * @param fireInfoDto consists of fire coordination and number of trucks
     * @return NearestHydrantsToFireDto - total fire hoses length in meters
     * - list of N nearest hydrants used by the fire brigade, with its unitId and distance to the fire
     */
    @Override
    public GenericRestResponse getNearestHydrantsCodeBase(FireInfoDto fireInfoDto) throws BadRequestAlertException, BusinessAlertException {
        checkCodeBaseValidation(fireInfoDto);

        List<HydrantDto> hydrantDtoList = getAreaFireBrigades(fireInfoDto);

        List<SelectedHydrantDto> sortedHydrants = sortHydrants(fireInfoDto, hydrantDtoList);

        NearestHydrantsToFireDto nearestHydrantsToFireDto = findNearestHydrant(fireInfoDto, sortedHydrants);

        return generateResponse(nearestHydrantsToFireDto, fireInfoDto);
    }

    /**
     * get N Nearest hydrants and total length of fire hoses, most of business done in dataBase side
     *
     * @param fireInfoDto consists of fire coordination and number of trucks
     * @return NearestHydrantsToFireDto - total fire hoses length in meters
     * - list of N Nearest hydrants used by the fire brigade, with its unitId and distance to the fire
     */
    @Override
    public GenericRestResponse getNearestHydrantsQueryBase(FireInfoDto fireInfoDto) throws BadRequestAlertException, BusinessAlertException {

        checkQueryBaseValidation(fireInfoDto);

        List<HydrantDto> hydrantDtoList = getSortedNearestFireBrigades(fireInfoDto);

        NearestHydrantsToFireDto nearestHydrantsToFireDto = generateSelectedHydrants(fireInfoDto, hydrantDtoList);
        return generateResponse(nearestHydrantsToFireDto, fireInfoDto);
    }

    /**
     * checks input values correctness
     *
     * @param fireInfoDto consists of fire coordination and number of trucks
     */
    private void checkCodeBaseValidation(FireInfoDto fireInfoDto) throws BadRequestAlertException {
        checkValidations(fireInfoDto);
        if (fireInfoDto.getTheGeom().getCoordinates() == null) {
            throw new BadRequestAlertException(ErrorConstants.InputValidationMessage.EMPTY_GEOM_MSG,
                    FIRE_INFO_DTO, ErrorConstants.InputValidationMessage.EMPTY_GEOM_KEY);
        }
        if (fireInfoDto.getTheGeom().getCoordinates()[0] < ConstantsUtil.DefaultValues.MIN_LONGITUDE ||
                fireInfoDto.getTheGeom().getCoordinates()[0] > ConstantsUtil.DefaultValues.MAX_LONGITUDE) {
            throw new BadRequestAlertException(ErrorConstants.InputValidationMessage.LONG_RANGE_MSG,
                    FIRE_INFO_DTO, ErrorConstants.InputValidationMessage.LONG_RANGE_KEY);
        }
        if (fireInfoDto.getTheGeom().getCoordinates()[1] < ConstantsUtil.DefaultValues.MIN_LATITUDE ||
                fireInfoDto.getTheGeom().getCoordinates()[1] > ConstantsUtil.DefaultValues.MAX_LATITUDE) {
            throw new BadRequestAlertException(ErrorConstants.InputValidationMessage.LAT_RANGE_MSG,
                    FIRE_INFO_DTO, ErrorConstants.InputValidationMessage.LAT_RANGE_KEY);
        }

    }

    private void checkQueryBaseValidation(FireInfoDto fireInfoDto) throws BadRequestAlertException {
        checkValidations(fireInfoDto);
        if (fireInfoDto.getLatitude() == null || fireInfoDto.getLongitude() == null) {
            throw new BadRequestAlertException(ErrorConstants.InputValidationMessage.EMPTY_LAT_LONG_MSG,
                    FIRE_INFO_DTO, ErrorConstants.InputValidationMessage.EMPTY_LAT_LONG_KEY);
        }
        if (fireInfoDto.getLatitude() > ConstantsUtil.DefaultValues.MAX_LATITUDE ||
                fireInfoDto.getLatitude() < ConstantsUtil.DefaultValues.MIN_LATITUDE) {
            throw new BadRequestAlertException(ErrorConstants.InputValidationMessage.LAT_RANGE_MSG,
                    FIRE_INFO_DTO, ErrorConstants.InputValidationMessage.LAT_RANGE_KEY);
        }
        if (fireInfoDto.getLongitude() > ConstantsUtil.DefaultValues.MAX_LONGITUDE ||
                fireInfoDto.getLongitude() < ConstantsUtil.DefaultValues.MIN_LONGITUDE) {
            throw new BadRequestAlertException(ErrorConstants.InputValidationMessage.LONG_RANGE_MSG,
                    FIRE_INFO_DTO, ErrorConstants.InputValidationMessage.LONG_RANGE_KEY);
        }
    }

    private void checkValidations(FireInfoDto fireInfoDto) throws BadRequestAlertException {
        if (fireInfoDto.getNumberOfFireTrucks() < 1 || fireInfoDto.getNumberOfFireTrucks() > ConstantsUtil.DefaultValues.MAX_NUMBER_OF_TRUCKS) {
            throw new BadRequestAlertException(ErrorConstants.InputValidationMessage.WRONG_TRUCK_NUMBERS_MSG,
                    FIRE_INFO_DTO, ErrorConstants.InputValidationMessage.WRONG_TRUCK_NUMBERS_KEY);
        }
    }

    /**
     * get hydrants with distance less than a default value of a firehose length
     *
     * @param fireInfoDto consists of fire coordination and number of trucks, this method uses the geom coordinates
     * @return List<HydrantDto> list of hydrant in the given circular dimension
     */
    private List<HydrantDto> getAreaFireBrigades(FireInfoDto fireInfoDto) throws BusinessAlertException {
        String url = "https://data.cityofnewyork.us/resource/5bgh-vtsn.json";

        Double fireX = fireInfoDto.getTheGeom().getCoordinates()[0];
        Double fireY = fireInfoDto.getTheGeom().getCoordinates()[1];

        StringBuilder whereStr = new StringBuilder("distance_in_meters(the_geom,'POINT(");
        whereStr.append(fireX);
        whereStr.append(" ");
        whereStr.append(fireY);
        whereStr.append(")') <= ");
        whereStr.append(ConstantsUtil.DefaultValues.MAX_LENGTH_OF_FIRE_HOSE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("$where", whereStr);
        URI uriPlus = builder.encode().build(false).toUri();

        String strictlyEscapedQuery = StringUtils.replace(uriPlus.getRawQuery(), "+", "%2B");
        URI uri = UriComponentsBuilder.fromUri(uriPlus)
                .replaceQuery(strictlyEscapedQuery)
                .build(true).toUri();
        return callApi(uri);
    }

    /**
     * get N nearest hydrants based on Latitude and Longitude of the fire, all in done in database side
     *
     * @param fireInfoDto consists of fire coordination and number of trucks, this method uses Latitude and Longitude of the fire
     * @return List<HydrantDto> list of n nearest hydrants based on fires coordinate and number of trucks
     * @see <a href="https://dev.socrata.com/foundry/data.cityofnewyork.us/5bgh-vtsn" ></a>
     */
    private List<HydrantDto> getSortedNearestFireBrigades(FireInfoDto fireInfoDto) throws BusinessAlertException {

        Double fireLat = fireInfoDto.getLatitude();
        Double fireLong = fireInfoDto.getLongitude();

        StringBuilder orderStr = new StringBuilder("((latitude+(");
        orderStr.append(-fireLat);
        orderStr.append("))*(latitude+(");
        orderStr.append(-fireLat);
        orderStr.append(")))+((longitude+(");
        orderStr.append(-fireLong);
        orderStr.append("))*(longitude+(");
        orderStr.append(-fireLong);
        orderStr.append(")))");

        StringBuilder whereStr = new StringBuilder(orderStr.toString());
        whereStr.append("<=");
        whereStr.append(Math.pow(ConstantsUtil.DefaultValues.MAX_LENGTH_OF_FIRE_HOSE, 2));

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

    private List<HydrantDto> callApi(URI uri) throws BusinessAlertException {
        List<HydrantDto> hydrantDtoList = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String resultStr = restTemplate.getForObject(uri, String.class);
            ObjectMapper mapper = new ObjectMapper();
            hydrantDtoList = mapper.readValue(resultStr, new TypeReference<List<HydrantDto>>() {
            });
        } catch (Exception e) {
            throw new BusinessAlertException(e.getMessage(),
                    FIRE_INFO_DTO, ErrorConstants.InputValidationMessage.API_EXCEPTION_KEY);
        }
        return hydrantDtoList;
    }

    /**
     * sort the found hydrants by their distance to the fire coordinate, this method gets the fire coordination and list of
     * hydrants that retrieve from dataset, sort the list based on their distance to the fire and returns first n hydrants
     *
     * @param fireInfoDto    consists of fire coordination and number of trucks
     * @param hydrantDtoList all the hydrants that found in the given circular dimension
     * @return List<SelectedHydrantDto> list of sorted selected hydrants in the given circular dimension
     */
    private List<SelectedHydrantDto> sortHydrants(FireInfoDto fireInfoDto, List<HydrantDto> hydrantDtoList) throws BusinessAlertException {
        List<SelectedHydrantDto> sortedHydrantDtoList = new ArrayList<>();
        Double fireLat = fireInfoDto.getLatitude() !=null ? fireInfoDto.getLatitude() : fireInfoDto.getTheGeom().getCoordinates()[1],
                fireLong = fireInfoDto.getLongitude() != null ? fireInfoDto.getLongitude() : fireInfoDto.getTheGeom().getCoordinates()[0];

        List<SelectedHydrantDto> finalSelectedHydrantDtoList = sortedHydrantDtoList;
        IntStream.range(0, hydrantDtoList.size())
                .forEach(i -> {
                    Double distanceToFire = getDistanceFromLatLonInMeters(fireLat, fireLong,
                            hydrantDtoList.get(i).getThe_geom().getCoordinates()[1] ,
                            hydrantDtoList.get(i).getThe_geom().getCoordinates()[0]);
                    if (distanceToFire <= ConstantsUtil.DefaultValues.MAX_LENGTH_OF_FIRE_HOSE) {
                        finalSelectedHydrantDtoList.add(new SelectedHydrantDto(hydrantDtoList.get(i).getUnitid(),
                                distanceToFire
                        ));
                    }
                });
        if (finalSelectedHydrantDtoList.isEmpty()) {
            throw new BusinessAlertException(ErrorConstants.BusinessError.NO_HYDRANT_MSG,
                    FIRE_INFO_DTO, ErrorConstants.BusinessError.NO_HYDRANT_KEY);

        }
        sortedHydrantDtoList = finalSelectedHydrantDtoList.stream()
                .sorted(Comparator.comparing(SelectedHydrantDto::getDistanceToFire))
                .collect(Collectors.toList());

        return sortedHydrantDtoList;
    }

    /**
     * calculates total length of firehoses and returns our output
     *
     * @param fireInfoDto    consists of fire coordination and number of trucks
     * @param hydrantDtoList all the hydrants that found in the given circular dimension
     * @return List<SelectedHydrantDto> list of sorted selected hydrants in the given circular dimension
     */
    private NearestHydrantsToFireDto generateSelectedHydrants(FireInfoDto fireInfoDto, List<HydrantDto> hydrantDtoList) throws BusinessAlertException {

        Double fireLat = fireInfoDto.getLatitude(),
                fireLong = fireInfoDto.getLongitude();
        List<SelectedHydrantDto> finalSelectedHydrantDtoList = new ArrayList<>();
        IntStream.range(0, hydrantDtoList.size())
                .forEach(i -> {
                    Double distanceToFire = getDistanceFromLatLonInMeters(fireLat, fireLong, hydrantDtoList.get(i).getLatitude(), hydrantDtoList.get(i).getLongitude());
                    if (distanceToFire <= ConstantsUtil.DefaultValues.MAX_LENGTH_OF_FIRE_HOSE) {
                        finalSelectedHydrantDtoList.add(new SelectedHydrantDto(hydrantDtoList.get(i).getUnitid(),
                                distanceToFire
                        ));
                    }
                });
        if (finalSelectedHydrantDtoList.isEmpty()) {
            throw new BusinessAlertException(ErrorConstants.BusinessError.NO_HYDRANT_MSG,
                    FIRE_INFO_DTO, ErrorConstants.BusinessError.NO_HYDRANT_KEY);

        }
        return new NearestHydrantsToFireDto(
                CalculationsHelper.calculateTotalLengthOfFireHoses(finalSelectedHydrantDtoList), finalSelectedHydrantDtoList);
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
        sortedHydrantDtoList = sortedHydrantDtoList.subList(0,
                Math.toIntExact(Math.min(fireInfoDto.getNumberOfFireTrucks(), sortedHydrantDtoList.size())));
        return new NearestHydrantsToFireDto(CalculationsHelper.calculateTotalLengthOfFireHoses(sortedHydrantDtoList), sortedHydrantDtoList);
    }

    private GenericRestResponse generateResponse(NearestHydrantsToFireDto nearestHydrantsToFireDto, FireInfoDto fireInfoDto) {
        String responseMsg = "SUCCESS";
        if (nearestHydrantsToFireDto.getHydrants().size() < fireInfoDto.getNumberOfFireTrucks()) {
            responseMsg = ErrorConstants.BusinessError.NOT_ENOUGH_HYDRANTS_MSG;
        }
        return new GenericRestResponse(GenericRestResponse.STATUS.SUCCESS, responseMsg, nearestHydrantsToFireDto);

    }
}
