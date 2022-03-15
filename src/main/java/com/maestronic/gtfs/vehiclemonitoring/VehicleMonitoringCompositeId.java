package com.maestronic.gtfs.vehiclemonitoring;

import java.io.Serializable;

public class VehicleMonitoringCompositeId implements Serializable {

    private String agencyId;
    private String vehicleLabel;
    private String routeId;
    private String tripId;
    private int stopSequence;
    private int directionId;

    public VehicleMonitoringCompositeId() {
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
