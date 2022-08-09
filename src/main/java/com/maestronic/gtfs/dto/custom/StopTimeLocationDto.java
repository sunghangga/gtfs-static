package com.maestronic.gtfs.dto.custom;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StopTimeLocationDto {

    private String tripId;
    private String stopId;
    private double stopLat;
    private double stopLon;
    private int stopSequence;
}
