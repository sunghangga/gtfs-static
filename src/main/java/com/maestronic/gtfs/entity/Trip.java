package com.maestronic.gtfs.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = Trip.TABLE_NAME)
public class Trip {

    public static final String TABLE_NAME = "trips";

    @Id
    @Column(name = "trip_id")
    private String tripId;

    @Column(name = "route_id")
    private String routeId;

    @Column(name = "service_id")
    private String serviceId;

    @Column(name = "realtime_trip_id")
    private String realtimeTripId;

    @Column(name = "trip_headsign")
    private String tripHeadsign;

    @Column(name = "trip_short_name")
    private String tripShortName;

    @Column(name = "trip_long_name")
    private String tripLongName;

    @Column(name = "direction_id")
    private Integer directionId;

    @Column(name = "block_id")
    private String blockId;

    @Column(name = "shape_id")
    private String shapeId;

    @Column(name = "wheelchair_accessible")
    private Integer wheelchairAccessible;

    @Column(name = "bikes_allowed")
    private Integer bikesAllowed;

    public Trip() {
    }

    public Trip(String tripId, String routeId, String serviceId, String realtimeTripId, String tripHeadsign, String tripShortName, String tripLongName, Integer directionId, String blockId, String shapeId, Integer wheelchairAccessible, Integer bikesAllowed) {
        this.tripId = tripId;
        this.routeId = routeId;
        this.serviceId = serviceId;
        this.realtimeTripId = realtimeTripId;
        this.tripHeadsign = tripHeadsign;
        this.tripShortName = tripShortName;
        this.tripLongName = tripLongName;
        this.directionId = directionId;
        this.blockId = blockId;
        this.shapeId = shapeId;
        this.wheelchairAccessible = wheelchairAccessible;
        this.bikesAllowed = bikesAllowed;
    }
}
