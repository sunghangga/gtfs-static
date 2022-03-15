package com.maestronic.gtfs.mapclass;

import com.maestronic.gtfs.util.Adapter1;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceDelivery implements Serializable {

    @XmlElement(name = "ResponseTimestamp", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1.class)
    @XmlSchemaType(name = "dateTime")
    private ZonedDateTime responseTimestamp;
    @XmlElement(name = "ProducerRef")
    private String producerRef;
    @XmlElement(name = "Status", defaultValue = "true")
    private boolean status;
    @XmlElement(name = "VehicleMonitoringDelivery")
    private List<VehicleMonitoringDelivery> vehicleMonitoringDeliveries;
    @XmlElement(name = "ConnectionTimetableDelivery")
    private List<ConnectionTimetableDelivery> connectionTimetableDelivery;

    public List<VehicleMonitoringDelivery> getVehicleMonitoringDeliveries() {
        if (vehicleMonitoringDeliveries == null) {
            vehicleMonitoringDeliveries = new ArrayList<VehicleMonitoringDelivery>();
        }
        return this.vehicleMonitoringDeliveries;
    }

    public List<ConnectionTimetableDelivery> getConnectionTimetableDeliveries() {
        if (connectionTimetableDelivery == null) {
            connectionTimetableDelivery = new ArrayList<ConnectionTimetableDelivery>();
        }
        return this.connectionTimetableDelivery;
    }

    public ZonedDateTime getResponseTimestamp() {
        return responseTimestamp;
    }

    public void setResponseTimestamp(ZonedDateTime responseTimestamp) {
        this.responseTimestamp = responseTimestamp;
    }

    public String getProducerRef() {
        return producerRef;
    }

    public void setProducerRef(String producerRef) {
        this.producerRef = producerRef;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
