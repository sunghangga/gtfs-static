package com.maestronic.gtfs.mapclass;

import com.maestronic.gtfs.util.Adapter1;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class MonitoredCall implements Serializable {

    @XmlElement(name = "StopPointRef")
    private String stopPointRef;
    @XmlElement(name = "StopPointName")
    private String stopPointNames;
    @XmlElement(name = "StopSequence")
    private int stopSequence;
    @XmlElement(name = "VehicleStopStatus")
    private String vehicleStopStatus;
    @XmlElement(name = "VehicleLocationAtStop")
    private Location vehicleLocationAtStop;
    @XmlElement(name = "WheelchairBoarding")
    private Integer wheelchairBoarding;
    @XmlElement(name = "StopScheduleRelationship", nillable = true)
    private String stopScheduleRelationship;
    @XmlElement(name = "AimedArrivalTime", type = String.class)
    @XmlJavaTypeAdapter(Adapter1.class)
    @XmlSchemaType(name = "dateTime")
    private ZonedDateTime aimedArrivalTime;
    @XmlElement(name = "ExpectedArrivalTime", nillable = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1.class)
    @XmlSchemaType(name = "dateTime")
    private ZonedDateTime expectedArrivalTime;
    @XmlElement(name = "ArrivalDelay")
    private int arrivalDelay;
    @XmlElement(name = "AimedDepartureTime", type = String.class)
    @XmlJavaTypeAdapter(Adapter1.class)
    @XmlSchemaType(name = "dateTime")
    private ZonedDateTime aimedDepartureTime;
    @XmlElement(name = "ExpectedDepartureTime", nillable = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1.class)
    @XmlSchemaType(name = "dateTime")
    private ZonedDateTime expectedDepartureTime;
    @XmlElement(name = "DepartureDelay")
    private int departureDelay;
    @XmlElement(name = "ConnectionRoute")
    private List<ConnectionRoutes> connectionRoute;

    public MonitoredCall(String stopPointRef, String stopPointNames, int stopSequence, String vehicleStopStatus, Location vehicleLocationAtStop, Integer wheelchairBoarding, String stopScheduleRelationship, ZonedDateTime aimedArrivalTime, ZonedDateTime expectedArrivalTime, int arrivalDelay, ZonedDateTime aimedDepartureTime, ZonedDateTime expectedDepartureTime, int departureDelay) {
        this.stopPointRef = stopPointRef;
        this.stopPointNames = stopPointNames;
        this.stopSequence = stopSequence;
        this.vehicleStopStatus = vehicleStopStatus;
        this.vehicleLocationAtStop = vehicleLocationAtStop;
        this.wheelchairBoarding = wheelchairBoarding;
        this.stopScheduleRelationship = stopScheduleRelationship;
        this.aimedArrivalTime = aimedArrivalTime;
        this.expectedArrivalTime = expectedArrivalTime;
        this.arrivalDelay = arrivalDelay;
        this.aimedDepartureTime = aimedDepartureTime;
        this.expectedDepartureTime = expectedDepartureTime;
        this.departureDelay = departureDelay;
    }

    public MonitoredCall(String stopPointRef, String stopPointNames, int stopSequence, String vehicleStopStatus, Location vehicleLocationAtStop, Integer wheelchairBoarding, String stopScheduleRelationship, ZonedDateTime aimedArrivalTime, ZonedDateTime expectedArrivalTime, int arrivalDelay, ZonedDateTime aimedDepartureTime, ZonedDateTime expectedDepartureTime, int departureDelay, List<ConnectionRoutes> connectionRoute) {
        this.stopPointRef = stopPointRef;
        this.stopPointNames = stopPointNames;
        this.stopSequence = stopSequence;
        this.vehicleStopStatus = vehicleStopStatus;
        this.vehicleLocationAtStop = vehicleLocationAtStop;
        this.wheelchairBoarding = wheelchairBoarding;
        this.stopScheduleRelationship = stopScheduleRelationship;
        this.aimedArrivalTime = aimedArrivalTime;
        this.expectedArrivalTime = expectedArrivalTime;
        this.arrivalDelay = arrivalDelay;
        this.aimedDepartureTime = aimedDepartureTime;
        this.expectedDepartureTime = expectedDepartureTime;
        this.departureDelay = departureDelay;
        this.connectionRoute = connectionRoute;
    }
}
