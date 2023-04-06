package com.maestronic.gtfs.compositeid;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ConnectionTimetableCompositeId implements Serializable {

    private int date;
    private int directionId;
    private String tripId;
    private String routeId;
    private String agencyId;
    private String stopId;
    private int stopSequence;
}
