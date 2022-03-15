package com.maestronic.gtfs.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = Stopplace.TABLE_NAME)
public class Stopplace {

    public static final String TABLE_NAME= "stopplaces";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "placecode")
    private String placecode;
    @Column(name = "stopplacecode")
    private String stopplacecode;
    @Column(name = "stopplacetype")
    private String stopplacetype;
    @Column(name = "publicname")
    private String publicname;
    @Column(name = "town")
    private String town;
    @Column(name = "street")
    private String street;
    @Column(name = "stopplacestatus")
    private String stopplacestatus;
    @Column(name = "uiccode")
    private Long uiccode;
    @Column(name = "level")
    private String level;
    @Column(name = "rd_x")
    private Integer rd_x;
    @Column(name = "rd_y")
    private Integer rd_y;
    @Column(name = "stopplaceownercode")
    private String stopplaceownercode;
    @Column(name = "timetableinformation")
    private Boolean timetableinformation;
    @Column(name = "passengerinformationdisplay")
    private Boolean passengerinformationdisplay;
    @Column(name = "passengerinformationdisplaytype")
    private String passengerinformationdisplaytype;
    @Column(name = "bicycleparking")
    private Boolean bicycleparking;
    @Column(name = "numberofbicycleplaces")
    private Integer numberofbicycleplaces;
    @Column(name = "toiletfacility")
    private Boolean toiletfacility;
    @Column(name = "ptbikerental")
    private Boolean ptbikerental;
    @Column(name = "bins")
    private Boolean bins;
    @Column(name = "ovccico")
    private Boolean ovccico;
    @Column(name = "ovccharging")
    private Boolean ovccharging;

    @OneToMany(
            mappedBy = "stopplace",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private List<Quay> quays = new ArrayList<>();

    public Stopplace() {
    }

    public Stopplace(String placecode, String stopplacecode, String stopplacetype, String publicname, String town, String street, String stopplacestatus, Long uiccode, String level, Integer rd_x, Integer rd_y, String stopplaceownercode, Boolean timetableinformation, Boolean passengerinformationdisplay, String passengerinformationdisplaytype, Boolean bicycleparking, Integer numberofbicycleplaces, Boolean toiletfacility, Boolean ptbikerental, Boolean bins, Boolean ovccico, Boolean ovccharging) {
        this.placecode = placecode;
        this.stopplacecode = stopplacecode;
        this.stopplacetype = stopplacetype;
        this.publicname = publicname;
        this.town = town;
        this.street = street;
        this.stopplacestatus = stopplacestatus;
        this.uiccode = uiccode;
        this.level = level;
        this.rd_x = rd_x;
        this.rd_y = rd_y;
        this.stopplaceownercode = stopplaceownercode;
        this.timetableinformation = timetableinformation;
        this.passengerinformationdisplay = passengerinformationdisplay;
        this.passengerinformationdisplaytype = passengerinformationdisplaytype;
        this.bicycleparking = bicycleparking;
        this.numberofbicycleplaces = numberofbicycleplaces;
        this.toiletfacility = toiletfacility;
        this.ptbikerental = ptbikerental;
        this.bins = bins;
        this.ovccico = ovccico;
        this.ovccharging = ovccharging;
    }

    public List<Quay> getQuays() {
        return quays;
    }
}
