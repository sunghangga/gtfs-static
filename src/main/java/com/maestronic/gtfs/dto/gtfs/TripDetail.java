package com.maestronic.gtfs.dto.gtfs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripDetail {

    private String operatorRef;
    private String routeRef;
    private String routeName;
    private String directionRef;
    private String tripRef;
    private String tripName;
    private String stopPointRef;
    private Integer stopSequence;
    private Integer wheelchairBoarding;
    private ZonedDateTime aimedArrivalTime;
    private ZonedDateTime aimedDepartureTime;
}
