package com.maestronic.gtfs.entity;

import com.maestronic.gtfs.compositeid.StopTimeCompositeId;
import com.vladmihalcea.hibernate.type.interval.PostgreSQLIntervalType;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.Duration;

@Entity
@IdClass(StopTimeCompositeId.class)
@Table(name = StopTime.TABLE_NAME)
@TypeDef(
        typeClass = PostgreSQLIntervalType.class,
        defaultForType = Duration.class
)
public class StopTime {

    public static final String TABLE_NAME= "stop_times";

    @Id
    @Column(name = "trip_id")
    private String tripId;

    @Column(name = "arrival_time", columnDefinition = "interval")
    private Duration arrivalTime;

    @Column(name = "departure_time", columnDefinition = "interval")
    private Duration departureTime;

    @Column(name = "stop_id")
    private String stopId;

    @Id
    @Column(name = "stop_sequence")
    private Integer stopSequence;

    @Column(name = "stop_headsign")
    private String stopHeadsign;

    @Column(name = "pickup_type")
    private Integer pickupType;

    @Column(name = "drop_off_type")
    private Integer dropOffType;

    @Column(name = "continuous_pickup")
    private Integer continuousPickup;

    @Column(name = "continuous_drop_off")
    private Integer continuousDropOff;

    @Column(name = "shape_dist_traveled")
    private Double shapeDistTraveled;

    @Column(name = "fare_units_traveled")
    private Double fareUnitsTraveled;

    @Column(name = "timepoint")
    private Integer timepoint;

    public StopTime() {
    }

    public StopTime(String tripId, Duration arrivalTime, Duration departureTime, String stopId, Integer stopSequence, String stopHeadsign, Integer pickupType, Integer dropOffType, Integer continuousPickup, Integer continuousDropOff, Double shapeDistTraveled, Double fareUnitsTraveled, Integer timepoint) {
        this.tripId = tripId;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.stopId = stopId;
        this.stopSequence = stopSequence;
        this.stopHeadsign = stopHeadsign;
        this.pickupType = pickupType;
        this.dropOffType = dropOffType;
        this.continuousPickup = continuousPickup;
        this.continuousDropOff = continuousDropOff;
        this.shapeDistTraveled = shapeDistTraveled;
        this.fareUnitsTraveled = fareUnitsTraveled;
        this.timepoint = timepoint;
    }
}
