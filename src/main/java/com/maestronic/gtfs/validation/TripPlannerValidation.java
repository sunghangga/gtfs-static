package com.maestronic.gtfs.validation;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TripPlannerValidation {

    @NotNull
    private Double ori_lat;
    @NotNull
    private Double ori_lon;
    @NotNull
    private String ori_name;
    @NotNull
    private Double des_lat;
    @NotNull
    private Double des_lon;
    @NotNull
    private String des_name;
    @NotNull
    private String date_time;
    @NotNull
    private String type_of_trip;
}
