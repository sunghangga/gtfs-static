package com.maestronic.gtfs.compositeid;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class VehicleMonitoringCompositeId implements Serializable {

    private String agencyId;
    private String vehicleLabel;
    private String routeId;
    private String tripId;
    private int stopSequence;
    private int directionId;
}
