package com.maestronic.gtfs.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = PassengerStopAssignment.TABLE_NAME)
public class PassengerStopAssignment {

    public static final String TABLE_NAME= "passengerstopassignment";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "quaycode")
    private String quaycode;

    @Column(name = "dataownercode")
    private String dataownercode;

    @Column(name = "userstopcode")
    private String userstopcode;

    @Column(name = "validfrom")
    private LocalDateTime validfrom;

    @Column(name = "validthru")
    private LocalDateTime validthru;

    public PassengerStopAssignment() {
    }

    public PassengerStopAssignment(String quaycode, String dataownercode, String userstopcode, LocalDateTime validfrom, LocalDateTime validthru) {
        this.quaycode = quaycode;
        this.dataownercode = dataownercode;
        this.userstopcode = userstopcode;
        this.validfrom = validfrom;
        this.validthru = validthru;
    }
}
