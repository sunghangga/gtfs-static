package com.maestronic.gtfs.dto.gtfs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripPlannerDelivery {

    private double totalFare;
    private String currencyType;
    private List<TripPlanners> tripPlanners;
}
