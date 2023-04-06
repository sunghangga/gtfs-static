package com.maestronic.gtfs.repository;

import com.maestronic.gtfs.service.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class AlertRepository {

    private final EntityManagerFactory emf;
    @Autowired
    private TimeService timeService;

    @Autowired
    public AlertRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public List<Map<String, Object>> getServiceAlert(String routeId,
                                                             String tripId,
                                                             String stopId,
                                                             String agencyId,
                                                             String routeType,
                                                             String cause,
                                                             String effect) {
        EntityManager em = emf.createEntityManager();

        // Get trip location
        String paramString = "";
        String queryString = "SELECT es.agency_id, es.route_id, es.route_type, es.trip_id, es.stop_id, " +
                "a.start, a.end, a.cause, a.effect, a.header_text, a.description_text " +
                "FROM alerts a " +
                "INNER JOIN entity_selectors es ON a.id = es.alert_id " +
                "WHERE a.start <= " + timeService.currentTimeToUnix() + " AND a.end >= " + timeService.currentTimeToUnix() + " ";

        if (routeId != null && routeId.length() > 0) {
            paramString += "OR es.route_id = '" + routeId + "' ";
        }
        if (tripId != null && tripId.length() > 0) {
            paramString += "OR es.trip_id = '" + tripId + "' ";
        }
        if (stopId != null && stopId.length() > 0) {
            paramString += "OR es.stop_id = '" + stopId + "' ";
        }
        // Check if parameter exists
        if (paramString.length() > 0) {
            queryString += "AND (1 != 1 " + paramString + ") ";
        } else {
            queryString += "AND (1 = 1) ";
        }

        if (agencyId != null && agencyId.length() > 0) {
            queryString += "AND es.agency_id = '" + agencyId + "' ";
        }
        if (routeType != null && routeType.length() > 0) {
            queryString += "AND es.route_type = " + routeType + " ";
        }
        if (cause != null && cause.length() > 0) {
            queryString += "AND a.cause = '" + cause + "' ";
        }
        if (effect != null && effect.length() > 0) {
            queryString += "AND a.effect = '" + effect + "' ";
        }
        queryString += "ORDER BY (cast(NULLIF(regexp_replace(es.route_id, '\\D', '', 'g'), '') AS integer)) ASC";

        // Execute query builder
        Query query = em.createNativeQuery(queryString, Tuple.class);
        List<Tuple> dataResult = query.getResultList();

        // Initialize result list
        List<Map<String, Object>> result = new ArrayList<>();

        // Mao result data
        if (dataResult.size() > 0) {
            dataResult.stream().forEach(
                tuple -> {
                    result.add(new HashMap<String, Object>() {{
                        put("agencyId", tuple.get("agency_id"));
                        put("routeId", tuple.get("route_id"));
                        put("routeType", tuple.get("route_type"));
                        put("tripId", tuple.get("trip_id"));
                        put("stopId", tuple.get("stop_id"));
                        put("start", timeService.unixToZoneDateTime(Long.parseLong(tuple.get("start").toString())));
                        put("end", timeService.unixToZoneDateTime(Long.parseLong(tuple.get("end").toString())));
                        put("cause", tuple.get("cause"));
                        put("effect", tuple.get("effect"));
                        put("headerText", tuple.get("header_text"));
                        put("descriptionText", tuple.get("description_text"));
                    }});
                }
            );
        }

        em.close();

        return result;
    }
}
