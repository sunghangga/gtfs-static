package com.maestronic.gtfs.service;

import com.maestronic.gtfs.repository.FareRepository;
import com.maestronic.gtfs.util.GlobalVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FareService implements GlobalVariable {

    @Autowired
    private FareRepository fareRepository;

    public List<Map<String, Object>> getTripFare(String agencyId,
                                                 String routeId,
                                                 String originId,
                                                 String destinationId,
                                                 String containsId){
        List<Map<String, Object>> resultList = fareRepository
                .findFareForTrip(agencyId,
                        routeId,
                        originId,
                        destinationId,
                        containsId);

        if (resultList == null || resultList.size() <= 0) {
            return null;
        }

        return resultList;
    }
}
