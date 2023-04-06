package com.maestronic.gtfs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JourneyParam {

    private Integer key;
    private Integer prev_key;
    private String trip_id;
    private String stop_id;
    private int stop_sequence;
    private Duration arrival_time;
    private Duration departure_time;
}
