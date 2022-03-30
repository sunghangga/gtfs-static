package com.maestronic.gtfs.mapclass;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
public class ConnectionRoutes implements Serializable {

    @XmlElement(name = "RouteRef")
    private String routeRef;
    @XmlElement(name = "RouteShortName")
    private String routeShortName;
    @XmlElement(name = "RouteName")
    private String routeName;
    @XmlElement(name = "RouteType")
    private String routeType;

    public ConnectionRoutes(String routeRef, String routeShortName, String routeName, String routeType) {
        this.routeRef = routeRef;
        this.routeShortName = routeShortName;
        this.routeName = routeName;
        this.routeType = routeType;
    }
}
