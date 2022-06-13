package com.maestronic.gtfs.entity;

import com.maestronic.gtfs.compositeid.NearestStopCompositeId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.time.Duration;

@Entity
@IdClass(NearestStopCompositeId.class)
public class NearestStop {
    @Id
    @Column(name = "trip_id")
    private String tripId;
    @Id
    @Column(name = "stop_id")
    private String stopId;

    @Column(name = "stop_code")
    private String stopCode;
    @Column(name = "stop_name")
    private String stopName;
    @Column(name = "distance")
    private double distance;
    @Column(name = "stop_sequence")
    private int stopSequence;
    @Column(name = "direction_id")
    private int directionId;
    @Column(name = "arrival_time", columnDefinition = "interval")
    private Duration arrivalTime;
    @Column(name = "departure_time", columnDefinition = "interval")
    private Duration departureTime;
}
