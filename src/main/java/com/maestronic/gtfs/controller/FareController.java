package com.maestronic.gtfs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maestronic.gtfs.service.FareService;
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
public class FareController {

    @Autowired
    private FareService fareService;
    private HttpHeaders headers;

    @GetMapping(path = "api/gtfs/fare-list", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getTripFare(@RequestParam(required = false) String agency_id,
                                              @RequestParam(required = false) String route_id,
                                              @RequestParam(required = false) String origin_id,
                                              @RequestParam(required = false) String destination_id,
                                              @RequestParam(required = false) String contains_id) {

        headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            List<Map<String, Object>> response = fareService
                    .getTripFare(agency_id,
                            route_id,
                            origin_id,
                            destination_id,
                            contains_id);

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
