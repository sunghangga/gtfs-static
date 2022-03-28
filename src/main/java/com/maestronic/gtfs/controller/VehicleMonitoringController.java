package com.maestronic.gtfs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maestronic.gtfs.service.VehicleService;
import com.maestronic.gtfs.util.GlobalVariable;
import com.maestronic.gtfs.util.ResponseMessage;
import com.maestronic.gtfs.service.VehicleMonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@CrossOrigin
@RestController
public class VehicleMonitoringController {

    @Autowired
    private VehicleMonitoringService vehicleMonitoringService;
    @Autowired
    private VehicleService vehicleService;
    private HttpHeaders headers;

    @GetMapping(path = "api/gtfs/vehiclemonitoring", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getRealVehicleMonitoring(@RequestParam(required = true) String agency_id,
                                                   @RequestParam(required = false) String vehicle_id,
                                                   @RequestParam(required = false) String format) {

        headers = new HttpHeaders();

        // Check if required parameter is empty or null
        if (agency_id.isEmpty() || agency_id == null) {
            throw new IllegalArgumentException();
        }

        // Check if request xml data
        if (format == null || format.equals(GlobalVariable.XML_TYPE_DATA)) {
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
            headers.setContentType(MediaType.APPLICATION_XML);

            try {
                String response = vehicleMonitoringService.getRealVehicleMonitoringXml(agency_id, vehicle_id);
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
                String response = vehicleMonitoringService.getRealVehicleMonitoringJson(agency_id, vehicle_id);

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

    @GetMapping(path = "api/gtfs/dummy/vehiclemonitoring", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getDummyVehicleMonitoring(@RequestParam(required = true) String agency_id,
                                                           @RequestParam(required = false) String vehicle_id,
                                                           @RequestParam(required = false) String format) {

        headers = new HttpHeaders();

        // Check if required parameter is empty or null
        if (agency_id.isEmpty() || agency_id == null) {
            throw new IllegalArgumentException();
        }
        // Check if request xml data
        if (format == null || format.equals(GlobalVariable.XML_TYPE_DATA)) {
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
            headers.setContentType(MediaType.APPLICATION_XML);

            try {
                String response = vehicleMonitoringService.getDummyVehicleMonitoringXml(agency_id, vehicle_id);
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
                e.printStackTrace();
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
                String response = vehicleMonitoringService.getDummyVehicleMonitoringJson(agency_id, vehicle_id);

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
            return new ResponseEntity<>(
                    ResponseMessage.missingRequestParameterError(HttpStatus.BAD_REQUEST.value(), "Parameter 'format' must have a value among: xml and json."),
                    headers,
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @GetMapping(path = "api/gtfs/vehicle-positions", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getVehiclePositions(@RequestParam(required = false) String agency_id,
                                                      @RequestParam(required = false) String vehicle_id,
                                                      @RequestParam(required = false) String trip_id) {

        // Set headers
        headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            List<Map<String, Object>> response = vehicleService.getVehiclePositions(agency_id, vehicle_id, trip_id);

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
