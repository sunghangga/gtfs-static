package com.maestronic.gtfs.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.math.BigInteger;
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

    public List<Map<String, Object>> getVehiclePositions(String agencyId, String vehicleId, String tripId) {
        EntityManager em = emf.createEntityManager();

        String queryString = "SELECT vp.vehicle_label, vp.position_latitude, position_longitude, r.agency_id, tu.trip_id, vp.timestamp " +
                "FROM vehicle_positions vp " +
                "LEFT JOIN trip_updates tu ON vp.trip_id = tu.trip_id " +
                "LEFT JOIN routes r on r.route_id  = tu.route_id " +
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
                        put("tripId", tuple.get("trip_id"));
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
