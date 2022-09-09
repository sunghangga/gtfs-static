package com.maestronic.gtfs.service;

import com.maestronic.gtfs.dto.custom.VehicleMonitoringDummyDto;
import com.maestronic.gtfs.dto.siri.*;
import com.maestronic.gtfs.entity.VehicleMonitoring;
import com.maestronic.gtfs.repository.ConnectionTimetableRepository;
import com.maestronic.gtfs.repository.VehicleMonitoringDummyRepository;
import com.maestronic.gtfs.repository.VehicleMonitoringRepository;
import com.maestronic.gtfs.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class VehicleConnectionService implements GlobalVariable {

    @Autowired
    private TimeService timeService;
    @Autowired
    private VehicleMonitoringRepository vehicleMonitoringRepository;
    @Autowired
    private VehicleMonitoringDummyRepository vehicleMonitoringDummyRepository;
    @Autowired
    private ConnectionTimetableRepository connectionTimetableRepository;
    private MonitoredCall monitoredCall;
    private Location location;
    @Value("${timezone}")
    private String timezone;

    private List<ConnectionRoutes> getConnectionRoutes(String routeId, String stopId) {
        List<Tuple> resultConnectionList = connectionTimetableRepository.findConnectionRoutesByParam(routeId, stopId);
        List<ConnectionRoutes> connectionList = new ArrayList<>();

        for (Tuple connection : resultConnectionList) {
            connectionList.add(
                    new ConnectionRoutes(
                            connection.get("route_id").toString(),
                            connection.get("route_short_name").toString(),
                            connection.get("route_long_name").toString(),
                            connection.get("route_type").toString()
                    )
            );
        }
        return connectionList;
    }

    private String mapFirstRow(VehicleMonitoring row) {
        location = new Location(row.getPositionLongitude(), row.getPositionLatitude());
        // Find connection routes

        monitoredCall = new MonitoredCall(
                row.getStopId(),
                row.getStopName(),
                row.getStopSequence(),
                row.getCurrentStatus(),
                row.getCurrentStatus() != null ? (row.getCurrentStatus().equals(STOPPED_AT) ? location : new Location()) : new Location(),
                row.getWheelchairBoarding(),
                row.getStopScheduleRelationship(),
                timeService.durToZoneDateTime(row.getAimedArrivalTime(), row.getTripStartDate()),
                timeService.unixToZoneDateTime(row.getExpectedArrivalTime()),
                row.getArrivalDelay(),
                timeService.durToZoneDateTime(row.getAimedDepartureTime(), row.getTripStartDate()),
                timeService.unixToZoneDateTime(row.getExpectedDepartureTime()),
                row.getDepartureDelay(),
                getConnectionRoutes(row.getRouteId(), row.getStopId())
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
                        resultList.get(i-1).getTripScheduleRelationship(),
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
                        row.getStopScheduleRelationship(),
                        timeService.durToZoneDateTime(row.getAimedArrivalTime(), row.getTripStartDate()),
                        timeService.unixToZoneDateTime(row.getExpectedArrivalTime()),
                        row.getArrivalDelay(),
                        timeService.durToZoneDateTime(row.getAimedDepartureTime(), row.getTripStartDate()),
                        timeService.unixToZoneDateTime(row.getExpectedDepartureTime()),
                        row.getDepartureDelay(),
                        getConnectionRoutes(row.getRouteId(), row.getStopId())
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
                    row.getStopScheduleRelationship(),
                    timeService.durToZoneDateTime(row.getAimedArrivalTime(), row.getTripStartDate()),
                    timeService.unixToZoneDateTime(row.getExpectedArrivalTime()),
                    row.getArrivalDelay(),
                    timeService.durToZoneDateTime(row.getAimedDepartureTime(), row.getTripStartDate()),
                    timeService.unixToZoneDateTime(row.getExpectedDepartureTime()),
                    row.getDepartureDelay(),
                    getConnectionRoutes(row.getRouteId(), row.getStopId())
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
                resultList.get(0).getTripScheduleRelationship(),
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

//    public Gtfs getDataVehicleMonitoringDummy(List<VehicleMonitoringDummyDto> resultList) {
//        // Initialize object
//        OnwardCalls onwardCalls = new OnwardCalls();
//        VehicleMonitoringDelivery vehicleMonitoringDelivery = new VehicleMonitoringDelivery();
//        String vehicleLabel = mapFirstRow(resultList.get(0));
//        VehicleMonitoring row;
//
//        for (int i = 1; i < resultList.size(); i++) {
//            row = resultList.get(i);
//            // Check for different vehicle
//            if (!vehicleLabel.equals(row.getVehicleLabel())) {
//                MonitoredVehicleJourney monitoredVehicleJourney = new MonitoredVehicleJourney(
//                        resultList.get(i-1).getRouteId(),
//                        resultList.get(i-1).getRouteLongName(),
//                        GlobalHelper.directionName(resultList.get(i-1).getDirectionId()),
//                        resultList.get(i-1).getTripId(),
//                        resultList.get(i-1).getTripHeadSign(),
//                        resultList.get(i-1).getTripScheduleRelationship(),
//                        resultList.get(i-1).getAgencyId(),
//                        resultList.get(i-1).getFirstStopId(),
//                        resultList.get(i-1).getFirstStopName(),
//                        resultList.get(i-1).getLastStopId(),
//                        resultList.get(i-1).getLastStopName(),
//                        location,
//                        resultList.get(i-1).getVehicleLabel(),
//                        monitoredCall,
//                        onwardCalls
//                );
//                VehicleActivity vehicleActivity = new VehicleActivity(
//                        timeService.unixToZoneDateTime(resultList.get(i-1).getTimestamp()),
//                        monitoredVehicleJourney
//                );
//                vehicleMonitoringDelivery.getVehicleActivities().add(vehicleActivity);
//
//                location = new Location(row.getPositionLongitude(), row.getPositionLatitude());
//                monitoredCall = new MonitoredCall(
//                        row.getStopId(),
//                        row.getStopName(),
//                        row.getStopSequence(),
//                        row.getCurrentStatus(),
//                        row.getCurrentStatus() != null ? (row.getCurrentStatus().equals(STOPPED_AT) ? location : new Location()) : new Location(),
//                        row.getWheelchairBoarding(),
//                        row.getStopScheduleRelationship(),
//                        timeService.durToZoneDateTime(row.getAimedArrivalTime(), row.getTripStartDate()),
//                        timeService.unixToZoneDateTime(row.getExpectedArrivalTime()),
//                        row.getArrivalDelay(),
//                        timeService.durToZoneDateTime(row.getAimedDepartureTime(), row.getTripStartDate()),
//                        timeService.unixToZoneDateTime(row.getExpectedDepartureTime()),
//                        row.getDepartureDelay(),
//                        getConnectionRoutes(row.getRouteId(), row.getStopId())
//                );
//
//                // Re-initial object
//                onwardCalls = new OnwardCalls();
//                vehicleLabel = row.getVehicleLabel();
//                continue;
//            }
//
//            OnwardCall onwardCall = new OnwardCall(
//                    row.getStopId(),
//                    row.getStopName(),
//                    row.getStopSequence(),
//                    row.getWheelchairBoarding(),
//                    row.getStopScheduleRelationship(),
//                    timeService.durToZoneDateTime(row.getAimedArrivalTime(), row.getTripStartDate()),
//                    timeService.unixToZoneDateTime(row.getExpectedArrivalTime()),
//                    row.getArrivalDelay(),
//                    timeService.durToZoneDateTime(row.getAimedDepartureTime(), row.getTripStartDate()),
//                    timeService.unixToZoneDateTime(row.getExpectedDepartureTime()),
//                    row.getDepartureDelay(),
//                    getConnectionRoutes(row.getRouteId(), row.getStopId())
//            );
//            onwardCalls.getOnwardCalls().add(onwardCall);
//        }
//
//        // Insert last value in MonitoredVehicleJourney
//        MonitoredVehicleJourney monitoredVehicleJourney = new MonitoredVehicleJourney(
//                resultList.get(0).getRouteId(),
//                resultList.get(0).getRouteLongName(),
//                GlobalHelper.directionName(resultList.get(0).getDirectionId()),
//                resultList.get(0).getTripId(),
//                resultList.get(0).getTripHeadSign(),
//                resultList.get(0).getTripScheduleRelationship(),
//                resultList.get(0).getAgencyId(),
//                resultList.get(0).getFirstStopId(),
//                resultList.get(0).getFirstStopName(),
//                resultList.get(0).getLastStopId(),
//                resultList.get(0).getLastStopName(),
//                location,
//                resultList.get(0).getVehicleLabel(),
//                monitoredCall,
//                onwardCalls
//        );
//        VehicleActivity vehicleActivity = new VehicleActivity(
//                timeService.unixToZoneDateTime(resultList.get(0).getTimestamp()),
//                monitoredVehicleJourney
//        );
//        vehicleMonitoringDelivery.getVehicleActivities().add(vehicleActivity);
//
//        // Add to root element
//        ServiceDelivery serviceDelivery = new ServiceDelivery();
//        serviceDelivery.setResponseTimestamp(timeService.localDateTimeZone());
//        serviceDelivery.setProducerRef(resultList.get(0).getAgencyId());
//        serviceDelivery.setStatus(true);
//        serviceDelivery.getVehicleMonitoringDeliveries().add(vehicleMonitoringDelivery);
//
//        Gtfs gtfs = new Gtfs();
//        gtfs.setVersion(gtfs.getVersion());
//        gtfs.setServiceDelivery(serviceDelivery);
//
//        return gtfs;
//    }

    public String getVehicleMonitorConnection(String agency_id, String vehicle_id, Long approx) throws Exception {
        List<VehicleMonitoring> resultList;
        long timestamp = approx == 0 ? 0 : timeService.currentTimeToUnix() + approx;
        if (vehicle_id == null) {
            resultList = vehicleMonitoringRepository.findVehicleMonitoringByAgency(agency_id, timestamp);
        } else {
            resultList = vehicleMonitoringRepository.findVehicleMonitoringByParam(
                    agency_id,
                    vehicle_id,
                    timestamp);
        }

        if (resultList == null || resultList.size() <= 0) {
            return null;
        }
        Gtfs gtfs = getDataVehicleMonitoring(resultList);
        return GtfsJson.toJson(gtfs);
    }

    public String getDummyVehicleMonitorConnection(String agencyId, String date, String startTime, String endTime, Integer limit) throws Exception {
        LocalDate dateFormat = timeService.strToLocalDate(date);
        int dateGTFS = timeService.localDateZoneGTFSByDate(dateFormat);
        String day = dateFormat.getDayOfWeek().name().toLowerCase();

        List<VehicleMonitoringDummyDto> resultList = vehicleMonitoringDummyRepository.findDummyVehicleMonitoringByParam(
                agencyId,
                dateGTFS,
                day,
                startTime,
                endTime);

        if (resultList == null || resultList.size() <= 0) {
            return null;
        }

        // Use this function to get monitoring dummy data (not finish yet)
//        Gtfs gtfs = getDataVehicleMonitoringDummy(resultList);
//        return GtfsJson.toJson(gtfs);
        return null;
    }
}
