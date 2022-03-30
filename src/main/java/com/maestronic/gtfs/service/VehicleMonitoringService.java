package com.maestronic.gtfs.service;

import com.maestronic.gtfs.entity.VehicleMonitoring;
import com.maestronic.gtfs.mapclass.*;
import com.maestronic.gtfs.repository.VehicleMonitoringRepository;
import com.maestronic.gtfs.util.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VehicleMonitoringService implements GlobalVariable {

    @Autowired
    private TimeService timeService;
    @Autowired
    private VehicleMonitoringRepository vehicleMonitoringRepository;
    private MonitoredCall monitoredCall;
    private Location location;
    @Value("${timezone}")
    private String timezone;

    private String mapFirstRow(VehicleMonitoring row) {
        location = new Location(row.getPositionLongitude(), row.getPositionLatitude());
        monitoredCall = new MonitoredCall(
                row.getStopId(),
                row.getStopName(),
                row.getStopSequence(),
                row.getCurrentStatus(),
                row.getCurrentStatus() != null ? (row.getCurrentStatus().equals(STOPPED_AT) ? location : new Location()) : new Location(),
                row.getWheelchairBoarding(),
                timeService.durToZoneDateTime(row.getAimedArrivalTime(), row.getTripStartDate()),
                timeService.unixToZoneDateTime(row.getExpectedArrivalTime()),
                row.getArrivalDelay(),
                timeService.durToZoneDateTime(row.getAimedDepartureTime(), row.getTripStartDate()),
                timeService.unixToZoneDateTime(row.getExpectedDepartureTime()),
                row.getDepartureDelay()
        );

        return row.getVehicleLabel();
    }

    public Gtfs getDataVehicleMonitoring(List<VehicleMonitoring> resultList) {
        // Initialize object
        OnwardCalls onwardCalls = new OnwardCalls();
        VehicleMonitoringDelivery vehicleMonitoringDelivery = new VehicleMonitoringDelivery();
        String vehicleLabel = mapFirstRow(resultList.get(0));
        VehicleMonitoring row;

        for (int i = 1; i < resultList.size(); i++) {
            row = resultList.get(i);
            // Check for different vehicle
            if (!vehicleLabel.equals(row.getVehicleLabel())) {
                MonitoredVehicleJourney monitoredVehicleJourney = new MonitoredVehicleJourney(
                        resultList.get(i-1).getRouteId(),
                        resultList.get(i-1).getRouteLongName(),
                        GlobalHelper.directionName(resultList.get(i-1).getDirectionId()),
                        resultList.get(i-1).getTripId(),
                        resultList.get(i-1).getTripHeadSign(),
                        resultList.get(i-1).getAgencyId(),
                        resultList.get(i-1).getFirstStopId(),
                        resultList.get(i-1).getFirstStopName(),
                        resultList.get(i-1).getLastStopId(),
                        resultList.get(i-1).getLastStopName(),
                        location,
                        resultList.get(i-1).getVehicleLabel(),
                        monitoredCall,
                        onwardCalls
                );
                VehicleActivity vehicleActivity = new VehicleActivity(
                        timeService.unixToZoneDateTime(resultList.get(i-1).getTimestamp()),
                        monitoredVehicleJourney
                );
                vehicleMonitoringDelivery.getVehicleActivities().add(vehicleActivity);

                location = new Location(row.getPositionLongitude(), row.getPositionLatitude());
                monitoredCall = new MonitoredCall(
                        row.getStopId(),
                        row.getStopName(),
                        row.getStopSequence(),
                        row.getCurrentStatus(),
                        row.getCurrentStatus() != null ? (row.getCurrentStatus().equals(STOPPED_AT) ? location : new Location()) : new Location(),
                        row.getWheelchairBoarding(),
                        timeService.durToZoneDateTime(row.getAimedArrivalTime(), row.getTripStartDate()),
                        timeService.unixToZoneDateTime(row.getExpectedArrivalTime()),
                        row.getArrivalDelay(),
                        timeService.durToZoneDateTime(row.getAimedDepartureTime(), row.getTripStartDate()),
                        timeService.unixToZoneDateTime(row.getExpectedDepartureTime()),
                        row.getDepartureDelay()
                );

                // Re-initial object
                onwardCalls = new OnwardCalls();
                vehicleLabel = row.getVehicleLabel();
                continue;
            }

            OnwardCall onwardCall = new OnwardCall(
                    row.getStopId(),
                    row.getStopName(),
                    row.getStopSequence(),
                    row.getWheelchairBoarding(),
                    timeService.durToZoneDateTime(row.getAimedArrivalTime(), row.getTripStartDate()),
                    timeService.unixToZoneDateTime(row.getExpectedArrivalTime()),
                    row.getArrivalDelay(),
                    timeService.durToZoneDateTime(row.getAimedDepartureTime(), row.getTripStartDate()),
                    timeService.unixToZoneDateTime(row.getExpectedDepartureTime()),
                    row.getDepartureDelay()
            );
            onwardCalls.getOnwardCalls().add(onwardCall);
        }

        // Insert last value in MonitoredVehicleJourney
        MonitoredVehicleJourney monitoredVehicleJourney = new MonitoredVehicleJourney(
                resultList.get(0).getRouteId(),
                resultList.get(0).getRouteLongName(),
                GlobalHelper.directionName(resultList.get(0).getDirectionId()),
                resultList.get(0).getTripId(),
                resultList.get(0).getTripHeadSign(),
                resultList.get(0).getAgencyId(),
                resultList.get(0).getFirstStopId(),
                resultList.get(0).getFirstStopName(),
                resultList.get(0).getLastStopId(),
                resultList.get(0).getLastStopName(),
                location,
                resultList.get(0).getVehicleLabel(),
                monitoredCall,
                onwardCalls
        );
        VehicleActivity vehicleActivity = new VehicleActivity(
                timeService.unixToZoneDateTime(resultList.get(0).getTimestamp()),
                monitoredVehicleJourney
        );
        vehicleMonitoringDelivery.getVehicleActivities().add(vehicleActivity);

        // Add to root element
        ServiceDelivery serviceDelivery = new ServiceDelivery();
        serviceDelivery.setResponseTimestamp(timeService.localDateTimeZone());
        serviceDelivery.setProducerRef(resultList.get(0).getAgencyId());
        serviceDelivery.setStatus(true);
        serviceDelivery.getVehicleMonitoringDeliveries().add(vehicleMonitoringDelivery);

        Gtfs gtfs = new Gtfs();
        gtfs.setVersion(gtfs.getVersion());
        gtfs.setServiceDelivery(serviceDelivery);

        return gtfs;
    }

    public String getRealVehicleMonitoringXml(String agency_id, String vehicle_id) {
        List<VehicleMonitoring> resultList;
        if (vehicle_id == null) {
            resultList = vehicleMonitoringRepository.findVehicleMonitoringByAgency(agency_id);
        } else {
            resultList = vehicleMonitoringRepository.findVehicleMonitoringByParam(
                    agency_id,
                    vehicle_id);
        }

        if (resultList == null || resultList.size() <= 0) {
            return null;
        }
        Gtfs gtfs = getDataVehicleMonitoring(resultList);
        return GtfsXml.objectToStrXml(gtfs);
    }

    public String getRealVehicleMonitoringJson(String agency_id, String vehicle_id) throws Exception {
        List<VehicleMonitoring> resultList;
        if (vehicle_id == null) {
            resultList = vehicleMonitoringRepository.findVehicleMonitoringByAgency(agency_id);
        } else {
            resultList = vehicleMonitoringRepository.findVehicleMonitoringByParam(
                    agency_id,
                    vehicle_id);
        }

        if (resultList == null || resultList.size() <= 0) {
            return null;
        }
        Gtfs gtfs = getDataVehicleMonitoring(resultList);
        return GtfsJson.toJson(gtfs);
    }

    private void resetTimeDummyData(Iterable<CSVRecord> csvRecords, CSVParser csvParser) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(new ClassPathResource(PATH_DUMMY_VM).getFile().getAbsolutePath()));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader(csvParser.getHeaderNames().toArray(new String[0])))) {

            long unixTime = Instant.now().getEpochSecond();
            long timestamp = unixTime;
            int tripStartDate = timeService.localDateZoneGTFS();
            LocalTime time;
            for (CSVRecord csvRecord : csvRecords) {
                time = timeService.unixToTime(unixTime);
                csvPrinter.printRecord(
                        csvRecord.get("vehicle_label"),
                        tripStartDate,
                        csvRecord.get("agency_id"),
                        csvRecord.get("route_id"),
                        csvRecord.get("route_long_name"),
                        csvRecord.get("trip_id"),
                        csvRecord.get("trip_headsign"),
                        csvRecord.get("stop_id"),
                        csvRecord.get("stop_name"),
                        csvRecord.get("position_latitude"),
                        csvRecord.get("position_longitude"),
                        csvRecord.get("stop_sequence"),
                        csvRecord.get("first_stop_id"),
                        csvRecord.get("first_stop_name"),
                        csvRecord.get("last_stop_id"),
                        csvRecord.get("last_stop_name"),
                        time, // aimed_departure_time
                        csvRecord.get("departure_delay"),
                        unixTime + Integer.parseInt(csvRecord.get("departure_delay")), // expected_departure_time
                        time, // aimed_arrival_time
                        csvRecord.get("arrival_delay"),
                        unixTime + Integer.parseInt(csvRecord.get("arrival_delay")), // expected_arrival_time
                        csvRecord.get("direction_id"),
                        csvRecord.get("current_status"),
                        timestamp // timestamp
                );
                unixTime += 120;
            }
            csvPrinter.flush();
        } catch (Exception e) {
            String logMessage = "Fail to update Vehicle Monitoring dummy file. " + e.getMessage();
            Logger.error(logMessage);
            e.printStackTrace();
            throw new RuntimeException(logMessage);
        }
    }

    private List<VehicleMonitoring> mappingDummyVehicleMonitoring(String agency_id, String vehicle_id) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new ClassPathResource(PATH_DUMMY_VM).getInputStream()));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            List<VehicleMonitoring> listVehicleMonitoring = new ArrayList<>();
            String trip = "";
            LocalTime nowTimeNl = timeService.timeZone();
            long unixTime = Instant.now().getEpochSecond();

            for (CSVRecord csvRecord : csvRecords) {
                // Just print 1 trip
                if (!trip.equals(csvRecord.get("trip_id")) && !trip.isEmpty()) {
                    break;
                }
                // Use like where query based on expected time
                if (LocalTime.parse(csvRecord.get("aimed_departure_time")).compareTo(nowTimeNl) >= 0
                        && (csvRecord.get("agency_id").equals(agency_id))
                        && (vehicle_id == null || csvRecord.get("vehicle_label").equals(vehicle_id))) {
                    trip = csvRecord.get("trip_id");
                    VehicleMonitoring vehicleMonitoring = new VehicleMonitoring(
                            csvRecord.get("agency_id"),
                            csvRecord.get("vehicle_label"),
                            csvRecord.get("route_id"),
                            csvRecord.get("trip_id"),
                            Integer.parseInt(csvRecord.get("stop_sequence")),
                            csvRecord.get("trip_start_date"),
                            csvRecord.get("trip_headsign"),
                            csvRecord.get("route_long_name"),
                            csvRecord.get("stop_id"),
                            Double.parseDouble(csvRecord.get("position_latitude")),
                            Double.parseDouble(csvRecord.get("position_longitude")),
                            csvRecord.get("stop_name"),
                            csvRecord.get("first_stop_id"),
                            csvRecord.get("first_stop_name"),
                            csvRecord.get("last_stop_id"),
                            csvRecord.get("last_stop_name"),
                            0,
                            timeService.strTimeToDuration(csvRecord.get("aimed_departure_time")),
                            Integer.parseInt(csvRecord.get("departure_delay")),
                            // Expected departure time
                            timeService.concatDateTime(csvRecord.get("aimed_departure_time")) + Integer.parseInt(csvRecord.get("departure_delay")),
                            timeService.strTimeToDuration(csvRecord.get("aimed_arrival_time")),
                            Integer.parseInt(csvRecord.get("arrival_delay")),
                            // Expected arrival time
                            timeService.concatDateTime(csvRecord.get("aimed_arrival_time")) + Integer.parseInt(csvRecord.get("arrival_delay")),
                            Integer.parseInt(csvRecord.get("direction_id")),
                            csvRecord.get("current_status"),
                            unixTime
                    );
                    listVehicleMonitoring.add(vehicleMonitoring);
                }
            }

            /*
             * Reset dummy data if no data available
             * Generate dummy file purpose only
             */
             // if (listVehicleMonitoring.isEmpty() || listVehicleMonitoring.size() < 1) {
             //    resetTimeDummyData(csvRecords, csvParser);
             // }

            return listVehicleMonitoring;
        } catch (Exception e) {
            String logMessage = "Fail to parse Vehicle Monitoring dummy file. " + e.getMessage();
            Logger.error(logMessage);
            throw new RuntimeException(logMessage);
        }
    }

    public String getDummyVehicleMonitoringXml(String agency_id, String vehicle_id) {
        List<VehicleMonitoring> resultList = mappingDummyVehicleMonitoring(agency_id, vehicle_id);
        if (resultList == null || resultList.size() <= 0) {
            return null;
        }
        Gtfs gtfs = getDataVehicleMonitoring(resultList);
        return GtfsXml.objectToStrXml(gtfs);
    }

    public String getDummyVehicleMonitoringJson(String agency_id, String vehicle_id) throws Exception {
        List<VehicleMonitoring> resultList = mappingDummyVehicleMonitoring(agency_id, vehicle_id);
        if (resultList == null || resultList.size() <= 0) {
            return null;
        }
        Gtfs gtfs = getDataVehicleMonitoring(resultList);
        return GtfsJson.toJson(gtfs);
    }
}
