package com.maestronic.gtfs.controller;

import com.maestronic.gtfs.dto.custom.StopDto;
import com.maestronic.gtfs.service.StopService;
import com.maestronic.gtfs.util.ResponseMessage;
import com.maestronic.gtfs.validation.PageValidation;
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
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class StopController {

    @Autowired
    private StopService stopService;
    private HttpHeaders headers;

    @GetMapping(path = "api/gtfs/stops", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getStops(@Validated PageValidation pageValidation,
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
            List<StopDto> response = stopService.getStops(
                    pageValidation.getPageNo() - 1,
                    pageValidation.getPageSize(),
                    pageValidation.getSortType().name());

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

    @GetMapping(path = "api/gtfs/stop/{stopId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getStops(@PathVariable @NotNull String stopId) {

        // Set headers
        headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            StopDto response = stopService.getStopsByStopId(stopId);

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
