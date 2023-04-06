package com.maestronic.gtfs.entity;


import com.maestronic.gtfs.compositeid.CalendarDateCompositeId;

import javax.persistence.*;

@Entity
@IdClass(CalendarDateCompositeId.class)
@Table(name = CalendarDate.TABLE_NAME)
public class CalendarDate {

    public static final String TABLE_NAME= "calendar_dates";

    @Id
    @Column(name = "service_id")
    private String serviceId;

    @Id
    @Column(name = "date")
    private Integer date;

    @Column(name = "exception_type")
    private Integer exceptionType;

    public CalendarDate() {
    }

    public CalendarDate(String serviceId, Integer date, Integer exceptionType) {
        this.serviceId = serviceId;
        this.date = date;
        this.exceptionType = exceptionType;
    }
}
