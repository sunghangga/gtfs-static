package com.maestronic.gtfs.service;

import com.maestronic.gtfs.entity.NearestStop;
import com.maestronic.gtfs.dto.gtfs.*;
import com.maestronic.gtfs.entity.Stop;
import com.maestronic.gtfs.repository.JourneyRepository;
import com.maestronic.gtfs.util.GlobalHelper;
import com.maestronic.gtfs.util.GlobalVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Service
public class JourneyService {

    @Autowired
    private JourneyRepository journeyRepository;
    @Autowired
    private TimeService timeService;
    List<Stop> stops;
    ServiceDelivery serviceDelivery = new ServiceDelivery();

    public List<Map<String, Object>> getShapeJourneyWithStop(String agencyId, String vehicleId) {
        return journeyRepository.getShapeJourneyWithStop(agencyId, vehicleId);
    }

    private void markDepartureStop(Stop stop) {
        stops.add(stop);
    }

    private void markDepartureNeighbour(Stop stop) {
        stops.add(stop);
    }

    public Gtfs getTripPlannerWithFare(Double originLat,
                                                            Double originLong,
                                                            Double destinationLat,
                                                            Double destinationLong,
                                                            LocalDateTime dateTime,
                                                            String typeOfTrip) {
        // Check if origin and destination possible reach by walk
        long distance = (long) GlobalHelper.computeGreatCircleDistance(originLat, originLong, destinationLat, destinationLong);
        if (distance <= GlobalHelper.MAX_WALK_DISTANCE) {
            TripDetail tripDetail = new TripDetail();
            tripDetail.setMode(GlobalVariable.WALK_MODE);
            Location destinationLocation = new Location(destinationLong, destinationLat);
            tripDetail.setLocation(destinationLocation);

            ZonedDateTime arrivalTime = timeService.localDateTimeToZonedDateTime(dateTime.plusSeconds(distance));
            tripDetail.setAimedArrivalTime(arrivalTime);
            tripDetail.setExpectedArrivalTime(arrivalTime);
            tripDetail.setArrivalDelay(0);

            TripPlannerDelivery tripPlannerDelivery = new TripPlannerDelivery();
            tripPlannerDelivery.setOriginLocation(new Location(originLong, originLat));
            tripPlannerDelivery.setDestinationLocation(destinationLocation);
            tripPlannerDelivery.getTripDetails().add(tripDetail);

            serviceDelivery.getTripPlannerDeliveries().add(tripPlannerDelivery);
        }

        // Get near stop by origin


        List<NearestStop> nearestStopOrigin = journeyRepository.getNearestStopFromLocation(originLat,
                originLong,
                dateTime.getDayOfWeek().name().toLowerCase(),
                timeService.localDateZoneGTFSByDateTime(dateTime),
                timeService.localTimeZoneGTFSByDateTime(dateTime));

        System.out.println(nearestStopOrigin);

        return new Gtfs(null, null, serviceDelivery);
    }
}
