package com.maestronic.gtfs.controller;

import com.maestronic.gtfs.service.AlertService;
import com.maestronic.gtfs.service.JourneyService;
import com.maestronic.gtfs.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class AlertController {

    @Autowired
    private AlertService alertService;
    private HttpHeaders headers;

    @GetMapping(path = "api/gtfs/service-alert", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getShapeJourneyWithStop(@RequestParam(required = false) String route_id,
                                                          @RequestParam(required = false) String trip_id,
                                                          @RequestParam(required = false) String stop_id,
                                                          @RequestParam(required = false) String agency_id,
                                                          @RequestParam(required = false) String route_type,
                                                          @RequestParam(required = false) String cause,
                                                          @RequestParam(required = false) String effect) {

        // Set headers
        headers = new HttpHeaders();

        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            List<Map<String, Object>> response = alertService.getServiceAlert(
                    route_id,
                    trip_id,
                    stop_id,
                    agency_id,
                    route_type,
                    cause,
                    effect
            );

            if (response == null) {
                return new ResponseEntity<>(
                        ResponseMessage.retrieveDataJson(
                                HttpStatus.OK.value(),
                                "No data available.",
                                new ArrayList()
                        ),
                        headers,
                        HttpStatus.OK
                );
            }

            return new ResponseEntity<>(
                    ResponseMessage.retrieveDataJson(
                            HttpStatus.OK.value(),
                            "Retrieved data successfully.",
                            response
                    ),
                    headers,
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    ResponseMessage.exceptionErrorJson(
                            HttpStatus.BAD_REQUEST.value(),
                            "Something went wrong."
                    ),
                    headers,
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> missingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest req) {
        headers = new HttpHeaders();

        String message = "Required request parameter '" + e.getParameterName() + "' for method parameter.";
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(
                ResponseMessage.exceptionErrorJson(HttpStatus.BAD_REQUEST.value(), message),
                headers,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> illegalArgumentException(IllegalArgumentException e, HttpServletRequest req) {
        headers = new HttpHeaders();

        String message = "Required parameter must not be null.";

        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(
                ResponseMessage.exceptionErrorJson(HttpStatus.BAD_REQUEST.value(), message),
                headers,
                HttpStatus.BAD_REQUEST
        );
    }
}
