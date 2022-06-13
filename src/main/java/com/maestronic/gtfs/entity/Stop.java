package com.maestronic.gtfs.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = Stop.TABLE_NAME)
public class Stop {

    public static final String TABLE_NAME = "stops";

    @Id
    @Column(name = "stop_id")
    private String stopId;

    @Column(name = "stop_code")
    private String stopCode;

    @Column(name = "stop_name")
    private String stopName;

    @Column(name = "tts_stop_name")
    private String ttsStopName;

    @Column(name = "stop_desc")
    private String stopDesc;

    @Column(name = "stop_lat")
    private double stopLat;

    @Column(name = "stop_lon")
    private double stopLon;

    @Column(name = "zone_id")
    private String zoneId;

    @Column(name = "stop_url")
    private String stopUrl;

    @Column(name = "location_type")
    private Integer locationType;

    @Column(name = "parent_station")
    private String parentStation;

    @Column(name = "stop_timezone")
    private String stopTimezone;

    @Column(name = "wheelchair_boarding")
    private Integer wheelchairBoarding;

    @Column(name = "level_id")
    private String levelId;

    @Column(name = "platform_code")
    private String platformCode;

    public Stop() {
    }

    public Stop(String stopId, String stopCode, String stopName, String ttsStopName, String stopDesc, double stopLat, double stopLon, String zoneId, String stopUrl, Integer locationType, String parentStation, String stopTimezone, Integer wheelchairBoarding, String levelId, String platformCode) {
        this.stopId = stopId;
        this.stopCode = stopCode;
        this.stopName = stopName;
        this.ttsStopName = ttsStopName;
        this.stopDesc = stopDesc;
        this.stopLat = stopLat;
        this.stopLon = stopLon;
        this.zoneId = zoneId;
        this.stopUrl = stopUrl;
        this.locationType = locationType;
        this.parentStation = parentStation;
        this.stopTimezone = stopTimezone;
        this.wheelchairBoarding = wheelchairBoarding;
        this.levelId = levelId;
        this.platformCode = platformCode;
    }
}
