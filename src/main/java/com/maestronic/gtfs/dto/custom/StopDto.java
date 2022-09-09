package com.maestronic.gtfs.dto.custom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StopDto {

    private String stopId;
    private String stopCode;
    private String stopName;
    private String ttsStopName;
    private String stopDesc;
    private double stopLat;
    private double stopLon;
    private String zoneId;
    private String stopUrl;
    private Integer locationType;
    private String parentStation;
    private String stopTimezone;
    private Integer wheelchairBoarding;
    private String levelId;
    private String platformCode;
}
