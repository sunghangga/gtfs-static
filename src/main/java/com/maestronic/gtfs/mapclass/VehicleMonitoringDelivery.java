package com.maestronic.gtfs.mapclass;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class VehicleMonitoringDelivery implements Serializable {

    @XmlElement(name = "VehicleActivity")
    private List<VehicleActivity> vehicleActivities;

    public List<VehicleActivity> getVehicleActivities() {
        if (vehicleActivities == null) {
            vehicleActivities = new ArrayList<VehicleActivity>();
        }
        return this.vehicleActivities;
    }
}
