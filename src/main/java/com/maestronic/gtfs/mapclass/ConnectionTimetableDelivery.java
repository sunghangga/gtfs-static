package com.maestronic.gtfs.mapclass;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class ConnectionTimetableDelivery implements Serializable {

    @XmlElement(name = "MonitoringRef")
    private String monitoringRef;
    @XmlElement(name = "TimetabledFeederArrival")
    private List<TimetabledFeederArrival> timetabledFeederArrivals;

    public List<TimetabledFeederArrival> getTimetabledFeederArrivals() {
        if (timetabledFeederArrivals == null) {
            timetabledFeederArrivals = new ArrayList<TimetabledFeederArrival>();
        }
        return this.timetabledFeederArrivals;
    }

    public void setMonitoringRef(String monitoringRef) {
        this.monitoringRef = monitoringRef;
    }
}
