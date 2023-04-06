package com.maestronic.gtfs.dto.custom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleMonitoringDummyDto {

    private String agencyId;
    private String routeId;
    private String routeLongName;
    private String tripId;
    private String tripHeadSign;
    private Integer wheelchairBoarding;
    private String stopId;
    private String stopName;
    private Integer stopSequence;
    private double stopLat;
    private double stopLon;
    private String firstStopId;
    private String firstStopName;
    private String lastStopId;
    private String lastStopName;
    private String aimedDepartureTime;
    private String aimedArrivalTime;
    private Integer directionId;
}
