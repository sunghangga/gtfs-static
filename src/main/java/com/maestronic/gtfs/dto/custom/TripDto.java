package com.maestronic.gtfs.dto.custom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripDto {

    private String tripId;
    private String routeId;
    private String serviceId;
    private String RealtimeTripId;
    private String tripHeadsign;
    private String tripShortName;
    private String tripLongName;
    private Integer directionId;
    private String blockId;
    private String shapeId;
    private Integer wheelchairAccessible;
    private Integer bikesAllowed;
}
