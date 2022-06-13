package com.maestronic.gtfs.compositeid;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class NearestStopCompositeId implements Serializable {

    private String tripId;
    private String stopId;
}
