package com.bookiply.interview.assignment.service;

import com.bookiply.interview.assignment.web.dto.GenericRestResponse;
import com.bookiply.interview.assignment.web.dto.HydrantDto;
import com.bookiply.interview.assignment.web.dto.InputDto;
import com.bookiply.interview.assignment.web.dto.OutputDto;
import com.bookiply.interview.assignment.web.error.BadRequestAlertException;
import org.springframework.stereotype.Service;

@Service
public interface NearestFireHosesService {

    public GenericRestResponse getNearestHydrants(InputDto inputDto) throws BadRequestAlertException;

    public OutputDto getNearestHydrantsJsonOut(InputDto inputDto) throws BadRequestAlertException;
}
