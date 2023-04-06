package com.maestronic.gtfs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maestronic.gtfs.service.VehicleConnectionService;
import com.maestronic.gtfs.util.ResponseMessage;
import com.maestronic.gtfs.validation.VehicleMonitorDummyValidation;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class VehicleConnectionController {

    @Autowired
    private VehicleConnectionService vehicleConnectionService;
    private HttpHeaders headers;

    @GetMapping(path = "api/gtfs/vehicle-monitoring-connection", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getVehicleMonitoringConnection(@RequestParam(required = true) String agency_id,
                                                                 @RequestParam(required = true) String vehicle_id,
                                                                 @RequestParam(required = false, defaultValue = "0") Long approx) {

        headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Check if required parameter is empty string or null
        if (agency_id.isEmpty() || agency_id == null || vehicle_id.isEmpty() || vehicle_id == null) {
            throw new IllegalArgumentException();
        }

        try {
            String response = vehicleConnectionService.getVehicleMonitorConnection(agency_id, vehicle_id, approx);

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
                            new ObjectMapper().readTree(response)
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

    @GetMapping(path = "api/gtfs/dummy/vehicle-monitoring-connection", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getDummyVehicleMonitoringConnection(@Validated VehicleMonitorDummyValidation validation,
                                                                      BindingResult bindingResult) {

        headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Check if required parameter is empty string or null
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

        try {
            String response = vehicleConnectionService.getDummyVehicleMonitorConnection(validation.getAgency_id(),
                    validation.getDate(),
                    validation.getStart_time(),
                    validation.getEnd_time(),
                    validation.getLimit());

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
                            new ObjectMapper().readTree(response)
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
