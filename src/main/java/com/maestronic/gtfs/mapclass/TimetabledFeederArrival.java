package com.maestronic.gtfs.mapclass;

import com.maestronic.gtfs.util.Adapter1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.ZonedDateTime;

@XmlAccessorType(XmlAccessType.FIELD)
public class TimetabledFeederArrival implements Serializable {

    @XmlElement(name = "StopPointRef")
    private String stopPointRef;
    @XmlElement(name = "StopPointName")
    private String stopPointName;
    @XmlElement(name = "AimedArrivalTime", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1.class)
    @XmlSchemaType(name = "dateTime")
    private ZonedDateTime aimedArrivalTime;
    @XmlElement(name = "FeederJourney", required = true)
    private FeederJourney feederJourney;

    public TimetabledFeederArrival(String stopPointRef, String stopPointName, ZonedDateTime aimedArrivalTime, FeederJourney feederJourney) {
        this.stopPointRef = stopPointRef;
        this.stopPointName = stopPointName;
        this.aimedArrivalTime = aimedArrivalTime;
        this.feederJourney = feederJourney;
    }
}