package com.maestronic.gtfs.entity;

import com.maestronic.gtfs.compositeid.PatternIdCompositeId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@IdClass(PatternIdCompositeId.class)
@Table(name = PatternId.TABLE_NAME)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PatternId {

    public static final String TABLE_NAME= "pattern_id";

    @Id
    @Column(name = "trip_id")
    private String tripId;

    @Id
    @Column(name = "pattern_id")
    private String patternId;

    @Column(name = "pattern_name")
    private String patternName;
}
