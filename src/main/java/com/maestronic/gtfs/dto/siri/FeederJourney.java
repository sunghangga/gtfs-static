package com.maestronic.gtfs.dto.siri;

import com.maestronic.gtfs.util.Adapter1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.ZonedDateTime;

@XmlAccessorType(XmlAccessType.FIELD)
public class FeederJourney implements Serializable {

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
    @XmlElement(name = "TransportMode")
    private String transportMode;
    @XmlElement(name = "DisabledAccessible")
    private String DisabledAccessible;
    @XmlElement(name = "VisuallyAccessible")
    private String VisuallyAccessible;
    @XmlElement(name = "AimedDepartureTime", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1.class)
    @XmlSchemaType(name = "dateTime")
    private ZonedDateTime aimedDepartureTime;

    public FeederJourney(String routeRef, String routeName, String directionRef, String tripRef, String tripName, String operatorRef, String originRef, String originNames, String destinationRef, String destinationNames, String transportMode, String disabledAccessible, String visuallyAccessible, ZonedDateTime aimedDepartureTime) {
        this.routeRef = routeRef;
        this.routeName = routeName;
        this.directionRef = directionRef;
        this.tripRef = tripRef;
        this.tripName = tripName;
        this.operatorRef = operatorRef;
        this.originRef = originRef;
        this.originNames = originNames;
        this.destinationRef = destinationRef;
        this.destinationNames = destinationNames;
        this.transportMode = transportMode;
        this.DisabledAccessible = disabledAccessible;
        this.VisuallyAccessible = visuallyAccessible;
        this.aimedDepartureTime = aimedDepartureTime;
    }
}
