package com.maestronic.gtfs.dto.gtfs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Location {

    private Double longitude;
    private Double latitude;
}
