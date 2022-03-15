package com.maestronic.gtfs.entity;

import com.maestronic.gtfs.compositeid.ShapeCompositeId;

import javax.persistence.*;

@Entity
@IdClass(ShapeCompositeId.class)
@Table(name = Shape.TABLE_NAME)
public class Shape {

    public static final String TABLE_NAME = "shapes";

    @Id
    @Column(name = "shape_id")
    private String shapeId;

    @Column(name = "shape_pt_lat")
    private double shapesPtLat;

    @Column(name = "shape_pt_lon")
    private double shapePtLon;

    @Id
    @Column(name = "shape_pt_sequence")
    private Integer shapePtSequence;

    @Column(name = "shape_dist_traveled")
    private double shapeDistTraveled;

    public Shape() {
    }

    public Shape(String shapeId, double shapesPtLat, double shapePtLon, Integer shapePtSequence, double shapeDistTraveled) {
        this.shapeId = shapeId;
        this.shapesPtLat = shapesPtLat;
        this.shapePtLon = shapePtLon;
        this.shapePtSequence = shapePtSequence;
        this.shapeDistTraveled = shapeDistTraveled;
    }
}
