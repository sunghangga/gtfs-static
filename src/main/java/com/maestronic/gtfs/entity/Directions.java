package com.maestronic.gtfs.entity;

import com.maestronic.gtfs.compositeid.DirectionsCompositeId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = Directions.TABLE_NAME)
@IdClass(DirectionsCompositeId.class)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Directions {

    public static final String TABLE_NAME= "directions";

    @Id
    @Column(name = "route_id")
    private String routeId;

    @Id
    @Column(name = "direction_id")
    private int directionId;

    @Column(name = "direction")
    private String direction;

    @Column(name = "route_short_name")
    private String routeShortName;
}
