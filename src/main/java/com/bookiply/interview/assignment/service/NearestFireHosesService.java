package com.bookiply.interview.assignment.service;

import com.bookiply.interview.assignment.web.dto.GenericRestResponse;
import com.bookiply.interview.assignment.web.dto.FireInfoDto;
import com.bookiply.interview.assignment.web.dto.NearestHydrantsToFireDto;
import com.bookiply.interview.assignment.web.error.BadRequestAlertException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

@Service
public interface NearestFireHosesService {

    public GenericRestResponse getNearestHydrants(FireInfoDto fireInfoDto) throws BadRequestAlertException, JsonProcessingException;

    public NearestHydrantsToFireDto getNearestHydrantsCodeBase(FireInfoDto fireInfoDto) throws BadRequestAlertException, JsonProcessingException;

    public NearestHydrantsToFireDto getNearestHydrantsQueryBase(FireInfoDto fireInfoDto) throws BadRequestAlertException, JsonProcessingException;
}
