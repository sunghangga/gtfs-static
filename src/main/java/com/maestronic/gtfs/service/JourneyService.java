package com.maestronic.gtfs.service;

import com.maestronic.gtfs.entity.Journey;
import com.maestronic.gtfs.dto.gtfs.*;
import com.maestronic.gtfs.entity.JourneyParam;
import com.maestronic.gtfs.repository.JourneyRepository;
import com.maestronic.gtfs.util.GlobalHelper;
import com.maestronic.gtfs.util.GlobalVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class JourneyService implements GlobalVariable {

    @Autowired
    private JourneyRepository journeyRepository;
    @Autowired
    private TimeService timeService;
    ServiceDelivery serviceDelivery = new ServiceDelivery();
    HashMap<UUID, Journey> journeyList = new HashMap<>();
    List<UUID> resultList = new ArrayList<>();
    List<JourneyParam> markedStops = new ArrayList<>();
    String day;
    int date;
    String time;
    String tripType;
    double destLat;
    double destLon;
    int countResult = 0;
    int maxExpectedResult = 5;
    int currentTransfers = 0;
    int allowedTransfers = 0;

    public List<Map<String, Object>> getShapeJourneyWithStop(String agencyId, String vehicleId) {
        return journeyRepository.getShapeJourneyWithStop(agencyId, vehicleId);
    }

    private void reconstructJourney() {
        List<TripPlanners> tripPlanners;
        for (UUID key : resultList) {
            tripPlanners = new ArrayList<>();
            Journey journey = journeyList.get(key);
            while (journey != null) {
                tripPlanners.add(0,
                        new TripPlanners(
                                MODE.get(journey.getMode()),
                                journey.getStopName(),
                                null,
                                null,
                                null,
                                null
                        )
                );
                // If the stop is the first stop of journey
                if (journey.getPrevKey() == null)
                    break;
                journey = journeyList.get(journey.getPrevKey());
            }
            TripPlannerDelivery tripPlannerDelivery = new TripPlannerDelivery(tripPlanners);
            serviceDelivery.getTripPlannerDeliveries().add(tripPlannerDelivery);
        }
    }

    private List<Tuple> getAllFollowingStop(String tripId, String stopId) {
        List<Tuple> tripListByStop = journeyRepository.getAllFollowingStop(
                tripId,
                stopId);

        return tripListByStop;
    }

    private List<Tuple> getAllFollowingStopMultiTrip(List<JourneyParam> markedStopParams) {
        List<Tuple> tripListByStop = journeyRepository.getAllFollowingStopMultiTrip(markedStopParams);

        return tripListByStop;
    }

    private List<Tuple> getAllTripsByStops(List<JourneyParam> markedStopParams) {
        List<Tuple> allTripsByStop = journeyRepository.getTripsByStops(
                markedStopParams,
                tripType);
        return allTripsByStop;
    }

    private void addStops(UUID key, UUID prevId, Tuple markedStop) {
        journeyList.put(key, new Journey(
                key,
                prevId,
                markedStop.get("route_type").toString(),
                markedStop.get("trip_id").toString(),
                markedStop.get("stop_id").toString(),
                markedStop.get("stop_name").toString(),
                Integer.parseInt(markedStop.get("stop_sequence").toString()),
                timeService.strTimeToDuration(markedStop.get("arrival_time").toString()),
                timeService.strTimeToDuration(markedStop.get("departure_time").toString()),
        ));
    }

    private void addMarkedStop(UUID key, UUID prevKey, Tuple markedStop) {
        Duration arrivalTime = timeService.strTimeToDuration(markedStop.get("arrival_time").toString());
        Duration departureTime = timeService.strTimeToDuration(markedStop.get("departure_time").toString());
        markedStops.add(new JourneyParam(
                key,
                prevKey,
                markedStop.get("trip_id").toString(),
                markedStop.get("stop_id").toString(),
                Integer.parseInt(markedStop.get("stop_sequence").toString()),
                arrivalTime,
                departureTime
        ));
    }

    private void getFollowingStops(List<JourneyParam> markedStopParams) {
        // Find solution based on count result (get following stops)
        List<Tuple> allFollowingStops = getAllFollowingStopMultiTrip(markedStopParams);
        UUID prevKey = null;
        UUID key;
        for (int i = 0; i < allFollowingStops.size(); i++) {
            Tuple allFollowingStop = allFollowingStops.get(i);
            // Get first UUID for each trips
            if (i == 0 || !allFollowingStops.get(i-1).get("trip_id").equals(allFollowingStop.get("trip_id")))
                prevKey = UUID.fromString(allFollowingStop.get("key").toString());

            // Add current stop to result journey
            key = UUID.randomUUID();
            addStops(key, prevKey, allFollowingStop);
            addMarkedStop(key, prevKey, allFollowingStop);
            prevKey = key;

            // Do check the stop if near destination (possible reach by walk)
            if (GlobalHelper.computeGreatCircleDistance((double) allFollowingStop.get("stop_lat"),
                    (double) allFollowingStop.get("stop_lon"), destLat, destLon) < MAX_WALK_DISTANCE) {
                resultList.add(key);
                countResult++;
                if (countResult >= maxExpectedResult)
                    return;
            }
        }
    }

    private void getTransfersNoWalk(List<JourneyParam> markedStopParams) {
        // Get all trips from the following stop loop
        List<Tuple> allTripsByStop = getAllTripsByStops(markedStopParams);

        // Find for next transfers no walk
        int allTripSize = allTripsByStop.size();
        if (allTripSize > 0) {
            for (Tuple trip : allTripsByStop) {
                UUID key = UUID.randomUUID();
                addMarkedStop(key, UUID.fromString(trip.get("key").toString()), trip);
            }
        }
    }

    public Gtfs getTripPlannerWithFare(Double originLat,
                                                            Double originLong,
                                                            String originName,
                                                            Double destinationLat,
                                                            Double destinationLong,
                                                            String destinationName,
                                                            LocalDateTime dateTime,
                                                            String typeOfTrip) {

        tripType = typeOfTrip;
        destLat = destinationLat;
        destLon = destinationLong;
        // Check if origin and destination possible reach by walk
        long distance = (long) GlobalHelper.computeGreatCircleDistance(originLat, originLong, destinationLat, destinationLong);
        if (distance <= MAX_WALK_DISTANCE) {
            // Set TripPlannerDelivery
            List<TripPlanners> tripPlanners = new ArrayList<>();
            tripPlanners.add(new TripPlanners(
                    MODE.get(WALK_MODE),
                    originName,
                    null,
                    timeService.localDateTimeToZonedDateTime(dateTime),
                    null,
                    null
            ));
            tripPlanners.add(new TripPlanners(
                    MODE.get(WALK_MODE),
                    destinationName,
                    timeService.localDateTimeToZonedDateTime(dateTime.plusSeconds((long) (distance/NORMAL_WALKING_SPEED))),
                    null,
                    null,
                    null
            ));

            TripPlannerDelivery tripPlannerDelivery = new TripPlannerDelivery(tripPlanners);

            // Set first trip plan
            serviceDelivery.getTripPlannerDeliveries().add(tripPlannerDelivery);
        }

        // Set variable
        day = dateTime.getDayOfWeek().name().toLowerCase();
        date = timeService.localDateZoneGTFSByDateTime(dateTime);
        time = timeService.localTimeZoneGTFSByDateTime(dateTime);

        // Get near stop by origin
        List<Tuple> nearestStops = journeyRepository.getNearestStopFromLocation(originLat,
                originLong,
                day,
                date,
                time,
                typeOfTrip);

        if (nearestStops.size() > 0) {
            // Add to list
            for (Tuple nearestStop : nearestStops) {
                UUID key = UUID.randomUUID();
                addMarkedStop(key, null, nearestStop);
                addStops(key, null, nearestStop);
            }

            // Loop while maximum result reached
            List<JourneyParam> tempStops;
            while (countResult < maxExpectedResult) {
                tempStops = markedStops;
                markedStops = new ArrayList<>();
                // Find following stops
                getFollowingStops(tempStops);

                // Check if result is less
                if (countResult < maxExpectedResult) {
                    allowedTransfers++;
                } else {
                    break;
                }

                // Check if more transfer allowed
                if (currentTransfers < allowedTransfers) {
                    tempStops = markedStops;
                    markedStops = new ArrayList<>();
                    getTransfersNoWalk(tempStops);
                    currentTransfers++;
                }
            }
        }

        reconstructJourney();

        return new Gtfs(null, null, serviceDelivery);
    }
}
