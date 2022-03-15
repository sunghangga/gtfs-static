package com.maestronic.gtfs.mapclass;

import com.maestronic.gtfs.util.Adapter1;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.ZonedDateTime;

@XmlAccessorType(XmlAccessType.FIELD)
public class OnwardCall implements Serializable {

    @XmlElement(name = "StopPointRef")
    private String stopPointRef;
    @XmlElement(name = "StopPointName")
    private String stopPointNames;
    @XmlElement(name = "StopSequence")
    private int stopSequence;
    @XmlElement(name = "AimedArrivalTime", type = String.class)
    @XmlJavaTypeAdapter(Adapter1.class)
    @XmlSchemaType(name = "dateTime")
    private ZonedDateTime aimedArrivalTime;
    @XmlElement(name = "ExpectedArrivalTime", type = String.class)
    @XmlJavaTypeAdapter(Adapter1.class)
    @XmlSchemaType(name = "dateTime")
    private ZonedDateTime expectedArrivalTime;
    @XmlElement(name = "ArrivalDelay")
    private int arrivalDelay;
    @XmlElement(name = "AimedDepartureTime", type = String.class)
    @XmlJavaTypeAdapter(Adapter1.class)
    @XmlSchemaType(name = "dateTime")
    private ZonedDateTime aimedDepartureTime;
    @XmlElement(name = "ExpectedDepartureTime", type = String.class)
    @XmlJavaTypeAdapter(Adapter1.class)
    @XmlSchemaType(name = "dateTime")
    private ZonedDateTime expectedDepartureTime;
    @XmlElement(name = "DepartureDelay")
    private int departureDelay;

    public OnwardCall(String stopPointRef, String stopPointNames, int stopSequence, ZonedDateTime aimedArrivalTime, ZonedDateTime expectedArrivalTime, int arrivalDelay, ZonedDateTime aimedDepartureTime, ZonedDateTime expectedDepartureTime, int departureDelay) {
        this.stopPointRef = stopPointRef;
        this.stopPointNames = stopPointNames;
        this.stopSequence = stopSequence;
        this.aimedArrivalTime = aimedArrivalTime;
        this.expectedArrivalTime = expectedArrivalTime;
        this.arrivalDelay = arrivalDelay;
        this.aimedDepartureTime = aimedDepartureTime;
        this.expectedDepartureTime = expectedDepartureTime;
        this.departureDelay = departureDelay;
    }
}
