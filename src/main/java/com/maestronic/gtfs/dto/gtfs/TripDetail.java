package com.maestronic.gtfs.dto.gtfs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripDetail {

    private String mode;
    private Vehicle vehicle;
    private Location location;
    private String stopPointRef;
    private String stopPointNames;
    private Integer stopSequence;
    private Integer wheelchairBoarding;
    private String stopScheduleRelationship;
    private ZonedDateTime aimedArrivalTime;
    private ZonedDateTime expectedArrivalTime;
    private Integer arrivalDelay;
    private ZonedDateTime aimedDepartureTime;
    private ZonedDateTime expectedDepartureTime;
    private Integer departureDelay;
}
