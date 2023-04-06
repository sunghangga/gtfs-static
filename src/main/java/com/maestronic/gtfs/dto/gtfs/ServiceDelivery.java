package com.maestronic.gtfs.dto.gtfs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDelivery {

    private List<TripPlannerDelivery> tripPlannerDeliveries;

    public List<TripPlannerDelivery> getTripPlannerDeliveries() {
        if (tripPlannerDeliveries == null) {
            tripPlannerDeliveries = new ArrayList<TripPlannerDelivery>();
        }
        return this.tripPlannerDeliveries;
    }
}
