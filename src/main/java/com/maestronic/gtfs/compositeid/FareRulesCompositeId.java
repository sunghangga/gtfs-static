package com.maestronic.gtfs.compositeid;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FareRulesCompositeId implements Serializable {

    private String fareId;
    private String routeId;
    private String originId;
    private String destinationId;
    private String containsId;
}
