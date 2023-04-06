package com.maestronic.gtfs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = SignupPeriods.TABLE_NAME)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SignupPeriods {

    public static final String TABLE_NAME= "signup_periods";

    @Id
    @Column(name = "sign_id")
    private String signId;

    @Column(name = "from_date")
    private Integer routeName;

    @Column(name = "to_date")
    private Integer directionName;
}
