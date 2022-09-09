package com.maestronic.gtfs.validation;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class VehicleMonitorDummyValidation {

    @NotNull
    private String agency_id;
    @NotNull
    private String date;
    @NotNull
    private String start_time;
    @NotNull
    private String end_time;
    @Min(1)
    private Integer limit;
}
