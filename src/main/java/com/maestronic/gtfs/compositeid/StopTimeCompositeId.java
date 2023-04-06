package com.maestronic.gtfs.compositeid;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StopTimeCompositeId implements Serializable {

    private String tripId;
    private Integer stopSequence;
}
