package com.maestronic.gtfs.controller;

import com.maestronic.gtfs.dto.gtfs.Gtfs;
import com.maestronic.gtfs.service.JourneyService;
import com.maestronic.gtfs.service.TimeService;
import com.maestronic.gtfs.util.ResponseMessage;
import com.maestronic.gtfs.validation.TripPlannerValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class JourneyController {

    @Autowired
    private JourneyService journeyService;
    @Autowired
    private TimeService timeService;
    private HttpHeaders headers;

    @GetMapping(path = "api/gtfs/shape-journey-stop", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getShapeJourneyWithStop(@RequestParam(required = true) String agency_id,
                                                          @RequestParam(required = true) String vehicle_id) {

        // Set headers
        headers = new HttpHeaders();

        // Check if required parameter is empty or null
        if (agency_id.isEmpty() || agency_id == null || vehicle_id.isEmpty() || vehicle_id == null) {
            throw new IllegalArgumentException();
        }

        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            List<Map<String, Object>> response = journeyService.getShapeJourneyWithStop(agency_id, vehicle_id);

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

    @GetMapping(path = "api/gtfs/trip-planner", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getTripPlannerWithFare(@Validated TripPlannerValidation tripPlannerValidation,
                                                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(
                    ResponseMessage.exceptionErrorJson(
                            HttpStatus.BAD_REQUEST.value(),
                            bindingResult
                                    .getFieldErrors()
                                    .stream()
                                    .map(f -> (f.getField() + ": " + f.getDefaultMessage()))
                                    .collect(Collectors.toList())
                    ),
                    headers,
                    HttpStatus.BAD_REQUEST
            );
        }

        // Set headers
        headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            Gtfs response = journeyService.getTripPlannerWithFare(
                    tripPlannerValidation.getOri_lat(),
                    tripPlannerValidation.getOri_lon(),
                    tripPlannerValidation.getOri_name(),
                    tripPlannerValidation.getDes_lat(),
                    tripPlannerValidation.getDes_lon(),
                    tripPlannerValidation.getDes_name(),
                    timeService.strToLocalDateTime(tripPlannerValidation.getDate_time()),
                    tripPlannerValidation.getType_of_trip(),
                    tripPlannerValidation.getLimit());

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
            e.printStackTrace();
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
