package com.maestronic.gtfs.stoptime;

import java.io.Serializable;

public class StopTimeCompositeId implements Serializable {

    private String tripId;
    private Integer stopSequence;

    public StopTimeCompositeId() {
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
