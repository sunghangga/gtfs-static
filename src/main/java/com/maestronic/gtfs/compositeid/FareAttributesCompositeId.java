package com.maestronic.gtfs.compositeid;

import java.io.Serializable;

public class FareAttributesCompositeId implements Serializable {

    private String fareId;
    private String agencyId;

    public FareAttributesCompositeId() {
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
