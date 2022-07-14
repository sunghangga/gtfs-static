package com.maestronic.gtfs.entity;

import com.maestronic.gtfs.dto.gtfs.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Journey {

    private UUID key;
    private UUID prevKey;
    private String mode;
    private String tripId;
    private String stopId;
    private String stopName;
    private int stopSequence;
    private Duration arrivalTime;
    private Duration departureTime;

    private String operatorRef;
    private String routeRef;
    private String routeName;
    private String directionRef;
    private String tripName;
//    private Vehicle vehicle;
    private Integer wheelchairBoarding;
    private ZonedDateTime aimedArrivalTime;
    private ZonedDateTime aimedDepartureTime;
}
