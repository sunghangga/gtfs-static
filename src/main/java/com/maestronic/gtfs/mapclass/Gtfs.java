package com.maestronic.gtfs.mapclass;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Gtfs")
public class Gtfs implements Serializable {

    @XmlAttribute(name = "version")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String version;
    @XmlElement(name = "ServiceDelivery")
    private ServiceDelivery serviceDelivery;

    public String getVersion() {
        if (version == null) {
            return "1.0";
        } else {
            return version;
        }
    }

    public void setVersion(String value) {
        this.version = value;
    }

    public ServiceDelivery getServiceDelivery() {
        return serviceDelivery;
    }

    public void setServiceDelivery(ServiceDelivery serviceDelivery) {
        this.serviceDelivery = serviceDelivery;
    }
}
