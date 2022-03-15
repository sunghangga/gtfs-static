package com.maestronic.gtfs.mapclass;

import com.maestronic.gtfs.util.Adapter1;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.ZonedDateTime;

@XmlAccessorType(XmlAccessType.FIELD)
public class VehicleActivity implements Serializable {

    @XmlElement(name = "RecordedAtTime", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1.class)
    @XmlSchemaType(name = "dateTime")
    private ZonedDateTime recordedAtTime;
    @XmlElement(name = "MonitoredVehicleJourney", required = true)
    private MonitoredVehicleJourney monitoredVehicleJourney;

    public VehicleActivity(ZonedDateTime recordedAtTime, MonitoredVehicleJourney monitoredVehicleJourney) {
        this.recordedAtTime = recordedAtTime;
        this.monitoredVehicleJourney = monitoredVehicleJourney;
    }
}