package com.maestronic.gtfs.entity;

import com.maestronic.gtfs.compositeid.FareRulesCompositeId;

import javax.persistence.*;

@Entity
@IdClass(FareRulesCompositeId.class)
@Table(name = FareRules.TABLE_NAME)
public class FareRules {

    public static final String TABLE_NAME= "fare_rules";

    @Id
    @Column(name = "fare_id")
    private String fareId;

    @Id
    @Column(name = "route_id")
    private String routeId;

    @Id
    @Column(name = "origin_id")
    private String originId;

    @Id
    @Column(name = "destination_id")
    private String destinationId;

    @Id
    @Column(name = "contains_id")
    private String containsId;

    public FareRules() {
    }

    public FareRules(String fareId, String routeId, String originId, String destinationId, String containsId) {
        this.fareId = fareId;
        this.routeId = routeId;
        this.originId = originId;
        this.destinationId = destinationId;
        this.containsId = containsId;
    }
}
