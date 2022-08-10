package com.maestronic.gtfs.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class FareRepository {

    private final EntityManagerFactory emf;

    @Autowired
    public FareRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public List<Map<String, Object>> findFareForTrip(String agencyId,
                                                     String routeId,
                                                     String originId,
                                                     String destinationId,
                                                     String containsId) {
        EntityManager em = emf.createEntityManager();
        String queryString = "SELECT fa.*, fr.route_id, fr.origin_id, fr.destination_id, fr.contains_id" +
                " FROM fare_rules fr" +
                " LEFT JOIN fare_attributes fa ON fr.fare_id = fa.fare_id" +
                " WHERE 1 = 1 ";

        if (agencyId != null && agencyId != "") {
            queryString += "AND fa.agency_id = '" + agencyId + "' ";
        }
        if (routeId != null && routeId != "") {
            queryString += "AND fr.route_id = '" + routeId + "' ";
        }
        if (originId != null && originId != "") {
            queryString += "AND fr.origin_id = '" + originId + "' ";
        }
        if (destinationId != null && destinationId != "") {
            queryString += "AND fr.destination_id = '" + destinationId + "' ";
        }
        if (containsId != null && containsId != "") {
            queryString += "AND fr.contains_id = '" + containsId + "' ";
        }

        // Execute query builder
        Query query = em.createNativeQuery(queryString, Tuple.class);
        List<Tuple> dataList = query.getResultList();
        List<Map<String, Object>> result = new ArrayList<>();
        dataList.stream().forEach(
                tuple -> {
                    result.add(new HashMap<String, Object>() {{
                        put("fareId", tuple.get("fare_id"));
                        put("price", tuple.get("price"));
                        put("currencyType", tuple.get("currency_type"));
                        put("paymentMethod", tuple.get("payment_method"));
                        put("transfers", tuple.get("transfers"));
                        put("agencyId", tuple.get("agency_id"));
                        put("transferDuration", tuple.get("transfer_duration"));
                        put("routeId", tuple.get("route_id"));
                        put("originId", tuple.get("origin_id"));
                        put("destinationId", tuple.get("destination_id"));
                        put("containsId", tuple.get("contains_id"));
                    }});
                }
        );
        em.close();

        return result;
    }

    public List<Tuple> fareByRouteGroup(List<String> routeIds) {
        EntityManager em = emf.createEntityManager();
        String queryString = "select sum(fa.price) total_price, fa.currency_type\n" +
                "from fare_rules fr\n" +
                "left join fare_attributes fa on fr.fare_id = fa.fare_id\n" +
                "where route_id in (" + String.join(",", routeIds) + ")\n" +
                "group by fa.currency_type";

        // Execute query builder
        Query query = em.createNativeQuery(queryString, Tuple.class);
        List<Tuple> dataList = query.getResultList();
        em.close();

        return dataList;
    }
}
