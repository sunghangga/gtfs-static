package com.maestronic.gtfs.entity;

import javax.persistence.*;

@Entity
@Table(name = Calendar.TABLE_NAME)
public class Calendar {

    public static final String TABLE_NAME= "calendar";

    @Id
    @Column(name = "service_id")
    private String serviceId;

    @Column(name = "monday")
    private int monday;

    @Column(name = "tuesday")
    private int tuesday;

    @Column(name = "wednesday")
    private int wednesday;

    @Column(name = "thursday")
    private int thursday;

    @Column(name = "friday")
    private int friday;

    @Column(name = "saturday")
    private int saturday;

    @Column(name = "sunday")
    private int sunday;

    @Column(name = "start_date")
    private int start_date;

    @Column(name = "end_date")
    private int end_date;

    public Calendar() {
    }

    public Calendar(String serviceId, int monday, int tuesday, int wednesday, int thursday, int friday, int saturday, int sunday, int start_date, int end_date) {
        this.serviceId = serviceId;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
        this.start_date = start_date;
        this.end_date = end_date;
    }
}
