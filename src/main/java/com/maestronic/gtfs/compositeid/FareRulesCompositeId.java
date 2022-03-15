package com.maestronic.gtfs.compositeid;

import java.io.Serializable;

public class FareRulesCompositeId implements Serializable {

    private String fareId;
    private String routeId;
    private String originId;
    private String destinationId;
    private String containsId;

    public FareRulesCompositeId() {
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
