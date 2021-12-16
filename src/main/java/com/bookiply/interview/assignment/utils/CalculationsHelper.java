package com.bookiply.interview.assignment.utils;

import com.bookiply.interview.assignment.web.dto.HydrantDto;
import com.bookiply.interview.assignment.web.dto.SelectedHydrantDto;

import java.util.List;

public class CalculationsHelper {

    /**
     * @param sortedHydrantDtoList
     * @return Double Total Length Of FireHoses
     */
    public static Double calculateTotalLengthOfFireHoses(List<SelectedHydrantDto> sortedHydrantDtoList) {
        return sortedHydrantDtoList.stream()
                .mapToDouble(SelectedHydrantDto::getDistanceToFire)
                .sum();
    }

    public static Double getDistanceFromLatLonInMeters(Double fireLat,
                                                       Double fireLong,
                                                       Double hydrantLat,
                                                       Double hydrantLong) {
        Double xDistance = hydrantLat - fireLat,
                yDistance = hydrantLong - fireLong;
        return (Math.sqrt((xDistance * xDistance) + (yDistance * yDistance))) * 1000;

    }

    public static Double getDistanceFromLatLonInKm(Double lat1,Double lon1,Double lat2,Double lon2) {
        Double R = 6371.0; // Radius of the earth in km
        Double dLat = deg2rad(lat2-lat1);  // deg2rad below
        Double dLon = deg2rad(lon2-lon1);
        Double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double d = R * c; // Distance in km
        return d;
    }

    public static Double deg2rad(Double deg) {
        return deg * (Math.PI/180.0);
    }
}
