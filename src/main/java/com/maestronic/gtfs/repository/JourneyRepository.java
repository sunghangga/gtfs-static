package com.maestronic.gtfs.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.maestronic.gtfs.entity.Journey;
import com.maestronic.gtfs.entity.JourneyParam;
import com.maestronic.gtfs.util.GlobalHelper;
import com.maestronic.gtfs.util.GlobalVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class JourneyRepository {

    private final EntityManagerFactory emf;

    @Autowired
    public JourneyRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public List<Map<String, Object>> getShapeJourneyWithStop(String agencyId, String vehicleId) {
        EntityManager em = emf.createEntityManager();

        // Get trip location
        String queryString = "SELECT vp.vehicle_label, r.agency_id, t.trip_id, s.shape_pt_lat, s.shape_pt_lon, s.shape_pt_sequence, s.shape_dist_traveled " +
                "FROM vehicle_positions vp " +
                "INNER JOIN trip_updates tu ON vp.trip_id = tu.trip_id " +
                "INNER JOIN trips t ON t.trip_id = tu.trip_id " +
                "INNER JOIN routes r ON r.route_id = t.route_id " +
                "INNER JOIN shapes s ON s.shape_id = t.shape_id " +
                "WHERE r.agency_id = '" + agencyId + "' " +
                "AND vp.vehicle_label = '" + vehicleId +"' " +
                "ORDER BY s.shape_pt_sequence ASC";

        // Execute query builder
        Query query = em.createNativeQuery(queryString, Tuple.class);
        List<Tuple> dataListShape = query.getResultList();

        // Initialize result list
        List<Map<String, Object>> stops = new ArrayList<>();
        List<Map<String, Object>> result = new ArrayList<>();

        // Get stop location
        if (dataListShape.size() > 0) {
            queryString = "SELECT s.stop_id, s.stop_lat, s.stop_lon, st.stop_sequence " +
                    "FROM stop_times st " +
                    "INNER JOIN stops s on s.stop_id = st.stop_id " +
                    "WHERE st.trip_id = '" + dataListShape.get(0).get("trip_id") + "' " +
                    "ORDER BY st.stop_sequence ASC";

            // Execute query builder
            query = em.createNativeQuery(queryString, Tuple.class);
            List<Tuple> dataListStop = query.getResultList();

            // Mapping result
            // Loop through shape
            List<Map<String, Object>> shapes = new ArrayList<>();
            dataListShape.stream().forEach(
                    tuple -> {
                        shapes.add(new HashMap<String, Object>() {{
                            put("sequence", tuple.get("shape_pt_sequence"));
                            put("distance", tuple.get("shape_dist_traveled"));
                            put("latitude", tuple.get("shape_pt_lat"));
                            put("longitude", tuple.get("shape_pt_lon"));
                        }});
                    }
            );
            // Loop through stop
            dataListStop.stream().forEach(
                    tuple -> {
                        stops.add(new HashMap<String, Object>() {{
                            put("sequence", tuple.get("stop_sequence"));
                            put("stopId", tuple.get("stop_id"));
                            put("latitude", tuple.get("stop_lat"));
                            put("longitude", tuple.get("stop_lon"));
                        }});
                    }
            );
            // Loop through shape and stop
            result.add(new HashMap<String, Object>() {{
                put("vehicleLabel", dataListShape.get(0).get("vehicle_label"));
                put("agencyId", dataListShape.get(0).get("agency_id"));
                put("tripId", dataListShape.get(0).get("trip_id"));
                put("shapes", shapes);
                put("stops", stops);
            }});
        }

        em.close();

        return result;
    }

    public List<Tuple> getNearestStopFromLocation(Double originLat, Double originLong, String day, int date, String time, String typeOfTrip) {

        EntityManager em = emf.createEntityManager();

        String timeOfTrip = typeOfTrip.equals(GlobalVariable.DEPARTURE_TRIP) ? "departure_time >=" : "arrival_time <=";
        // Get trip location
        String queryString = "SELECT * FROM (SELECT DISTINCT ON (res.trip_id) * FROM (SELECT DISTINCT ON (s.stop_id)\n" +
                "r.route_id, r.route_type, r.agency_id, r.route_long_name, t.trip_id, t.direction_id,\n" +
                "t.trip_headsign, t.wheelchair_accessible, s.*, st.stop_sequence,\n" +
                "cast(date_part('epoch', st.arrival_time) * INTERVAL '1 second' as varchar) as aimed_arrival_time,\n" +
                "cast(date_part('epoch', st.departure_time) * INTERVAL '1 second' as varchar) as aimed_departure_time,\n" +
                "stu.arrival_time expected_arrival_time, stu.departure_time expected_departure_time\n" +
                "FROM trips t\n" +
                "INNER JOIN routes r ON t.route_id = r.route_id\n" +
                "INNER JOIN (SELECT service_id FROM calendar\n" +
                "          WHERE start_date <= " + date + "\n" +
                "            AND end_date >= " + date + "\n" +
                "            AND " + day + " = 1 \n" +
                "          UNION \n" +
                "            SELECT service_id FROM calendar_dates \n" +
                "              WHERE date = " + date + " \n" +
                "                AND exception_type = 1 \n" +
                "          EXCEPT \n" +
                "            SELECT service_id FROM calendar_dates \n" +
                "              WHERE date = " + date + " \n" +
                "                AND exception_type = 2 \n" +
                "       ) c ON c.service_id = t.service_id \n" +
                "INNER JOIN stop_times st ON t.trip_id = st.trip_id \n" +
                "INNER JOIN (SELECT stop_id, stop_code, stop_name, \n" +
                "   st_distancesphere(geometry(point(stop_lon, stop_lat)), geometry(point(" + originLong + ", " + originLat + "))) distance \n" +
                "   FROM stops) s ON st.stop_id = s.stop_id \n" +
                "left join trip_updates tu on tu.trip_id = t.trip_id \n" +
                "left join stop_time_updates stu on tu.id = stu.trip_update_id and stu.stop_id = st.stop_id \n" +
                "WHERE s.distance < " + GlobalHelper.MAX_WALK_DISTANCE + " \n" +
                "AND st." + timeOfTrip + " ('" + time + "' + (s.distance/" + GlobalVariable.NORMAL_WALKING_SPEED + ") * interval '1 second') \n" +
                "ORDER BY s.stop_id, st.departure_time ASC) res \n" +
                "ORDER BY res.trip_id, res.aimed_arrival_time ASC) srt \n" +
                "ORDER BY srt.aimed_arrival_time, srt.distance DESC";

        // Execute query builder
        Query query = em.createNativeQuery(queryString, Tuple.class);
        List<Tuple> dataList = query.getResultList();

        em.close();

        return dataList;
    }

    public List<String> getNearestStopFromDestination(Double destinationLat, Double destinationLong) {

        EntityManager em = emf.createEntityManager();

        // Get trip location
        String queryString = "SELECT s.stop_id\n" +
                "FROM (SELECT stop_id, stop_code, stop_name, \n" +
                "   st_distancesphere(geometry(point(stop_lon, stop_lat)), geometry(point(" + destinationLong + ", " + destinationLat + "))) distance \n" +
                "   FROM stops) s \n" +
                "WHERE s.distance < " + GlobalHelper.MAX_WALK_DISTANCE;

        // Execute query builder
        Query query = em.createNativeQuery(queryString, Tuple.class);
        List<Tuple> dataList = query.getResultList();
        List<String> stopList = new ArrayList<>();
        dataList.stream().forEach(
                tuple -> stopList.add(tuple.get("stop_id").toString())
        );

        em.close();

        return stopList;
    }

    public List<Tuple> getAllFollowingStop(String tripId, String stopId) {
        EntityManager em = emf.createEntityManager();

        // Get trip location
        String queryString = "SELECT r.route_type, t.trip_id, st.stop_id, s.stop_name, st.stop_sequence, s.stop_lat, s.stop_lon, " +
                "cast(date_part('epoch', st.arrival_time) * INTERVAL '1 second' as varchar) as arrival_time, " +
                "cast(date_part('epoch', st.departure_time) * INTERVAL '1 second' as varchar) as departure_time " +
                "FROM trips t " +
                "INNER JOIN routes r ON t.route_id = r.route_id " +
                "INNER JOIN stop_times st ON t.trip_id = st.trip_id " +
                "INNER JOIN stops s ON st.stop_id = s.stop_id " +
                "WHERE st.stop_sequence > (SELECT stop_sequence FROM stop_times WHERE trip_id = '" + tripId + "' AND stop_id = '" + stopId + "') " +
                "AND t.trip_id = '" + tripId + "' " +
                "ORDER BY st.stop_sequence ASC";

        // Execute query builder
        Query query = em.createNativeQuery(queryString, Tuple.class);
        List<Tuple> dataList = query.getResultList();

        em.close();

        return dataList;
    }

    public List<Tuple> getAllFollowingStopMultiTrip(List<JourneyParam> markedStopParams) {
        EntityManager em = emf.createEntityManager();

        String jsonStr = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            jsonStr = objectMapper.writeValueAsString(markedStopParams);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // Get trip location
        String queryString = "SELECT temp_json.key, r.route_type, t.trip_id, st.stop_id, s.stop_name, st.stop_sequence, s.stop_lat, s.stop_lon, \n" +
                "r.agency_id, r.route_id, r.route_long_name, t.direction_id, t.trip_headsign, t.wheelchair_accessible, \n" +
                "cast(date_part('epoch', st.arrival_time) * INTERVAL '1 second' as varchar) as aimed_arrival_time, \n" +
                "cast(date_part('epoch', st.departure_time) * INTERVAL '1 second' as varchar) as aimed_departure_time, \n" +
                "stu.arrival_time expected_arrival_time, stu.departure_time expected_departure_time \n" +
                "FROM json_to_recordset('" + jsonStr + "') as temp_json(key int, trip_id varchar, stop_sequence int) \n" +
                "INNER JOIN trips t ON temp_json.trip_id = t.trip_id \n" +
                "INNER JOIN routes r ON t.route_id = r.route_id \n" +
                "INNER JOIN stop_times st ON t.trip_id = st.trip_id \n" +
                "INNER JOIN stops s ON st.stop_id = s.stop_id \n" +
                "left join trip_updates tu on tu.trip_id = t.trip_id \n" +
                "left join stop_time_updates stu on tu.id = stu.trip_update_id and stu.stop_id = st.stop_id \n" +
                "WHERE st.stop_sequence > temp_json.stop_sequence \n" +
                "ORDER BY temp_json.key, st.stop_sequence ASC";

        // Execute query builder
        Query query = em.createNativeQuery(queryString, Tuple.class);
        List<Tuple> dataList = query.getResultList();

        em.close();

        return dataList;
    }

    public List<Tuple> getTripsByStops(List<JourneyParam> markedStopParams, String typeOfTrip, String day, int date) {

        EntityManager em = emf.createEntityManager();

        String timeOfTrip = typeOfTrip.equals(GlobalVariable.DEPARTURE_TRIP) ? "departure_time >=" : "arrival_time <=";
        String jsonStr = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            jsonStr = objectMapper.writeValueAsString(markedStopParams);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // Get trip location
        String queryString = "SELECT temp_json.prev_key source_key, t.trip_id, st.stop_id, s.stop_name, r.route_type, st.stop_sequence, \n" +
                "r.agency_id, r.route_id, r.route_long_name, t.direction_id, t.trip_headsign, t.wheelchair_accessible, \n" +
                "cast(date_part('epoch', st.arrival_time) * INTERVAL '1 second' as varchar) as aimed_arrival_time, \n" +
                "cast(date_part('epoch', st.departure_time) * INTERVAL '1 second' as varchar) as aimed_departure_time, \n" +
                "stu.arrival_time expected_arrival_time, stu.departure_time expected_departure_time \n" +
                "FROM stop_times st \n" +
                "inner join stops s on st.stop_id = s.stop_id \n" +
                "INNER JOIN trips t ON st.trip_id = t.trip_id \n" +
                "INNER JOIN (SELECT service_id FROM calendar\n" +
                "          WHERE start_date <= " + date + "\n" +
                "            AND end_date >= " + date + "\n" +
                "            AND " + day + " = 1 \n" +
                "          UNION \n" +
                "            SELECT service_id FROM calendar_dates \n" +
                "              WHERE date = " + date + " \n" +
                "                AND exception_type = 1 \n" +
                "          EXCEPT \n" +
                "            SELECT service_id FROM calendar_dates \n" +
                "              WHERE date = " + date + " \n" +
                "                AND exception_type = 2 \n" +
                "       ) c ON c.service_id = t.service_id \n" +
                "left join json_to_recordset('" + jsonStr + "') as trip_json(trip_id varchar) \n" +
                        "on t.trip_id = trip_json.trip_id \n" +
                "left join json_to_recordset('" + jsonStr + "') as temp_json(prev_key int, trip_id varchar, stop_id varchar, arrival_time varchar) \n" +
                        "on st.stop_id = temp_json.stop_id \n" +
                "INNER JOIN routes r ON t.route_id = r.route_id \n" +
                "left join trip_updates tu on tu.trip_id = t.trip_id \n" +
                "left join stop_time_updates stu on tu.id = stu.trip_update_id and stu.stop_id = st.stop_id \n" +
                "where st.stop_id = temp_json.stop_id \n" +
                "and trip_json.trip_id is null \n" +
                "and st." + timeOfTrip + " cast(temp_json.arrival_time as interval) \n" +
                "order by st.arrival_time ASC";

        // Execute query builder
        Query query = em.createNativeQuery(queryString, Tuple.class);
        List<Tuple> dataList = query.getResultList();

        em.close();

        return dataList;
    }

    public List<Tuple> getTransfersByStop(List<JourneyParam> markedStopParams, String typeOfTrip, String day, int date) {

        EntityManager em = emf.createEntityManager();

        String timeOfTrip = typeOfTrip.equals(GlobalVariable.DEPARTURE_TRIP) ? "departure_time >=" : "arrival_time <=";
        String jsonStr = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            jsonStr = objectMapper.writeValueAsString(markedStopParams);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // Get trip location
        String queryString = "SELECT temp_json.key source_key, t.trip_id, trf.to_stop_id stop_id, s.stop_name, r.route_type, st.stop_sequence, \n" +
                "r.agency_id, r.route_id, r.route_long_name, t.direction_id, t.trip_headsign, t.wheelchair_accessible, \n" +
                "cast(date_part('epoch', st.arrival_time) * INTERVAL '1 second' as varchar) as aimed_arrival_time, \n" +
                "cast(date_part('epoch', st.departure_time) * INTERVAL '1 second' as varchar) as aimed_departure_time, \n" +
                "stu.arrival_time expected_arrival_time, stu.departure_time expected_departure_time \n" +
                "FROM trips t \n" +
                "INNER JOIN routes r ON t.route_id = r.route_id \n" +
                "INNER JOIN (SELECT service_id FROM calendar \n" +
                "          WHERE start_date <= " + date + " \n" +
                "            AND end_date >= " + date + " \n" +
                "            AND " + day + " = 1 \n" +
                "          UNION \n" +
                "            SELECT service_id FROM calendar_dates \n" +
                "              WHERE date = " + date + " \n" +
                "                AND exception_type = 1 \n" +
                "          EXCEPT \n" +
                "            SELECT service_id FROM calendar_dates \n" +
                "              WHERE date = " + date + " \n" +
                "                AND exception_type = 2 \n" +
                "       ) c ON c.service_id = t.service_id \n" +
                "inner JOIN stop_times st ON t.trip_id = st.trip_id \n" +
                "INNER JOIN transfers trf ON st.stop_id = trf.from_stop_id \n" +
                "inner JOIN stops s ON trf.to_stop_id = s.stop_id \n" +
                "left join json_to_recordset('" + jsonStr + "') as trip_json(trip_id varchar) \n" +
                "       on t.trip_id = trip_json.trip_id \n" +
                "left join json_to_recordset('" + jsonStr + "') as temp_json(key int, trip_id varchar, stop_id varchar, arrival_time varchar) \n" +
                "       on st.stop_id = temp_json.stop_id \n" +
                "left join trip_updates tu on tu.trip_id = t.trip_id \n" +
                "left join stop_time_updates stu on tu.id = stu.trip_update_id and stu.stop_id = st.stop_id \n" +
                "WHERE trf.from_stop_id = temp_json.stop_id \n" +
                "and trip_json.trip_id is null \n" +
                "and st." + timeOfTrip + " cast(temp_json.arrival_time as interval) + (trf.min_transfer_time * interval '1 second') \n" +
                "ORDER BY st.arrival_time ASC";

        // Execute query builder
        Query query = em.createNativeQuery(queryString, Tuple.class);
        List<Tuple> dataList = query.getResultList();

        em.close();

        return dataList;
    }
}
