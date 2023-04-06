package com.maestronic.gtfs.dto.custom;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShapeBetweenStopDto {

    private double shapeLat;
    private double shapeLon;
}
