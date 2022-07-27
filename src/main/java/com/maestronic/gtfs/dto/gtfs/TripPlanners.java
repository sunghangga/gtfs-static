package com.maestronic.gtfs.dto.gtfs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripPlanners {

    private String mode;
    private String stopPointNames;
    private ZonedDateTime expectedArrivalTime;
    private ZonedDateTime expectedDepartureTime;
    private Object pathDetail;
    private TripDetail tripDetail;
}
