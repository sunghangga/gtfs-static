package com.maestronic.gtfs.entity;

import com.maestronic.gtfs.compositeid.TransferCompositeId;

import javax.persistence.*;

@Entity
@IdClass(TransferCompositeId.class)
@Table(name = Transfer.TABLE_NAME)
public class Transfer {

    public static final String TABLE_NAME = "transfers";

    @Id
    @Column(name = "from_stop_id")
    private String fromStopId;

    @Id
    @Column(name = "to_stop_id")
    private String toStopId;

    @Column(name = "from_route_id")
    private String fromRouteId;

    @Column(name = "to_route_id")
    private String toRouteId;

    @Id
    @Column(name = "from_trip_id")
    private String fromTripId;

    @Id
    @Column(name = "to_trip_id")
    private String toTripId;

    @Column(name = "transfer_type")
    private Integer transferType;

    @Column(name = "min_transfer_time")
    private Integer minTransferTime;

    public Transfer() {
    }

    public Transfer(String fromStopId, String toStopId, String fromRouteId, String toRouteId, String fromTripId, String toTripId, Integer transferType, Integer minTransferTime) {
        this.fromStopId = fromStopId;
        this.toStopId = toStopId;
        this.fromRouteId = fromRouteId;
        this.toRouteId = toRouteId;
        this.fromTripId = fromTripId;
        this.toTripId = toTripId;
        this.transferType = transferType;
        this.minTransferTime = minTransferTime;
    }
}
