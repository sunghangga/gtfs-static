package com.maestronic.gtfs.entity;

import javax.persistence.*;

@Entity
@Table(name = Dataowner.TABLE_NAME)
public class Dataowner {

    public static final String TABLE_NAME= "dataowner";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "daowcode")
    private String daowcode;

    @Column(name = "daowname")
    private String daowname;

    @Column(name = "daowtype")
    private String daowtype;

    public Dataowner() {
    }

    public Dataowner(String daowcode, String daowname, String daowtype) {
        this.daowcode = daowcode;
        this.daowname = daowname;
        this.daowtype = daowtype;
    }
}
