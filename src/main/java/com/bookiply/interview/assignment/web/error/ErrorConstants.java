package com.bookiply.interview.assignment.web.error;

import java.net.URI;

public final class ErrorConstants {

    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String PROBLEM_BASE_URL = "https://www.egs.com/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraint-violation");
    public static final URI ENTITY_NOT_FOUND_TYPE = URI.create(PROBLEM_BASE_URL + "/entity-not-found");

    private ErrorConstants() {
    }

    public static class InputValidationMessage{
        public static final String WRONG_TRUCK_NUMBERS_KEY = "wrongTruckNumbers";
        public static final String WRONG_TRUCK_NUMBERS_MSG = "Number of trucks is Not Valid.";
        public static final String API_EXCEPTION_KEY = "apiConnection";
        public static final String API_EXCEPTION_MSG = "External API does not Response";
    }

}