package com.maestronic.gtfs.agency;

import javax.persistence.*;

@Entity
@Table(name = Agency.TABLE_NAME)
public class Agency {

    public static final String TABLE_NAME= "agency";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "agency_id")
    private String agencyId;

    @Column(name = "agency_name")
    private String agencyName;

    @Column(name = "agency_url")
    private String agencyUrl;

    @Column(name = "agency_timezone")
    private String agencyTimezone;

    @Column(name = "agency_lang")
    private String agencyLang;

    @Column(name = "agency_phone")
    private String agencyPhone;

    @Column(name = "agency_fare_url")
    private String agencyFareUrl;

    @Column(name = "agency_email")
    private String agencyEmail;

    public Agency() {
    }

    public Agency(String agencyId, String agencyName, String agencyUrl, String agencyTimezone, String agencyLang, String agencyPhone, String agencyFareUrl, String agencyEmail) {
        this.agencyId = agencyId;
        this.agencyName = agencyName;
        this.agencyUrl = agencyUrl;
        this.agencyTimezone = agencyTimezone;
        this.agencyLang = agencyLang;
        this.agencyPhone = agencyPhone;
        this.agencyFareUrl = agencyFareUrl;
        this.agencyEmail = agencyEmail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
