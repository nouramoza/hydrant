package com.bookiply.interview.assignment.service.impl;

import com.bookiply.interview.assignment.service.NearestFireHosesService;
import com.bookiply.interview.assignment.web.dto.*;
import com.bookiply.interview.assignment.web.error.BadRequestAlertException;
import com.bookiply.interview.assignment.web.error.ErrorConstants;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service Implementation for finding nearest hydrants and total legnth.
 */

@Service
public class NearestFireHosesServiceImpl implements NearestFireHosesService {

    private static final String INPUT_DTO = "inputDto";

    /**
     * get N nearest hydrants and total length of fire hoses
     *
     * @param inputDto consists of fire coorditation and number of trucks
     * @return GenericRestResponse
     */
    @Override
    public GenericRestResponse getNearestHydrants(InputDto inputDto) throws BadRequestAlertException {
        checkValidations(inputDto);
        List<HydrantDto> hydrantDtoList = getAreaFireBrigades(inputDto);
        OutputDto outputDto = findNearestHydrant(inputDto, hydrantDtoList);
        return new GenericRestResponse(GenericRestResponse.STATUS.SUCCESS, outputDto.toString());
    }

    /**
     * get N nearest hydrants and total length of fire hoses
     *
     * @param inputDto consists of fire coorditation and number of trucks
     * @return OutputDto - total firehoses length in meters
     * - list of N nearest hydrants used by the fire brigade, with its unitId and distance to the fire
     */
    @Override
    public OutputDto getNearestHydrantsJsonOut(InputDto inputDto) throws BadRequestAlertException {

        checkValidations(inputDto);
        /** find hydrants in a circular */
        List<HydrantDto> hydrantDtoList = getAreaFireBrigades(inputDto);
        return findNearestHydrant(inputDto, hydrantDtoList);
    }


    /**
     * checks input values correctness
     *
     * @param inputDto consists of fire coordination and number of trucks
     */
    private void checkValidations(InputDto inputDto) throws BadRequestAlertException {
        if (inputDto.getNumberOfFireTrucks() < 1) {
            throw new BadRequestAlertException(ErrorConstants.InputValidationMessage.WRONG_TRUCK_NUMBERS_MSG,
                    INPUT_DTO, ErrorConstants.InputValidationMessage.WRONG_TRUCK_NUMBERS_KEY);
        }
    }

    /**
     * get hydrants in a cicular dimension
     *
     * @param inputDto consists of fire coordination and number of trucks
     * @return List<HydrantDto> list of hydrant in the given circular dimension
     */
    private List<HydrantDto> getAreaFireBrigades(InputDto inputDto) {

        List<HydrantDto> hydrantDtoList = new ArrayList<>();
        //1
        HydrantDto hydrantDto = new HydrantDto();
        hydrantDto.setUnitId("H425919a");
        PointDto point = new PointDto(-73.79456804377382, 40.7722177771488);
        hydrantDto.setTheGeom(point);
        hydrantDtoList.add(hydrantDto);

        //2
        hydrantDto = new HydrantDto();
        hydrantDto.setUnitId("H325449");
        point = new PointDto(-73.91289250895464, 40.644346617665086);
        hydrantDto.setTheGeom(point);
        hydrantDtoList.add(hydrantDto);

        //3
        hydrantDto = new HydrantDto();
        hydrantDto.setUnitId("H307276");
        point = new PointDto(-73.95303997851815, 40.72505714515934);
        hydrantDto.setTheGeom(point);
        hydrantDtoList.add(hydrantDto);

        //4
        hydrantDto = new HydrantDto();
        hydrantDto.setUnitId("H301843");
        point = new PointDto(-73.99463256503688, 40.693988927757644);
        hydrantDto.setTheGeom(point);
        hydrantDtoList.add(hydrantDto);

        //5
        hydrantDto = new HydrantDto();
        hydrantDto.setUnitId("H439410");
        point = new PointDto(-73.93569187481359, 40.73528872722265);
        hydrantDto.setTheGeom(point);
        hydrantDtoList.add(hydrantDto);

        //6
        hydrantDto = new HydrantDto();
        hydrantDto.setUnitId("H328476");
        point = new PointDto(-73.91147293739287, 40.63402697951722);
        hydrantDto.setTheGeom(point);
        hydrantDtoList.add(hydrantDto);

        return hydrantDtoList;
    }

    /**
     * find N nearest hydrants from the list of given hydrants and total length of firehoses. this method is the main business of the app
     *
     * @param inputDto       consists of fire coordination and number of trucks
     * @param hydrantDtoList list of hydrants in the circular dimension
     * @return OutputDto - total firehoses length in meters
     * - list of N nearest hydrants used by the fire brigade, with its unitId and distance to the fire
     */
    private OutputDto findNearestHydrant(InputDto inputDto, List<HydrantDto> hydrantDtoList) {
        List<SelectedHydrantDto> selectedHydrantDtoList = new ArrayList<>();
        Double totalFirehosesLength = 0.0;
        Double maxDistance = 0.0;
        for (int i = 0; i < hydrantDtoList.size(); i++) {
            Double distanceToFire = Math.sqrt(Math.pow(Math.abs((inputDto.getTheGeom().getX() - hydrantDtoList.get(i).getTheGeom().getX())), 2)
                    + Math.pow(Math.abs((inputDto.getTheGeom().getY() - hydrantDtoList.get(i).getTheGeom().getY())), 2));
            if (selectedHydrantDtoList.size() < inputDto.getNumberOfFireTrucks()) {
                totalFirehosesLength += distanceToFire;
                selectedHydrantDtoList.add(new SelectedHydrantDto(hydrantDtoList.get(i).getUnitId(), distanceToFire));
                maxDistance = maxDistance > distanceToFire ? maxDistance : distanceToFire;
            } else if (maxDistance > distanceToFire) {
                totalFirehosesLength -= (maxDistance - distanceToFire);
                selectedHydrantDtoList.remove(maxDistance);
                selectedHydrantDtoList.add(new SelectedHydrantDto(hydrantDtoList.get(i).getUnitId(), distanceToFire));
                maxDistance = distanceToFire;
            }
        }
        return new OutputDto(totalFirehosesLength * 1000, selectedHydrantDtoList);
    }
}
