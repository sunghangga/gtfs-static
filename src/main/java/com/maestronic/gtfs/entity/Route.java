package com.maestronic.gtfs.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = Route.TABLE_NAME)
public class Route {

    public static final String TABLE_NAME= "routes";

    @Id
    @Column(name = "route_id")
    private String routeId;

    @Column(name = "agency_id")
    private String agencyId;

    @Column(name = "route_short_name")
    private String routeShortName;

    @Column(name = "route_long_name")
    private String routeLongName;

    @Column(name = "route_desc")
    private String routeDesc;

    @Column(name = "route_type")
    private Integer routeType;

    @Column(name = "route_url")
    private String routeUrl;

    @Column(name = "route_color")
    private String routeColor;

    @Column(name = "route_text_color")
    private String routeTextColor;

    @Column(name = "route_sort_order")
    private Integer routeSortOrder;

    @Column(name = "continuous_pickup")
    private Integer continuousPickup;

    @Column(name = "continuous_drop_off")
    private Integer continuousDropOff;

    public Route() {
    }

    public Route(String routeId, String agencyId, String routeShortName, String routeLongName, String routeDesc, Integer routeType, String routeUrl, String routeColor, String routeTextColor, Integer routeSortOrder, Integer continuousPickup, Integer continuousDropOff) {
        this.routeId = routeId;
        this.agencyId = agencyId;
        this.routeShortName = routeShortName;
        this.routeLongName = routeLongName;
        this.routeDesc = routeDesc;
        this.routeType = routeType;
        this.routeUrl = routeUrl;
        this.routeColor = routeColor;
        this.routeTextColor = routeTextColor;
        this.routeSortOrder = routeSortOrder;
        this.continuousPickup = continuousPickup;
        this.continuousDropOff = continuousDropOff;
    }
}
