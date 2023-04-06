package com.maestronic.gtfs.compositeid;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TransferCompositeId implements Serializable {

    private String fromStopId;
    private String toStopId;
    private String fromTripId;
    private String toTripId;
}
