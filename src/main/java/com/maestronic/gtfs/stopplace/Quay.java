package com.maestronic.gtfs.stopplace;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = Quay.TABLE_NAME)
public class Quay {

    public static final String TABLE_NAME= "quays";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stopplace_id", referencedColumnName="id", nullable = false)
    private Stopplace stopplace;

    @Column(name = "quaycode")
    private String quaycode;
    @Column(name = "quaytype")
    private String quaytype;
    @Column(name = "transportmode")
    private String transportmode;
    @Column(name = "quaystatus")
    private String quaystatus;
    @Column(name = "rd_x")
    private Integer rd_x;
    @Column(name = "rd_y")
    private Integer rd_y;
    @Column(name = "town")
    private String town;
    @Column(name = "level")
    private String level;
    @Column(name = "street")
    private String street;
    @Column(name = "location")
    private String location;
    @Column(name = "compassdirection")
    private Integer compassdirection;
    @Column(name = "visuallyaccessible")
    private String visuallyaccessible;
    @Column(name = "disabledaccessible")
    private String disabledaccessible;
    @Column(name = "onlygetout")
    private Boolean onlygetout;
    @Column(name = "municipalitycode")
    private String municipalitycode;
    @Column(name = "quayownercode")
    private String quayownercode;
    @Column(name = "concessionprovidercode")
    private String concessionprovidercode;
    @Column(name = "quayname")
    private String quayname;
    @Column(name = "stopsidecode")
    private String stopsidecode;
    @Column(name = "quayshapetype")
    private String quayshapetype;
    @Column(name = "baylength")
    private BigDecimal baylength;
    @Column(name = "markedkerb")
    private Boolean markedkerb;
    @Column(name = "lift")
    private Boolean lift;
    @Column(name = "guidelines")
    private Boolean guidelines;
    @Column(name = "groundsurfaceindicator")
    private Boolean groundsurfaceindicator;
    @Column(name = "stopplaceaccessroute")
    private Boolean stopplaceaccessroute;
    @Column(name = "embaymentwidth")
    private BigDecimal embaymentwidth;
    @Column(name = "bayentranceangles")
    private BigDecimal bayentranceangles;
    @Column(name = "bayexitangles")
    private BigDecimal bayexitangles;
    @Column(name = "kerbheight")
    private BigDecimal kerbheight;
    @Column(name = "boardingpositionwidth")
    private BigDecimal boardingpositionwidth;
    @Column(name = "alightingpositionwidth")
    private BigDecimal alightingpositionwidth;
    @Column(name = "liftedpartlength")
    private BigDecimal liftedpartlength;
    @Column(name = "narrowestpassagewidth")
    private BigDecimal narrowestpassagewidth;
    @Column(name = "fulllengthguideline")
    private Boolean fulllengthguideline;
    @Column(name = "guidelinestopplaceconnection")
    private Boolean guidelinestopplaceconnection;
    @Column(name = "tactilegroundsurfaceindicator")
    private Boolean tactilegroundsurfaceindicator;
    @Column(name = "ramp")
    private Boolean ramp;
    @Column(name = "ramplength")
    private BigDecimal ramplength;
    @Column(name = "heightwithenvironment")
    private BigDecimal heightwithenvironment;
    @Column(name = "rampwidth")
    private BigDecimal rampwidth;
    @Column(name = "stopsign")
    private Boolean stopsign;
    @Column(name = "audiobutton")
    private Boolean audiobutton;
    @Column(name = "stopsigntype")
    private String stopsigntype;
    @Column(name = "shelter")
    private Boolean shelter;
    @Column(name = "shelterpublicity")
    private Boolean shelterpublicity;
    @Column(name = "illuminatedstop")
    private Boolean illuminatedstop;
    @Column(name = "seatavailable")
    private Boolean seatavailable;
    @Column(name = "leantosupport")
    private Boolean leantosupport;
    @Column(name = "timetableinformation")
    private Boolean timetableinformation;
    @Column(name = "infounit")
    private Boolean infounit;
    @Column(name = "routenetworkmap")
    private Boolean routenetworkmap;
    @Column(name = "passengerinformationdisplay")
    private Boolean passengerinformationdisplay;
    @Column(name = "passengerinformationdisplaytype")
    private String passengerinformationdisplaytype;
    @Column(name = "bicycleparking")
    private Boolean bicycleparking;
    @Column(name = "numberofbicycleplaces")
    private Integer numberofbicycleplaces;
    @Column(name = "bins")
    private Boolean bins;
    @Column(name = "ovccico")
    private Boolean ovccico;
    @Column(name = "ovccharging")
    private Boolean ovccharging;
    @Column(name = "remarks")
    private String remarks;
    @Column(name = "roadcode")
    private String roadcode;
    @Column(name = "hectometersign")
    private String hectometersign;
    @Column(name = "greenstop")
    private Boolean greenstop;
    @Column(name = "liftedbicyclepath")
    private Boolean liftedbicyclepath;

    public Quay() {
    }

    public void setStopplace(Stopplace stopplace) {
        this.stopplace = stopplace;
    }

    public void setQuaycode(String quaycode) {
        this.quaycode = quaycode;
    }

    public void setQuaytype(String quaytype) {
        this.quaytype = quaytype;
    }

    public void setTransportmode(String transportmode) {
        this.transportmode = transportmode;
    }

    public void setQuaystatus(String quaystatus) {
        this.quaystatus = quaystatus;
    }

    public void setRd_x(Integer rd_x) {
        this.rd_x = rd_x;
    }

    public void setRd_y(Integer rd_y) {
        this.rd_y = rd_y;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCompassdirection(Integer compassdirection) {
        this.compassdirection = compassdirection;
    }

    public void setVisuallyaccessible(String visuallyaccessible) {
        this.visuallyaccessible = visuallyaccessible;
    }

    public void setDisabledaccessible(String disabledaccessible) {
        this.disabledaccessible = disabledaccessible;
    }

    public void setOnlygetout(Boolean onlygetout) {
        this.onlygetout = onlygetout;
    }

    public void setMunicipalitycode(String municipalitycode) {
        this.municipalitycode = municipalitycode;
    }

    public void setQuayownercode(String quayownercode) {
        this.quayownercode = quayownercode;
    }

    public void setConcessionprovidercode(String concessionprovidercode) {
        this.concessionprovidercode = concessionprovidercode;
    }

    public void setQuayname(String quayname) {
        this.quayname = quayname;
    }

    public void setStopsidecode(String stopsidecode) {
        this.stopsidecode = stopsidecode;
    }

    public void setQuayshapetype(String quayshapetype) {
        this.quayshapetype = quayshapetype;
    }

    public void setBaylength(BigDecimal baylength) {
        this.baylength = baylength;
    }

    public void setMarkedkerb(Boolean markedkerb) {
        this.markedkerb = markedkerb;
    }

    public void setLift(Boolean lift) {
        this.lift = lift;
    }

    public void setGuidelines(Boolean guidelines) {
        this.guidelines = guidelines;
    }

    public void setGroundsurfaceindicator(Boolean groundsurfaceindicator) {
        this.groundsurfaceindicator = groundsurfaceindicator;
    }

    public void setStopplaceaccessroute(Boolean stopplaceaccessroute) {
        this.stopplaceaccessroute = stopplaceaccessroute;
    }

    public void setEmbaymentwidth(BigDecimal embaymentwidth) {
        this.embaymentwidth = embaymentwidth;
    }

    public void setBayentranceangles(BigDecimal bayentranceangles) {
        this.bayentranceangles = bayentranceangles;
    }

    public void setBayexitangles(BigDecimal bayexitangles) {
        this.bayexitangles = bayexitangles;
    }

    public void setKerbheight(BigDecimal kerbheight) {
        this.kerbheight = kerbheight;
    }

    public void setBoardingpositionwidth(BigDecimal boardingpositionwidth) {
        this.boardingpositionwidth = boardingpositionwidth;
    }

    public void setAlightingpositionwidth(BigDecimal alightingpositionwidth) {
        this.alightingpositionwidth = alightingpositionwidth;
    }

    public void setLiftedpartlength(BigDecimal liftedpartlength) {
        this.liftedpartlength = liftedpartlength;
    }

    public void setNarrowestpassagewidth(BigDecimal narrowestpassagewidth) {
        this.narrowestpassagewidth = narrowestpassagewidth;
    }

    public void setFulllengthguideline(Boolean fulllengthguideline) {
        this.fulllengthguideline = fulllengthguideline;
    }

    public void setGuidelinestopplaceconnection(Boolean guidelinestopplaceconnection) {
        this.guidelinestopplaceconnection = guidelinestopplaceconnection;
    }

    public void setTactilegroundsurfaceindicator(Boolean tactilegroundsurfaceindicator) {
        this.tactilegroundsurfaceindicator = tactilegroundsurfaceindicator;
    }

    public void setRamp(Boolean ramp) {
        this.ramp = ramp;
    }

    public void setRamplength(BigDecimal ramplength) {
        this.ramplength = ramplength;
    }

    public void setHeightwithenvironment(BigDecimal heightwithenvironment) {
        this.heightwithenvironment = heightwithenvironment;
    }

    public void setRampwidth(BigDecimal rampwidth) {
        this.rampwidth = rampwidth;
    }

    public void setStopsign(Boolean stopsign) {
        this.stopsign = stopsign;
    }

    public void setAudiobutton(Boolean audiobutton) {
        this.audiobutton = audiobutton;
    }

    public void setStopsigntype(String stopsigntype) {
        this.stopsigntype = stopsigntype;
    }

    public void setShelter(Boolean shelter) {
        this.shelter = shelter;
    }

    public void setShelterpublicity(Boolean shelterpublicity) {
        this.shelterpublicity = shelterpublicity;
    }

    public void setIlluminatedstop(Boolean illuminatedstop) {
        this.illuminatedstop = illuminatedstop;
    }

    public void setSeatavailable(Boolean seatavailable) {
        this.seatavailable = seatavailable;
    }

    public void setLeantosupport(Boolean leantosupport) {
        this.leantosupport = leantosupport;
    }

    public void setTimetableinformation(Boolean timetableinformation) {
        this.timetableinformation = timetableinformation;
    }

    public void setInfounit(Boolean infounit) {
        this.infounit = infounit;
    }

    public void setRoutenetworkmap(Boolean routenetworkmap) {
        this.routenetworkmap = routenetworkmap;
    }

    public void setPassengerinformationdisplay(Boolean passengerinformationdisplay) {
        this.passengerinformationdisplay = passengerinformationdisplay;
    }

    public void setPassengerinformationdisplaytype(String passengerinformationdisplaytype) {
        this.passengerinformationdisplaytype = passengerinformationdisplaytype;
    }

    public void setBicycleparking(Boolean bicycleparking) {
        this.bicycleparking = bicycleparking;
    }

    public void setNumberofbicycleplaces(Integer numberofbicycleplaces) {
        this.numberofbicycleplaces = numberofbicycleplaces;
    }

    public void setBins(Boolean bins) {
        this.bins = bins;
    }

    public void setOvccico(Boolean ovccico) {
        this.ovccico = ovccico;
    }

    public void setOvccharging(Boolean ovccharging) {
        this.ovccharging = ovccharging;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setRoadcode(String roadcode) {
        this.roadcode = roadcode;
    }

    public void setHectometersign(String hectometersign) {
        this.hectometersign = hectometersign;
    }

    public void setGreenstop(Boolean greenstop) {
        this.greenstop = greenstop;
    }

    public void setLiftedbicyclepath(Boolean liftedbicyclepath) {
        this.liftedbicyclepath = liftedbicyclepath;
    }
}
