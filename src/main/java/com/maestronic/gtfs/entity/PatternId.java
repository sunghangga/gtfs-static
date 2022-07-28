package com.maestronic.gtfs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = PatternId.TABLE_NAME)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PatternId {

    public static final String TABLE_NAME= "pattern_id";

    @Id
    @Column(name = "trip_id")
    private String tripId;

    @Column(name = "pattern_id")
    private String patternId;

    @Column(name = "pattern_name")
    private String patternName;
}
