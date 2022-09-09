package com.maestronic.gtfs.repository;

import com.maestronic.gtfs.entity.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class VehicleRepository {

    private final EntityManagerFactory emf;

    @Autowired
    public VehicleRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public List<Vehicle> getVehicles() {
        EntityManager em = emf.createEntityManager();
        // Execute query builder
        Query query = em.createQuery("SELECT v FROM Vehicle v");
        List<Vehicle> dataList = query.getResultList();
        em.close();

        return dataList;
    }

    public List<Map<String, Object>> getVehiclePositions(String agencyId, String vehicleId, String tripId, long timestamp) {
        EntityManager em = emf.createEntityManager();

        String queryString = "SELECT vp.vehicle_label, vp.position_latitude, vp.position_longitude, r.agency_id, " +
                "r.route_id, vp_apx.trip_id, vp_apx.stop_id, vp.timestamp " +
                "FROM vehicle_positions vp " +
                "LEFT JOIN (SELECT id, trip_id, stop_id " +
                            "FROM vehicle_positions " +
                            "WHERE timestamp >= " + timestamp + ") vp_apx ON vp.id = vp_apx.id " +
                "LEFT JOIN trip_updates tu ON vp_apx.trip_id = tu.trip_id " +
                "LEFT JOIN routes r ON r.route_id  = tu.route_id " +
                "WHERE 1 = 1 ";

        if (agencyId != null && agencyId != "") {
            queryString += "AND r.agency_id = '" + agencyId + "' ";
        }
        if (vehicleId != null && vehicleId != "") {
            queryString += "AND vp.vehicle_label = '" + vehicleId + "' ";
        }
        if (tripId != null && tripId != "") {
            queryString += "AND tu.trip_id = '" + tripId + "' ";
        }

        // Execute query builder
        Query query = em.createNativeQuery(queryString, Tuple.class);
        List<Tuple> dataList = query.getResultList();
        List<Map<String, Object>> result = new ArrayList<>();
        dataList.stream().forEach(
                tuple -> {
                    result.add(new HashMap<String, Object>() {{
                        put("vehicleLabel", tuple.get("vehicle_label"));
                        put("location", new HashMap<String, Object>() {{
                            put("latitude", tuple.get("position_latitude"));
                            put("longitude", tuple.get("position_longitude"));
                        }});
                        put("agencyId", tuple.get("agency_id"));
                        put("routeId", tuple.get("route_id"));
                        put("tripId", tuple.get("trip_id"));
                        put("stopId", tuple.get("stop_id"));
                        put("timestamp", tuple.get("timestamp"));
                    }});
                }
        );
        em.close();

        return result;
    }

    public static List<Map<String, Object>> convertTuplesToMap(List<Tuple> tuples) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Tuple single : tuples) {
            Map<String, Object> tempMap = new HashMap<>();
            for (TupleElement<?> key : single.getElements()) {
                tempMap.put(key.getAlias(), single.get(key));
            }
            result.add(tempMap);
        }
        return result;
    }
}
