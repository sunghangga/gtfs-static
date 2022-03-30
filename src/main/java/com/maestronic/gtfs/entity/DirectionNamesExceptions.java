package com.maestronic.gtfs.entity;

import com.maestronic.gtfs.compositeid.DirectionNamesExceptionsCompositeId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = DirectionNamesExceptions.TABLE_NAME)
@IdClass(DirectionNamesExceptionsCompositeId.class)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DirectionNamesExceptions {

    public static final String TABLE_NAME= "direction_names_exceptions";

    @Id
    @Column(name = "route_name")
    private String routeName;

    @Id
    @Column(name = "direction_id")
    private int directionId;

    @Column(name = "direction_name")
    private String directionName;

    @Column(name = "direction_do")
    private int directionDo;
}
