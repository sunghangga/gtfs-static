package com.maestronic.gtfs.service;

import com.maestronic.gtfs.entity.Vehicle;
import com.maestronic.gtfs.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private TimeService timeService;

    public List<Map<String, Object>> getVehiclePositions(String agencyId, String vehicleId, String tripId, long approx) {
        long timestamp = approx == 0 ? 0 : timeService.currentTimeToUnix() + approx;
        return vehicleRepository.getVehiclePositions(agencyId, vehicleId, tripId, timestamp);
    }

    public List<Vehicle> getVehicles() {
        return vehicleRepository.getVehicles();
    }
}
