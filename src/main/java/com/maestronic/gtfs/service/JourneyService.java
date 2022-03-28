package com.maestronic.gtfs.service;

import com.maestronic.gtfs.repository.JourneyRepository;
import com.maestronic.gtfs.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class JourneyService {

    @Autowired
    private JourneyRepository journeyRepository;

    public List<Map<String, Object>> getShapeJourneyWithStop(String agencyId, String vehicleId) {
        return journeyRepository.getShapeJourneyWithStop(agencyId, vehicleId);
    }
}
