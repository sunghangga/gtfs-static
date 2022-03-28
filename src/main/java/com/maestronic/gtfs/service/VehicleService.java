package com.maestronic.gtfs.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.maestronic.gtfs.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import java.util.List;
import java.util.Map;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Map<String, Object>> getVehiclePositions(String agencyId, String vehicleId, String tripId) {
        return vehicleRepository.getVehiclePositions(agencyId, vehicleId, tripId);
    }
}
