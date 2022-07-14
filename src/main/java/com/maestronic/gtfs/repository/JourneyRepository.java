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
        String queryString = "SELECT * FROM (SELECT DISTINCT ON (res.trip_id) * FROM (SELECT DISTINCT ON (s.stop_id) " +
                "r.agency_id, r.route_id, r.route_type, r.agency_id, r.route_long_name, t.trip_id, t.direction_id, " +
                "t.trip_headsign, t.wheelchair_accessible, s.*, st.stop_sequence, t.direction_id, " +
                "cast(date_part('epoch', st.arrival_time) * INTERVAL '1 second' as varchar) as arrival_time, " +
                "cast(date_part('epoch', st.departure_time) * INTERVAL '1 second' as varchar) as departure_time, " +
                "stu.arrival_time, stu.departure_time " +
                "FROM trips t " +
                "INNER JOIN routes r ON t.route_id = r.route_id " +
                "INNER JOIN (SELECT service_id FROM calendar " +
                "          WHERE start_date <= " + date + " " +
                "            AND end_date >= " + date + " " +
                "            AND " + day + " = 1 " +
                "          UNION " +
                "            SELECT service_id FROM calendar_dates " +
                "              WHERE date = " + date + " " +
                "                AND exception_type = 1 " +
                "          EXCEPT " +
                "            SELECT service_id FROM calendar_dates " +
                "              WHERE date = " + date + " " +
                "                AND exception_type = 2 " +
                "       ) c ON c.service_id = t.service_id " +
                "INNER JOIN stop_times st ON t.trip_id = st.trip_id " +
                "INNER JOIN (SELECT stop_id, stop_code, stop_name, " +
                "   st_distancesphere(geometry(point(stop_lon, stop_lat)), geometry(point(" + originLong + ", " + originLat + "))) distance " +
                "   FROM stops) s ON st.stop_id = s.stop_id " +
                "left join trip_updates tu on tu.trip_id = t.trip_id " +
                "left join stop_time_updates stu on tu.id = stu.trip_update_id and stu.stop_id = st.stop_id" +
                "WHERE s.distance < " + GlobalHelper.MAX_WALK_DISTANCE + " " +
                "AND st." + timeOfTrip + " ('" + time + "' + (s.distance/" + GlobalVariable.NORMAL_WALKING_SPEED + ") * interval '1 second') " +
                "ORDER BY s.stop_id, st.departure_time ASC) res " +
                "ORDER BY res.trip_id, res.departure_time ASC) srt " +
                "ORDER BY srt.departure_time, srt.distance DESC";

        // Execute query builder
        Query query = em.createNativeQuery(queryString, Tuple.class);
        List<Tuple> dataListShape = query.getResultList();

        em.close();

        return dataListShape;
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
        List<Tuple> dataListShape = query.getResultList();

        em.close();

        return dataListShape;
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
        String queryString = "SELECT temp_json.key, r.route_type, t.trip_id, st.stop_id, s.stop_name, st.stop_sequence, s.stop_lat, s.stop_lon, " +
                "cast(date_part('epoch', st.arrival_time) * INTERVAL '1 second' as varchar) as arrival_time, " +
                "cast(date_part('epoch', st.departure_time) * INTERVAL '1 second' as varchar) as departure_time " +
                "FROM json_to_recordset('" + jsonStr + "') as temp_json(key varchar, trip_id varchar, stop_sequence int) " +
                "INNER JOIN trips t ON temp_json.trip_id = t.trip_id " +
                "INNER JOIN routes r ON t.route_id = r.route_id " +
                "INNER JOIN stop_times st ON t.trip_id = st.trip_id " +
                "INNER JOIN stops s ON st.stop_id = s.stop_id " +
                "WHERE st.stop_sequence > temp_json.stop_sequence " +
                "ORDER BY t.trip_id, st.stop_sequence ASC";

        // Execute query builder
        Query query = em.createNativeQuery(queryString, Tuple.class);
        List<Tuple> dataListShape = query.getResultList();

        em.close();

        return dataListShape;
    }

    public List<Tuple> getTripsByStops(List<JourneyParam> markedStopParams, String typeOfTrip) {

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
        String queryString = "SELECT temp_json.key, t.trip_id, st.stop_id, s.stop_name, r.route_type, st.stop_sequence, " +
                "cast(date_part('epoch', st.arrival_time) * INTERVAL '1 second' as varchar) as arrival_time, " +
                "cast(date_part('epoch', st.departure_time) * INTERVAL '1 second' as varchar) as departure_time " +
                "FROM stop_times st " +
                "inner join stops s on st.stop_id = s.stop_id " +
                "INNER JOIN trips t ON st.trip_id = t.trip_id " +
                "left join json_to_recordset('" + jsonStr + "') as trip_json(trip_id varchar) " +
                        "on t.trip_id = trip_json.trip_id " +
                "left join json_to_recordset('" + jsonStr + "') as temp_json(key varchar, trip_id varchar, stop_id varchar, arrival_time varchar) " +
                        "on st.stop_id = temp_json.stop_id " +
                "INNER JOIN routes r ON t.route_id = r.route_id " +
                "where st.stop_id = temp_json.stop_id " +
                "and trip_json.trip_id is null " +
                "and st." + timeOfTrip + " cast(temp_json.arrival_time as interval) " +
                "order by st.arrival_time ASC";

        // Execute query builder
        Query query = em.createNativeQuery(queryString, Tuple.class);
        List<Tuple> dataListShape = query.getResultList();

        em.close();

        return dataListShape;
    }

    public List<Tuple> getTransfersByStop(String stopId, String day, int date, Duration time) {

        EntityManager em = emf.createEntityManager();

        // Get trip location
        String queryString = "SELECT t.trip_id, s.stop_id, s.stop_code, s.stop_name, st.stop_sequence, t.direction_id, " +
                "cast(date_part('epoch', st.arrival_time) * INTERVAL '1 second' as varchar) as arrival_time, " +
                "cast(date_part('epoch', st.departure_time) * INTERVAL '1 second' as varchar) as departure_time " +
                "FROM trips t " +
                "INNER JOIN (SELECT service_id FROM calendar " +
                "          WHERE start_date <= " + date + " " +
                "            AND end_date >= " + date + " " +
                "            AND " + day + " = 1 " +
                "          UNION " +
                "            SELECT service_id FROM calendar_dates " +
                "              WHERE date = " + date + " " +
                "                AND exception_type = 1 " +
                "          EXCEPT " +
                "            SELECT service_id FROM calendar_dates " +
                "              WHERE date = " + date + " " +
                "                AND exception_type = 2 " +
                "       ) c ON c.service_id = t.service_id " +
                "LEFT JOIN stop_times st ON t.trip_id = st.trip_id " +
                "INNER JOIN transfers trf ON st.stop_id = trf.to_stop_id " +
                "LEFT JOIN stops s ON trf.to_stop_id = s.stop_id " +
                "WHERE trf.from_stop_id = '" + stopId + "' " +
                "AND st.arrival_time >= '" + time + "' + (trf.min_transfer_time * interval '1 second') " +
                "ORDER BY trf.to_stop_id, st.arrival_time ASC";

        // Execute query builder
        Query query = em.createNativeQuery(queryString, Tuple.class);
        List<Tuple> dataListShape = query.getResultList();

        em.close();

        return dataListShape;
    }
}
