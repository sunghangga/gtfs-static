package com.maestronic.gtfs.entity;

import com.maestronic.gtfs.compositeid.ConnectionTimetableCompositeId;

import javax.persistence.*;
import java.time.Duration;

@Entity
@IdClass(ConnectionTimetableCompositeId.class)
@Table(name = "connection_timetable")
public class ConnectionTimetable {

    @Id
    @Column(name = "date")
    private Integer date;
    @Id
    @Column(name = "direction_id")
    private int directionId;
    @Id
    @Column(name = "trip_id")
    private String tripId;
    @Id
    @Column(name = "route_id")
    private String routeId;
    @Id
    @Column(name = "agency_id")
    private String agencyId;
    @Id
    @Column(name = "stop_id")
    private String stopId;
    @Id
    @Column(name = "stop_sequence")
    private int stopSequence;

    @Column(name = "transportmode")
    private String transportMode;
    @Column(name = "trip_headsign")
    private String tripHeadSign;
    @Column(name = "first_stop_id")
    private String firstStopId;
    @Column(name = "last_stop_id")
    private String lastStopId;
    @Column(name = "departure_time", columnDefinition = "interval")
    private Duration departureTime;
    @Column(name = "arrival_time", columnDefinition = "interval")
    private Duration arrivalTime;
    @Column(name = "route_long_name")
    private String routeLongName;
    @Column(name = "stop_name")
    private String stopName;
    @Column(name = "stop_lat")
    private double stopLat;
    @Column(name = "stop_lon")
    private double stopLon;
    @Column(name = "first_stop_name")
    private String firstStopName;
    @Column(name = "last_stop_name")
    private String lastStopName;
    @Column(name = "disabledaccessible")
    private String disabledAccessible;
    @Column(name = "visuallyaccessible")
    private String visuallyAccessible;

    public ConnectionTimetable() {
    }

    public ConnectionTimetable(Integer date, int directionId, String tripId, String routeId, String agencyId, String stopId, int stopSequence, String transportMode, String tripHeadSign, String firstStopId, String lastStopId, Duration departureTime, Duration arrivalTime, String routeLongName, String stopName, double stopLat, double stopLon, String firstStopName, String lastStopName, String disabledAccessible, String visuallyAccessible) {
        this.date = date;
        this.directionId = directionId;
        this.tripId = tripId;
        this.routeId = routeId;
        this.agencyId = agencyId;
        this.stopId = stopId;
        this.stopSequence = stopSequence;
        this.transportMode = transportMode;
        this.tripHeadSign = tripHeadSign;
        this.firstStopId = firstStopId;
        this.lastStopId = lastStopId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.routeLongName = routeLongName;
        this.stopName = stopName;
        this.stopLat = stopLat;
        this.stopLon = stopLon;
        this.firstStopName = firstStopName;
        this.lastStopName = lastStopName;
        this.disabledAccessible = disabledAccessible;
        this.visuallyAccessible = visuallyAccessible;
    }

    public Integer getDate() {
        return date;
    }

    public int getDirectionId() {
        return directionId;
    }

    public String getTripId() {
        return tripId;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public String getStopId() {
        return stopId;
    }

    public int getStopSequence() {
        return stopSequence;
    }

    public String getTransportMode() {
        return transportMode;
    }

    public String getTripHeadSign() {
        return tripHeadSign;
    }

    public String getFirstStopId() {
        return firstStopId;
    }

    public String getLastStopId() {
        return lastStopId;
    }

    public Duration getDepartureTime() {
        return departureTime;
    }

    public Duration getArrivalTime() {
        return arrivalTime;
    }

    public String getRouteLongName() {
        return routeLongName;
    }

    public String getStopName() {
        return stopName;
    }

    public double getStopLat() {
        return stopLat;
    }

    public double getStopLon() {
        return stopLon;
    }

    public String getFirstStopName() {
        return firstStopName;
    }

    public String getLastStopName() {
        return lastStopName;
    }

    public String getDisabledAccessible() {
        return disabledAccessible;
    }

    public String getVisuallyAccessible() {
        return visuallyAccessible;
    }
}
