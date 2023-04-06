package com.maestronic.gtfs.service;

import com.maestronic.gtfs.repository.AlertRepository;
import com.maestronic.gtfs.repository.JourneyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;

    public List<Map<String, Object>> getServiceAlert(String routeId,
                                                     String tripId,
                                                     String stopId,
                                                     String agencyId,
                                                     String routeType,
                                                     String cause,
                                                     String effect) {
        return alertRepository.getServiceAlert(
                routeId,
                tripId,
                stopId,
                agencyId,
                routeType,
                cause,
                effect);
    }
}
