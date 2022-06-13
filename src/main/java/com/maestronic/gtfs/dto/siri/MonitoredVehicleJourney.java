package com.maestronic.gtfs.dto.siri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
public class MonitoredVehicleJourney implements Serializable {

    @XmlElement(name = "RouteRef")
    private String routeRef;
    @XmlElement(name = "RouteName")
    private String routeName;
    @XmlElement(name = "DirectionRef")
    private String directionRef;
    @XmlElement(name = "TripRef")
    private String tripRef;
    @XmlElement(name = "TripHeadSign")
    private String tripName;
    @XmlElement(name = "TripScheduleRelationship", nillable = true)
    private String tripScheduleRelationship;
    @XmlElement(name = "OperatorRef")
    private String operatorRef;
    @XmlElement(name = "OriginRef")
    private String originRef;
    @XmlElement(name = "OriginName")
    private String originNames;
    @XmlElement(name = "DestinationRef")
    private String destinationRef;
    @XmlElement(name = "DestinationName")
    private String destinationNames;
    @XmlElement(name = "VehicleLocation")
    private Location vehicleLocation;
    @XmlElement(name = "VehicleRef")
    private String vehicleRef;
    @XmlElement(name = "MonitoredCall")
    private MonitoredCall monitoredCall;
    @XmlElement(name = "OnwardCalls")
    private OnwardCalls onwardCalls;

    public MonitoredVehicleJourney(String routeRef, String routeName, String directionRef, String tripRef, String tripName, String tripScheduleRelationship, String operatorRef, String originRef, String originNames, String destinationRef, String destinationNames, Location vehicleLocation, String vehicleRef, MonitoredCall monitoredCall, OnwardCalls onwardCalls) {
        this.routeRef = routeRef;
        this.routeName = routeName;
        this.directionRef = directionRef;
        this.tripRef = tripRef;
        this.tripName = tripName;
        this.tripScheduleRelationship = tripScheduleRelationship;
        this.operatorRef = operatorRef;
        this.originRef = originRef;
        this.originNames = originNames;
        this.destinationRef = destinationRef;
        this.destinationNames = destinationNames;
        this.vehicleLocation = vehicleLocation;
        this.vehicleRef = vehicleRef;
        this.monitoredCall = monitoredCall;
        this.onwardCalls = onwardCalls;
    }
}
