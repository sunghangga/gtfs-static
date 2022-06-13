package com.maestronic.gtfs.repository;

import com.maestronic.gtfs.entity.NearestStop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
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

    public List<NearestStop> getNearestStopFromLocation(Double originLat, Double originLong, String day, int date, String time) {
        EntityManager em = emf.createEntityManager();

        // Get trip location
        String queryString = "SELECT t.trip_id, s.*, st.stop_sequence, t.direction_id, st.arrival_time, st.departure_time " +
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
                "INNER JOIN (SELECT stop_id, stop_code, stop_name, " +
                "   st_distancesphere(geometry(point(stop_lon, stop_lat)), geometry(point(" + originLong + ", " + originLat + "))) distance " +
                "   FROM stops) s ON st.stop_id = s.stop_id " +
                "WHERE s.distance < 500 " +
                "AND st.departure_time > '" + time + "'";

        // Execute query builder
        Query query = em.createNativeQuery(queryString, NearestStop.class);
        List<NearestStop> dataListShape = query.getResultList();

        em.close();

        return dataListShape;
    }
}
