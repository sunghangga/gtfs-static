package com.maestronic.gtfs.dto.gtfs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripPlanners {

    private String mode;
    private String originPointNames;
    private String destinationPointNames;
    private ZonedDateTime tripDepartureTime; // First time of trip
    private ZonedDateTime tripArrivalTime; // Last time of trip
    private Object pathDetail;
    private List<Trips> trips;
}
