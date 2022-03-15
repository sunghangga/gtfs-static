package com.maestronic.gtfs.calendardate;

import java.io.Serializable;

public class CalendarDateCompositeId implements Serializable {

    private String serviceId;
    private Integer date;

    public CalendarDateCompositeId() {
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
