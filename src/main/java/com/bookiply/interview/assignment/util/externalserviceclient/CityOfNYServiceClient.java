//package com.bookiply.interview.assignment.util.externalserviceclient;
//
//import com.bookiply.interview.assignment.web.dto.GenericRestResponse;
//import com.bookiply.interview.assignment.web.dto.HydrantDto;
//import com.bookiply.interview.assignment.web.error.BadRequestAlertException;
//import com.bookiply.interview.assignment.web.error.ErrorConstants;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.apache.tomcat.util.codec.binary.Base64;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.web.client.RestTemplateBuilder;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.retry.annotation.Backoff;
//import org.springframework.retry.annotation.Retryable;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestClientException;
//import org.springframework.web.client.RestTemplate;
//
//import javax.annotation.PostConstruct;
//import java.nio.charset.Charset;
//import java.time.Duration;
//import java.util.List;
//
//@Component
//public class CityOfNYServiceClient {
//
//    private static final Logger log = LoggerFactory.getLogger(CityOfNYServiceClient.class);
//    private static final int RETRY_COUNT = 2;
//
//    @Value("${cityOfNewyorkData.url}")
//    private String hydrantServiceTarget;
//    @Value("${hydrantService.connection.timeout.ms}")
//    private int hydrantServiceConnectionTimeout;
//    @Value("${hydrantService.socket.timeout.ms}")
//    private int hydrantServiceSocketTimeout;
////    @Value("${hydrantService.auth.key}")
////    private String bankServiceAuthKey;
//
//    private RestTemplate restTemplate;
//
//    @Retryable(
//            maxAttempts = RETRY_COUNT,
//            include = RestClientException.class,
//            backoff = @Backoff(delay = 1000)
//    )
//    public ResponseEntity<GenericRestResponse> getAreaFireBrigadesApi(String requestPath) throws RestClientException {
//        String uri = "https://data.cityofnewyork.us/resource/5bgh-vtsn.json";
//
////        String uri = "https://data.cityofnewyork.us/resource/5bgh-vtsn.json?$where=within_circle(the_geom,%2040.71,%20-74.0,%20100)";
//
//
//        try {
//            RestTemplate restTemplate = new RestTemplate();
//            String resultStr = restTemplate.getForObject(uri, String.class);
//            ObjectMapper mapper = new ObjectMapper();
//            hydrantDtoList = mapper.readValue(resultStr, new TypeReference<List<HydrantDto>>() {
//            });
//        } catch (Exception e) {
//            throw new BadRequestAlertException(e.getMessage(),
//                    FIRE_INFO_DTO, ErrorConstants.InputValidationMessage.API_EXCEPTION_KEY);
//        }
//
//
//
//    }
//}
