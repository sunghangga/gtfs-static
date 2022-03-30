package com.maestronic.gtfs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = StopOrderExceptions.TABLE_NAME)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StopOrderExceptions {

    public static final String TABLE_NAME= "stop_order_exceptions";

    @Id
    @Column(name = "stop_id")
    private String stopId;

    @Column(name = "route_name")
    private String routeName;

    @Column(name = "direction_name")
    private String directionName;

    @Column(name = "direction_do")
    private int directionDo;

    @Column(name = "stop_name")
    private String stopName;

    @Column(name = "stop_do")
    private int stopDo;
}
