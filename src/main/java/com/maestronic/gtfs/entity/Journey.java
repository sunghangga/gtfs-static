package com.maestronic.gtfs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Journey {

    private Integer key;
    private Integer prevKey;
    private String mode;
    private String prevMode;
    private String tripId;
    private String stopId;
    private String stopName;
    private int stopSequence;
    private Duration aimedArrivalTime;
    private Duration aimedDepartureTime;

    private String operatorRef;
    private String routeRef;
    private String routeName;
    private String directionRef;
    private String tripName;
//    private Vehicle vehicle;
    private Integer wheelchairBoarding;
    private ZonedDateTime expectedArrivalTime;
    private ZonedDateTime expectedDepartureTime;
}
