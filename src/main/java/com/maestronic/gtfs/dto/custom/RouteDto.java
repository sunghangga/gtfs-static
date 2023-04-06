package com.maestronic.gtfs.dto.custom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteDto {

    private String routeId;
    private String agencyId;
    private String routeShortName;
    private String routeLongName;
    private String routeDesc;
    private Integer routeType;
    private String routeUrl;
    private String routeColor;
    private String routeTextColor;
    private Integer routeSortOrder;
    private Integer continuousPickup;
    private Integer continuousDropOff;
}
