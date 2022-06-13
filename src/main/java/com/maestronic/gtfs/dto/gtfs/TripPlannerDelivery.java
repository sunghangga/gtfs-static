package com.maestronic.gtfs.dto.gtfs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripPlannerDelivery {

    private String routeRef;
    private String routeName;
    private String directionRef;
    private String tripRef;
    private String tripName;
    private String tripScheduleRelationship;
    private String operatorRef;
    private String originRef;
    private String originNames;
    private Location originLocation;
    private String destinationRef;
    private String destinationNames;
    private Location destinationLocation;
    private List<TripDetail> tripDetails;
}
