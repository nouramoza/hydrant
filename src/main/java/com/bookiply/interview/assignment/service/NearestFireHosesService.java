package com.bookiply.interview.assignment.service;

import com.bookiply.interview.assignment.web.dto.GenericRestResponse;
import com.bookiply.interview.assignment.web.dto.FireInfoDto;
import com.bookiply.interview.assignment.web.error.BadRequestAlertException;
import org.springframework.stereotype.Service;

@Service
public interface NearestFireHosesService {

    public GenericRestResponse getNearestHydrantsCodeBase(FireInfoDto fireInfoDto) throws BadRequestAlertException;

    public GenericRestResponse getNearestHydrantsQueryBase(FireInfoDto fireInfoDto) throws BadRequestAlertException;
}
