package com.maestronic.gtfs.connectiontimetable;

import java.io.Serializable;

public class ConnectionTimetableCompositeId implements Serializable {

    private int date;
    private int directionId;
    private String tripId;
    private String routeId;
    private String agencyId;
    private String stopId;
    private int stopSequence;

    public ConnectionTimetableCompositeId() {
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
