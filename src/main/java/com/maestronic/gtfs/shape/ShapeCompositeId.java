package com.maestronic.gtfs.shape;

import java.io.Serializable;

public class ShapeCompositeId implements Serializable {

    private String shapeId;
    private int shapePtSequence;

    public ShapeCompositeId() {
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
