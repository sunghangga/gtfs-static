package com.maestronic.gtfs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maestronic.gtfs.service.ConnectionTimetableService;
import com.maestronic.gtfs.util.GlobalVariable;
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

@CrossOrigin
@RestController
public class ConnectionTimetableController {

    @Autowired
    private ConnectionTimetableService connectionTimetableService;
    private HttpHeaders headers;

    @GetMapping(path = "api/gtfs/connectiontimetable", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getRealConnectionTimetable(@RequestParam(required = true) String stop_id,
                                                           @RequestParam(required = false) String format) {

        headers = new HttpHeaders();

        // Check if required parameter is empty or null
        if (stop_id.isEmpty() || stop_id == null) {
            throw new IllegalArgumentException();
        }

        // Check if request xml data
        if (format == null || format.equals(GlobalVariable.XML_TYPE_DATA)) {
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
            headers.setContentType(MediaType.APPLICATION_XML);

            try {
                String response = connectionTimetableService.getRealConnectionTimetableXml(stop_id);
                if (response == null) {
                    return new ResponseEntity<>(
                            ResponseMessage.noInfoForTopicError(HttpStatus.OK.value(), "No data available."),
                            headers,
                            HttpStatus.OK
                    );
                }
                return new ResponseEntity<>(
                        response,
                        headers,
                        HttpStatus.OK
                );
            } catch (Exception e) {
                return new ResponseEntity<>(
                        ResponseMessage.otherError(HttpStatus.BAD_REQUEST.value(), "Something went wrong."),
                        headers,
                        HttpStatus.BAD_REQUEST
                );
            }

        } else if (format.equals(GlobalVariable.JSON_TYPE_DATA)) {
            // Check if format data is json
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);

            try {
                String response = connectionTimetableService.getRealConnectionTimetableJson(stop_id);

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
                return new ResponseEntity<>(
                        ResponseMessage.exceptionErrorJson(
                                HttpStatus.BAD_REQUEST.value(),
                                "Something went wrong."
                        ),
                        headers,
                        HttpStatus.BAD_REQUEST
                );
            }
        } else {
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
            headers.setContentType(MediaType.APPLICATION_XML);
            return new ResponseEntity<>(
                    ResponseMessage.missingRequestParameterError(HttpStatus.BAD_REQUEST.value(), "Parameter 'format' must have a value among: xml and json."),
                    headers,
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @GetMapping(path = "api/gtfs/dummy/connectiontimetable", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getDummyConnectionTimetable(@RequestParam(required = true) String stop_id,
                                                             @RequestParam(required = false) String format) {

        headers = new HttpHeaders();

        // Check if required parameter is empty or null
        if (stop_id.isEmpty() || stop_id == null) {
            throw new IllegalArgumentException();
        }

        // Check if request xml data
        if (format == null || format.equals(GlobalVariable.XML_TYPE_DATA)) {
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
            headers.setContentType(MediaType.APPLICATION_XML);

            try {
                String response = connectionTimetableService.getDummyConnectionTimetableXml(stop_id);
                if (response == null) {
                    return new ResponseEntity<>(
                            ResponseMessage.noInfoForTopicError(HttpStatus.OK.value(), "No data available."),
                            headers,
                            HttpStatus.OK
                    );
                }
                return new ResponseEntity<>(
                        response,
                        headers,
                        HttpStatus.OK
                );
            } catch (Exception e) {
                return new ResponseEntity<>(
                        ResponseMessage.otherError(HttpStatus.BAD_REQUEST.value(), "Something went wrong."),
                        headers,
                        HttpStatus.BAD_REQUEST
                );
            }

        } else if (format.equals(GlobalVariable.JSON_TYPE_DATA)) {
            // Check if format data is json
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);

            try {
                String response = connectionTimetableService.getDummyConnectionTimetableJson(stop_id);

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
                return new ResponseEntity<>(
                        ResponseMessage.exceptionErrorJson(
                                HttpStatus.BAD_REQUEST.value(),
                                "Something went wrong."
                        ),
                        headers,
                        HttpStatus.BAD_REQUEST
                );
            }
        } else {
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
            headers.setContentType(MediaType.APPLICATION_XML);
            return new ResponseEntity<>(
                    ResponseMessage.missingRequestParameterError(HttpStatus.BAD_REQUEST.value(), "Parameter 'format' must have a value among: xml and json."),
                    headers,
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> missingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest req) {
        headers = new HttpHeaders();

        String formatData = req.getParameter("format");
        String message = "Required request parameter '" + e.getParameterName() + "' for method parameter.";

        // If format parameter is 'json'
        if (formatData != null && formatData.equals(GlobalVariable.JSON_TYPE_DATA)) {
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);

            return new ResponseEntity<>(
                    ResponseMessage.exceptionErrorJson(HttpStatus.BAD_REQUEST.value(), message),
                    headers,
                    HttpStatus.BAD_REQUEST
            );
        }
        // If format parameter is 'xml'
        else {
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
            headers.setContentType(MediaType.APPLICATION_XML);

            return new ResponseEntity<>(
                    ResponseMessage.missingRequestParameterError(HttpStatus.BAD_REQUEST.value(), message),
                    headers,
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> illegalArgumentException(IllegalArgumentException e, HttpServletRequest req) {
        headers = new HttpHeaders();

        String formatData = req.getParameter("format");
        String message = "Required parameter must not be null.";

        // If format parameter is 'json'
        if (formatData != null && formatData.equals(GlobalVariable.JSON_TYPE_DATA)) {
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);

            return new ResponseEntity<>(
                    ResponseMessage.exceptionErrorJson(HttpStatus.BAD_REQUEST.value(), message),
                    headers,
                    HttpStatus.BAD_REQUEST
            );
        }
        // If format parameter is 'xml'
        else {
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
            headers.setContentType(MediaType.APPLICATION_XML);

            return new ResponseEntity<>(
                    ResponseMessage.illegalArgumentException(HttpStatus.BAD_REQUEST.value(), message),
                    headers,
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
