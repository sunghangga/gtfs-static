package com.maestronic.gtfs.entity;

import javax.persistence.*;

@Entity
@Table(name = Place.TABLE_NAME)
public class Place {

    public static final String TABLE_NAME= "places";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "daowcode")
    private String daowcode;

    @Column(name = "placecode")
    private String placecode;

    @Column(name = "publicname")
    private String publicname;

    @Column(name = "town")
    private String town;

    public Place() {
    }

    public Place(String daowcode, String placecode, String publicname, String town) {
        this.daowcode = daowcode;
        this.placecode = placecode;
        this.publicname = publicname;
        this.town = town;
    }
}
