package com.bookiply.interview.assignment.web.error;

import java.net.URI;

public final class ErrorConstants {

    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String PROBLEM_BASE_URL = "https://www.bookipy.com/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraint-violation");
    public static final URI ENTITY_NOT_FOUND_TYPE = URI.create(PROBLEM_BASE_URL + "/entity-not-found");

    private ErrorConstants() {
    }

    public static class InputValidationMessage{
        public static final String WRONG_TRUCK_NUMBERS_KEY = "wrongTruckNumbers";
        public static final String WRONG_TRUCK_NUMBERS_MSG = "Number of trucks is Not Valid.";
        public static final String API_EXCEPTION_KEY = "apiConnection";
        public static final String API_EXCEPTION_MSG = "External API does not Response.";
        public static final String EMPTY_GEOM_KEY = "emptyGeom";
        public static final String EMPTY_GEOM_MSG = "Please Fill The Geom Coordinates";
        public static final String EMPTY_LAT_LONG_KEY = "emptyLatLong";
        public static final String EMPTY_LAT_LONG_MSG = "Please Fill The Latitude And Longitude Coordinates";
        public static final String LAT_RANGE_KEY = "wrongLatRange";
        public static final String LAT_RANGE_MSG = "Latitude Value Range Is Not valid.";
        public static final String LONG_RANGE_KEY = "wrongLongRange";
        public static final String LONG_RANGE_MSG = "Longitude Value Range Is Not valid.";
    }

    public static class BusinessError{
        public static final String NO_HYDRANT_KEY = "noHydrantFound";
        public static final String NO_HYDRANT_MSG = "There is Not Any Hydrant in FireHoses Distance";
        public static final String NOT_ENOUGH_HYDRANTS_KEY = "noHydrantFound";
        public static final String NOT_ENOUGH_HYDRANTS_MSG = "Number Of Founded Hydrants Are Less Than Number Of trucks, But we return the nearest available hydrants.";
    }

}