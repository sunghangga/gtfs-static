package com.maestronic.gtfs.vehiclemonitoring;

import javax.persistence.*;
import java.time.Duration;

@Entity
@IdClass(VehicleMonitoringCompositeId.class)
@Table(name = "vehicle_monitoring")
public class VehicleMonitoring {

    @Id
    @Column(name = "agency_id")
    private String agencyId;
    @Id
    @Column(name = "vehicle_label")
    private String vehicleLabel;
    @Id
    @Column(name = "route_id")
    private String routeId;
    @Id
    @Column(name = "trip_id")
    private String tripId;
    @Id
    @Column(name = "stop_sequence")
    private int stopSequence;
    @Id
    @Column(name = "direction_id")
    private int directionId;

    @Column(name = "trip_start_date")
    private String tripStartDate;
    @Column(name = "trip_headsign")
    private String tripHeadSign;
    @Column(name = "route_long_name")
    private String routeLongName;
    @Column(name = "stop_id")
    private String stopId;
    @Column(name = "position_latitude")
    private double positionLatitude;
    @Column(name = "position_longitude")
    private double positionLongitude;
    @Column(name = "stop_name")
    private String stopName;
    @Column(name = "first_stop_id")
    private String firstStopId;
    @Column(name = "first_stop_name")
    private String firstStopName;
    @Column(name = "last_stop_id")
    private String lastStopId;
    @Column(name = "last_stop_name")
    private String lastStopName;
    @Column(name = "aimed_departure_time", columnDefinition = "interval")
    private Duration aimedDepartureTime;
    @Column(name = "departure_delay")
    private int departureDelay;
    @Column(name = "expected_departure_time")
    private long expectedDepartureTime;
    @Column(name = "aimed_arrival_time", columnDefinition = "interval")
    private Duration aimedArrivalTime;
    @Column(name = "arrival_delay")
    private int arrivalDelay;
    @Column(name = "expected_arrival_time")
    private long expectedArrivalTime;
    @Column(name = "current_status")
    private String currentStatus;
    @Column(name = "timestamp")
    private long timestamp;

    public VehicleMonitoring() {
    }

    public VehicleMonitoring(String agencyId, String vehicleLabel, String routeId, String tripId, int stopSequence, String tripStartDate, String tripHeadSign, String routeLongName, String stopId, double positionLatitude, double positionLongitude, String stopName, String firstStopId, String firstStopName, String lastStopId, String lastStopName, Duration aimedDepartureTime, int departureDelay, long expectedDepartureTime, Duration aimedArrivalTime, int arrivalDelay, long expectedArrivalTime, int directionId, String currentStatus, long timestamp) {
        this.agencyId = agencyId;
        this.vehicleLabel = vehicleLabel;
        this.routeId = routeId;
        this.tripId = tripId;
        this.stopSequence = stopSequence;
        this.tripStartDate = tripStartDate;
        this.tripHeadSign = tripHeadSign;
        this.routeLongName = routeLongName;
        this.stopId = stopId;
        this.positionLatitude = positionLatitude;
        this.positionLongitude = positionLongitude;
        this.stopName = stopName;
        this.firstStopId = firstStopId;
        this.firstStopName = firstStopName;
        this.lastStopId = lastStopId;
        this.lastStopName = lastStopName;
        this.aimedDepartureTime = aimedDepartureTime;
        this.departureDelay = departureDelay;
        this.expectedDepartureTime = expectedDepartureTime;
        this.aimedArrivalTime = aimedArrivalTime;
        this.arrivalDelay = arrivalDelay;
        this.expectedArrivalTime = expectedArrivalTime;
        this.directionId = directionId;
        this.currentStatus = currentStatus;
        this.timestamp = timestamp;
    }

    public String getTripStartDate() {
        return tripStartDate;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public String getVehicleLabel() {
        return vehicleLabel;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getTripId() {
        return tripId;
    }

    public int getStopSequence() {
        return stopSequence;
    }

    public String getTripHeadSign() {
        return tripHeadSign;
    }

    public String getRouteLongName() {
        return routeLongName;
    }

    public String getStopId() {
        return stopId;
    }

    public double getPositionLatitude() {
        return positionLatitude;
    }

    public double getPositionLongitude() {
        return positionLongitude;
    }

    public String getStopName() {
        return stopName;
    }

    public String getFirstStopId() {
        return firstStopId;
    }

    public String getFirstStopName() {
        return firstStopName;
    }

    public String getLastStopId() {
        return lastStopId;
    }

    public String getLastStopName() {
        return lastStopName;
    }

    public Duration getAimedDepartureTime() {
        return aimedDepartureTime;
    }

    public int getDepartureDelay() {
        return departureDelay;
    }

    public long getExpectedDepartureTime() {
        return expectedDepartureTime;
    }

    public Duration getAimedArrivalTime() {
        return aimedArrivalTime;
    }

    public int getArrivalDelay() {
        return arrivalDelay;
    }

    public long getExpectedArrivalTime() {
        return expectedArrivalTime;
    }

    public int getDirectionId() {
        return directionId;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
