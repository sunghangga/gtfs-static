package com.maestronic.gtfs.entity;

import com.maestronic.gtfs.compositeid.VehicleMonitoringCompositeId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Duration;

@Entity
@IdClass(VehicleMonitoringCompositeId.class)
@Table(name = "vehicle_monitoring")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    @Column(name = "wheelchair_boarding")
    private Integer wheelchairBoarding;
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
    @Column(name = "trip_schedule_relationship")
    private String tripScheduleRelationship;
    @Column(name = "stop_schedule_relationship")
    private String stopScheduleRelationship;
    @Column(name = "bikes_allowed")
    private Integer bikesAllowed;
    @Column(name = "route_color")
    private String routeColor;
    @Column(name = "route_text_color")
    private String routeTextColor;
}
