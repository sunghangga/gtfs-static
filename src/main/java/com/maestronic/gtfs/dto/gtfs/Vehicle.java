package com.maestronic.gtfs.dto.gtfs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Vehicle {

    private String vehicleRef;
    private String vehicleStopStatus;
}
