package com.maestronic.gtfs.dto.gtfs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class Gtfs {

    private String version;
    private ZonedDateTime responseTimestamp;
    private ServiceDelivery serviceDeliveries;

    public String getVersion() {
        if (version == null) {
            return "1.0";
        } else {
            return version;
        }
    }
}
