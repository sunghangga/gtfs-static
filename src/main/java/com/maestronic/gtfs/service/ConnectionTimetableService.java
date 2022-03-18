package com.maestronic.gtfs.service;

import com.maestronic.gtfs.entity.ConnectionTimetable;
import com.maestronic.gtfs.mapclass.*;
import com.maestronic.gtfs.repository.ConnectionTimetableRepository;
import com.maestronic.gtfs.util.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConnectionTimetableService implements GlobalVariable {

    @Autowired
    private ConnectionTimetableRepository connectionTimetableRepository;
    @Autowired
    private TimeService timeService;
    @Value("${timezone}")
    private String timezone;

    private Gtfs getDataConnectionTimetable(List<ConnectionTimetable> resultList, String stop_id) {
        ConnectionTimetable row;
        ConnectionTimetableDelivery connectionTimetableDelivery = new ConnectionTimetableDelivery();

        for (int i = 0; i < resultList.size(); i++) {
            row = resultList.get(i);

            FeederJourney feederJourney = new FeederJourney(
                    row.getRouteId(),
                    row.getRouteLongName(),
                    GlobalHelper.directionName(row.getDirectionId()),
                    row.getTripId(),
                    row.getTripHeadSign(),
                    row.getAgencyId(),
                    row.getFirstStopId(),
                    row.getFirstStopName(),
                    row.getLastStopId(),
                    row.getLastStopName(),
                    row.getTransportMode(),
                    row.getDisabledAccessible(),
                    row.getVisuallyAccessible(),
                    timeService.durToZoneDateTime(row.getDepartureTime(), row.getDate().toString())
            );

            TimetabledFeederArrival timetabledFeederArrival = new TimetabledFeederArrival(
                    row.getStopId(),
                    row.getStopName(),
                    timeService.durToZoneDateTime(row.getArrivalTime(), row.getDate().toString()),
                    feederJourney
            );
            connectionTimetableDelivery.getTimetabledFeederArrivals().add(timetabledFeederArrival);
        }
        // Add to root element
        connectionTimetableDelivery.setMonitoringRef(stop_id);
        ServiceDelivery serviceDelivery = new ServiceDelivery();
        serviceDelivery.setResponseTimestamp(timeService.localDateTimeZone());
        serviceDelivery.setStatus(true);
        serviceDelivery.getConnectionTimetableDeliveries().add(connectionTimetableDelivery);

        Gtfs gtfs = new Gtfs();
        gtfs.setVersion(gtfs.getVersion());
        gtfs.setServiceDelivery(serviceDelivery);

        return gtfs;
    }

    public String getRealConnectionTimetableXml(String stop_id) {
        List<ConnectionTimetable> resultList = connectionTimetableRepository.findConnectionTimetableByParam(stop_id, timezone);
        if (resultList == null || resultList.size() <= 0) {
            return null;
        }
        Gtfs gtfs = getDataConnectionTimetable(resultList, stop_id);
        return GtfsXml.objectToStrXml(gtfs);
    }

    public String getRealConnectionTimetableJson(String stop_id) throws Exception {
        List<ConnectionTimetable> resultList = connectionTimetableRepository.findConnectionTimetableByParam(stop_id, timezone);
        if (resultList == null || resultList.size() <= 0) {
            return null;
        }
        Gtfs gtfs = getDataConnectionTimetable(resultList, stop_id);
        return GtfsJson.toJson(gtfs);
    }

    private List<ConnectionTimetable> mappingDummyConnectionTimetable(String stop_id) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new ClassPathResource(PATH_DUMMY_CT).getInputStream()));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            List<ConnectionTimetable> listConnectionTimetable = new ArrayList<>();

            for (CSVRecord csvRecord : csvRecords) {
                ConnectionTimetable connectionTimetable = new ConnectionTimetable(
                        timeService.localDateZoneGTFS(),
                        Integer.parseInt(csvRecord.get("direction_id")),
                        csvRecord.get("trip_id"),
                        csvRecord.get("route_id"),
                        csvRecord.get("agency_id"),
                        csvRecord.get("stop_id"),
                        Integer.parseInt(csvRecord.get("stop_sequence")),
                        csvRecord.get("transportmode"),
                        csvRecord.get("trip_headsign"),
                        csvRecord.get("first_stop_id"),
                        csvRecord.get("last_stop_id"),
                        timeService.strTimeToDuration(csvRecord.get("departure_time")),
                        timeService.strTimeToDuration(csvRecord.get("arrival_time")),
                        csvRecord.get("route_long_name"),
                        csvRecord.get("stop_name"),
                        Double.parseDouble(csvRecord.get("stop_lat")),
                        Double.parseDouble(csvRecord.get("stop_lon")),
                        csvRecord.get("first_stop_name"),
                        csvRecord.get("last_stop_name"),
                        csvRecord.get("disabledaccessible"),
                        csvRecord.get("visuallyaccessible")
                );
                listConnectionTimetable.add(connectionTimetable);
            }

            return listConnectionTimetable;
        } catch (Exception e) {
            String logMessage = "Fail to parse Connection Timetable dummy file. " + e.getMessage();
            Logger.error(logMessage);
            throw new RuntimeException(logMessage);
        }
    }

    public String getDummyConnectionTimetableXml(String stop_id) {
        List<ConnectionTimetable> resultList = mappingDummyConnectionTimetable(stop_id);
        if (resultList == null || resultList.size() <= 0) {
            return null;
        }
        Gtfs gtfs = getDataConnectionTimetable(resultList, stop_id);
        return GtfsXml.objectToStrXml(gtfs);
    }

    public String getDummyConnectionTimetableJson(String stop_id) throws Exception {
        List<ConnectionTimetable> resultList = mappingDummyConnectionTimetable(stop_id);
        if (resultList == null || resultList.size() <= 0) {
            return null;
        }
        Gtfs gtfs = getDataConnectionTimetable(resultList, stop_id);
        return GtfsJson.toJson(gtfs);
    }
}
