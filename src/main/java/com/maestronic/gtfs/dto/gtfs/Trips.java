package com.maestronic.gtfs.dto.gtfs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trips {

    private String stopPointNames;
    private ZonedDateTime expectedArrivalTime;
    private ZonedDateTime expectedDepartureTime;
    private TripDetail tripDetail;
}
