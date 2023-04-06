package com.maestronic.gtfs.dto.custom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionRouteDto {

    private String routeId;
    private String routeShortName;
    private String routeLongName;
    private Integer routeType;
    private String tripId;
    private String stopId;
    private Integer stopSequence;
    private String aimedArrivalTime;
    private Long expectedArrivalTime;
    private Integer arrivalDelay;
    private Integer minTransferTime;
}
