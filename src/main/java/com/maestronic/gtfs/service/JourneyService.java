package com.maestronic.gtfs.service;

import com.maestronic.gtfs.entity.Journey;
import com.maestronic.gtfs.dto.gtfs.*;
import com.maestronic.gtfs.entity.JourneyParam;
import com.maestronic.gtfs.repository.JourneyRepository;
import com.maestronic.gtfs.util.GlobalHelper;
import com.maestronic.gtfs.util.GlobalVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class JourneyService implements GlobalVariable {

    @Autowired
    private JourneyRepository journeyRepository;
    @Autowired
    private TimeService timeService;
    @Autowired
    private RedisTemplate redisTemplate;
    HashMap<Integer, Journey> journeyList = new HashMap<>();
    List<JourneyParam> markedStops = new ArrayList<>();
    int countResult = 0;

    int key = 0;

    public List<Map<String, Object>> getShapeJourneyWithStop(String agencyId, String vehicleId) {
        return journeyRepository.getShapeJourneyWithStop(agencyId, vehicleId);
    }

    private Object getPathWalk(double originLon, double originLat, double destLon, double destLat) {
        // Get path walk
        String url = "https://routing.openstreetmap.de/routed-foot/route/v1/driving/" +
                originLon + "," + originLat + ";" +
                destLon + "," + destLat +
                "?overview=false&alternatives=false&steps=true&geometries=geojson";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, Object.class);
    }

    private Object getPathDrive(double originLon, double originLat, double destLon, double destLat) {
        // Get path walk
        String url = "https://routing.openstreetmap.de/routed-car/route/v1/driving/" +
                originLon + "," + originLat + ";" +
                destLon + "," + destLat +
                "?overview=false&alternatives=false&steps=true&geometries=geojson";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, Object.class);
    }

    private Object getPathDetailRedis(double oriLon, double oriLat, double destLon, double destLat, String mode) {
        String key = oriLon + ":" + oriLat + ":" + destLon + ":" + destLat;
        String pathMode = mode == WALK_MODE ? PATH_DETAIL_WALK_KEY : PATH_DETAIL_DRIVE_KEY;
        Object pathDetail = redisTemplate.opsForHash().get(pathMode, key);
        if (pathDetail == null) {
            if (mode == WALK_MODE) {
                pathDetail = getPathWalk(oriLon, oriLat, destLon, destLat);
            }
            else {
                pathDetail = getPathDrive(oriLon, oriLat, destLon, destLat);
            }
            redisTemplate.opsForHash().put(pathMode,
                    key,
                    pathDetail);
        }
        return pathDetail;
    }

    private void reconstructJourney(HashMap<Integer, String> resultList, LocalDateTime dateTime,
                                    String originName, String destinationName, Double oriLat, Double oriLon,
                                    Double destLat, Double destLon, ServiceDelivery serviceDelivery) {

        List<TripPlanners> tripPlanners;
        Journey journey;
        Journey prevJourney;
        TripDetail tripDetail;
        String prevMode;
        Object pathDetail;
        for (Integer key : resultList.keySet()) {
            tripPlanners = new ArrayList<>();
            journey = journeyList.get(key);
            pathDetail = null;

            // Check if destination location not same as last stop in journey
            double distance = GlobalHelper.computeGreatCircleDistance(journey.getStopLat(), journey.getStopLon(), destLat, destLon);
            if (distance != 0) {
                // Add destination stop in trip to mapping class
                tripPlanners.add(0,
                        new TripPlanners(
                                null,
                                destinationName,
                                journey.getExpectedDepartureTime() != null ? journey.getExpectedDepartureTime().plusSeconds((long) (distance/NORMAL_WALKING_SPEED)) :
                                        timeService.concatDateDur(dateTime.toLocalDate(), journey.getAimedDepartureTime()).plusSeconds((long) (distance/NORMAL_WALKING_SPEED)),
                                null,
                                null,
                                null
                    )
                );

                journey.setMode(WALK_MODE);
                // Get path walk
                pathDetail = getPathDetailRedis(journey.getStopLon(), journey.getStopLat(), destLon, destLat, WALK_MODE);
            }

            while (journey != null) {
                tripDetail = new TripDetail(
                        journey.getOperatorRef(),
                        journey.getRouteRef(),
                        journey.getRouteName(),
                        journey.getDirectionRef(),
                        journey.getTripId(),
                        journey.getTripName(),
                        journey.getStopId(),
                        journey.getStopSequence(),
                        journey.getWheelchairBoarding(),
                        timeService.concatDateDur(dateTime.toLocalDate(), journey.getAimedArrivalTime()),
                        timeService.concatDateDur(dateTime.toLocalDate(), journey.getAimedDepartureTime())
                );

                tripPlanners.add(0,
                        new TripPlanners(
                                MODE.get(journey.getMode()),
                                journey.getStopName(),
                                journey.getExpectedArrivalTime() != null ? journey.getExpectedArrivalTime() :
                                        timeService.concatDateDur(dateTime.toLocalDate(), journey.getAimedArrivalTime()),
                                journey.getExpectedDepartureTime() != null ? journey.getExpectedDepartureTime() :
                                        timeService.concatDateDur(dateTime.toLocalDate(), journey.getAimedDepartureTime()),
                                pathDetail,
                                tripDetail
                        )
                );

                // If the stop is the first stop of journey
                if (journey.getPrevKey() == null)
                    break;

                // Do check if current and previous key reach by walk then add path walk
                // detect transfer without walk
                prevJourney = journeyList.get(journey.getPrevKey());
                prevMode = journey.getPrevMode();
                if (prevMode == WALK_MODE) {
                    prevJourney.setMode(prevMode);
                    // Get path walk in redis if exists
                    pathDetail = getPathDetailRedis(prevJourney.getStopLon(), prevJourney.getStopLat(), journey.getStopLon(), journey.getStopLat(), WALK_MODE);
                } else {
                    // Check path drive in redis if exists
                    pathDetail = getPathDetailRedis(prevJourney.getStopLon(), prevJourney.getStopLat(), journey.getStopLon(), journey.getStopLat(), DRIVE_MODE);
                }
                journey = prevJourney;
            }

            // Check if source location not same as first stop in journey
            distance = GlobalHelper.computeGreatCircleDistance(oriLat, oriLon, journey.getStopLat(), journey.getStopLon());
            if (distance != 0) {
                // Add source stop in trip to mapping class
                tripPlanners.add(0,
                        new TripPlanners(
                                MODE.get(WALK_MODE),
                                originName,
                                null,
                                journey.getExpectedArrivalTime() != null ? journey.getExpectedArrivalTime().minusSeconds((long) (distance/NORMAL_WALKING_SPEED)) :
                                        timeService.concatDateDur(dateTime.toLocalDate(), journey.getAimedArrivalTime()).minusSeconds((long) (distance/NORMAL_WALKING_SPEED)),
                                getPathWalk(oriLon, oriLat, journey.getStopLon(), journey.getStopLat()),
                                null
                        )
                );
            }

            // Add trip solution
            TripPlannerDelivery tripPlannerDelivery = new TripPlannerDelivery(tripPlanners);
            serviceDelivery.getTripPlannerDeliveries().add(tripPlannerDelivery);
        }
    }

    private void addStops(Integer key, Integer prevKey, String prevMode, Tuple markedStop) {
        journeyList.put(key, new Journey(
                key,
                prevKey,
                markedStop.get("route_type").toString(),
                prevMode,
                markedStop.get("trip_id").toString(),
                markedStop.get("stop_id").toString(),
                markedStop.get("stop_name").toString(),
                Integer.parseInt(markedStop.get("stop_sequence").toString()),
                timeService.strTimeToDuration(markedStop.get("aimed_arrival_time").toString()),
                timeService.strTimeToDuration(markedStop.get("aimed_departure_time").toString()),
                markedStop.get("agency_id").toString(),
                markedStop.get("route_id").toString(),
                markedStop.get("route_long_name").toString(),
                GlobalHelper.directionName(Integer.parseInt(markedStop.get("direction_id").toString())),
                markedStop.get("trip_headsign").toString(),
                Integer.parseInt(markedStop.get("wheelchair_accessible").toString()),
                markedStop.get("expected_arrival_time") == null ? null : timeService.unixToZoneDateTime(Long.parseLong(markedStop.get("expected_arrival_time").toString())),
                markedStop.get("expected_departure_time") == null ? null : timeService.unixToZoneDateTime(Long.parseLong(markedStop.get("expected_departure_time").toString())),
                Double.parseDouble(markedStop.get("stop_lon").toString()),
                Double.parseDouble(markedStop.get("stop_lat").toString())
        ));
    }

    private void addMarkedStop(Integer key, Integer prevKey, Tuple markedStop) {
        markedStops.add(new JourneyParam(
                key,
                prevKey,
                markedStop.get("trip_id").toString(),
                markedStop.get("stop_id").toString(),
                Integer.parseInt(markedStop.get("stop_sequence").toString()),
                timeService.strTimeToDuration(markedStop.get("aimed_arrival_time").toString()),
                timeService.strTimeToDuration(markedStop.get("aimed_departure_time").toString())
        ));
    }

    private void getFollowingStops(List<JourneyParam> markedStopParams,
                                   HashMap<Integer, String> resultList,
                                   Double destLat,
                                   Double destLon,
                                   int maxExpectedResult) {
        // Find solution based on count result (get following stops)
        List<Tuple> allFollowingStops = journeyRepository.getAllFollowingStopMultiTrip(markedStopParams);
        Integer prevKey = null;
        Integer excludeStop = null;
        for (int i = 0; i < allFollowingStops.size(); i++) {
            Tuple allFollowingStop = allFollowingStops.get(i);

            // Check if in this trip found solution, then skip next stop in this trip
            Integer sourceKey = Integer.parseInt(allFollowingStop.get("key").toString());
            if (excludeStop == sourceKey)
                continue;

            // Get first key for each trips
            if (i == 0 || !allFollowingStops.get(i-1).get("key").equals(allFollowingStop.get("key")))
                prevKey = sourceKey;

            // Add current stop to result journey
            key++;
            addStops(key, prevKey, null, allFollowingStop);
            addMarkedStop(key, prevKey, allFollowingStop);
            prevKey = key;

            // Do check the stop if near destination (possible reach by walk)
            if (GlobalHelper.computeGreatCircleDistance((double) allFollowingStop.get("stop_lat"),
                    (double) allFollowingStop.get("stop_lon"), destLat, destLon) < MAX_WALK_DISTANCE) {
                resultList.put(key, allFollowingStop.get("aimed_arrival_time").toString());
                excludeStop = sourceKey;
                countResult++;
                // Check if result is equal with expected result
                if (countResult >= maxExpectedResult) {
                    return;
                }
            }
        }
    }

    private void getTransfersNoWalk(List<JourneyParam> markedStopParams,
                                    String tripType,
                                    String day,
                                    int date,
                                    String time) {
        // Get all trips from the following stop loop
        List<Tuple> allTripsByStop = journeyRepository.getTripsByStops(markedStopParams,
                tripType,
                day,
                date,
                time);

        // Find for next transfers no walk
        int allTripSize = allTripsByStop.size();
        if (allTripSize > 0) {
            for (Tuple trip : allTripsByStop) {
                key++;
                addMarkedStop(key, Integer.parseInt(trip.get("source_key").toString()), trip);
                addStops(key, Integer.parseInt(trip.get("source_key").toString()), null, trip);
            }
        }
    }

    private void getTransfersWithWalk(HashMap<Integer, String> resultList,
                                      List<JourneyParam> markedStopParams,
                                      String tripType,
                                      String day,
                                      int date,
                                      String time,
                                      Double destLat,
                                      Double destLon,
                                      int maxExpectedResult) {
        // Get all trips from the following stop loop
        List<Tuple> allTripsByStop = journeyRepository.getTransfersByStop(markedStopParams,
                tripType,
                day,
                date,
                time);

        // Find for next transfers no walk
        int allTripSize = allTripsByStop.size();
        if (allTripSize > 0) {
            for (Tuple trip : allTripsByStop) {
                key++;
                // Do check the stop if near destination (possible reach by walk)
                if (GlobalHelper.computeGreatCircleDistance((double) trip.get("stop_lat"),
                        (double) trip.get("stop_lon"), destLat, destLon) < MAX_WALK_DISTANCE) {
                    resultList.put(key, trip.get("aimed_arrival_time").toString());
                    countResult++;
                    // Check if result is equal with expected result
                    if (countResult >= maxExpectedResult) {
                        return;
                    }
                    continue;
                }
                Integer prevKey = Integer.parseInt(trip.get("source_key").toString());
                addMarkedStop(key, prevKey, trip);
                addStops(key, prevKey, WALK_MODE, trip);
            }
        }
    }

    // function to sort hashmap by values
    private HashMap<Integer, String> sortHashMapByValue(HashMap<Integer, String> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<Integer, String> > list =
                new LinkedList<Map.Entry<Integer, String> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<Integer, String> >() {
            public int compare(Map.Entry<Integer, String> o1,
                               Map.Entry<Integer, String> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<Integer, String> temp = new LinkedHashMap<Integer, String>();
        for (Map.Entry<Integer, String> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public Gtfs getTripPlannerWithFare(Double originLat,
                                                            Double originLong,
                                                            String originName,
                                                            Double destinationLat,
                                                            Double destinationLong,
                                                            String destinationName,
                                                            LocalDateTime dateTime,
                                                            String typeOfTrip,
                                                            Integer limit) {

        ServiceDelivery serviceDelivery = new ServiceDelivery();
        HashMap<Integer, String> resultList = new HashMap<>();
        journeyList = new HashMap<>();
        markedStops = new ArrayList<>();
        int maxExpectedResult = limit == null ? DEFAULT_LIMIT_RESULT_TRIP_PLANNER : limit;
        countResult = 0;

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
                    getPathWalk(originLong, originLat, destinationLong, destinationLat),
                    null
            ));
            tripPlanners.add(new TripPlanners(
                    null,
                    destinationName,
                    timeService.localDateTimeToZonedDateTime(dateTime.plusSeconds((long) (distance/NORMAL_WALKING_SPEED))),
                    null,
                    null,
                    null
            ));

            TripPlannerDelivery tripPlannerDelivery = new TripPlannerDelivery(tripPlanners);

            // Set first trip plan
            serviceDelivery.getTripPlannerDeliveries().add(tripPlannerDelivery);
            // Add result
            countResult++;
        }

        // If maxExpectedResult != 1
        if (countResult < maxExpectedResult) {
            // Set variable
            String day = dateTime.getDayOfWeek().name().toLowerCase();
            int date = timeService.localDateZoneGTFSByDateTime(dateTime);
            String time = timeService.localTimeZoneGTFSByDateTime(dateTime);

            // Get near stops from origin
            List<Tuple> nearestStopsOrigin = journeyRepository.getNearestStopFromLocation(originLat,
                    originLong,
                    day,
                    date,
                    time,
                    typeOfTrip);

            if (nearestStopsOrigin.size() > 0) {
                int currentTransfers = 0;
                int allowedTransfers = 0;
                key = 0;
                // Add to list
                for (Tuple nearestStop : nearestStopsOrigin) {
                    key++;
                    addMarkedStop(key, null, nearestStop);
                    addStops(key, null, null, nearestStop);
                }

                // Loop while maximum result reached
                List<JourneyParam> tempStops;
                while (true) {
                    tempStops = markedStops;
                    markedStops = new ArrayList<>();
                    // Find following stops
                    getFollowingStops(tempStops,
                            resultList,
                            destinationLat,
                            destinationLong,
                            maxExpectedResult);

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
                        // Find transfers with no walk
                        getTransfersNoWalk(tempStops, typeOfTrip, day, date, time);
                        // Find transfers with walk
                        getTransfersWithWalk(resultList, tempStops, typeOfTrip, day, date, time,
                                destinationLat, destinationLong, maxExpectedResult);

                        // Check again because transfers with walk do the check nearest to destination
                        if (countResult < maxExpectedResult) {
                            currentTransfers++;
                        } else {
                            break;
                        }
                    }

                    // Break looping if no solution exists
                    if (markedStops.size() < 1) {
                        break;
                    }
                }
            }
        }

        // Reconstruct journey list
        reconstructJourney(sortHashMapByValue(resultList), dateTime, originName, destinationName,
                originLat, originLong, destinationLat, destinationLong, serviceDelivery);
        return new Gtfs(null, timeService.localDateTimeZone(), serviceDelivery);
    }
}
