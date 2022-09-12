package com.maestronic.gtfs.repository;

import com.maestronic.gtfs.compositeid.ConnectionTimetableCompositeId;
import com.maestronic.gtfs.entity.ConnectionTimetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.util.List;

@Repository
public interface ConnectionTimetableRepository extends JpaRepository<ConnectionTimetable, ConnectionTimetableCompositeId> {

    @Modifying
    @Query(value = "SELECT DISTINCT ON (s.stop_id) s.stop_id,\n" +
            "    cd.date,\n" +
            "    t.direction_id,\n" +
            "    t.trip_id,\n" +
            "    r.route_id,\n" +
            "    r.agency_id,\n" +
            "    st.stop_sequence,\n" +
            "    t.trip_headsign,\n" +
            "    first_s.stop_id AS first_stop_id,\n" +
            "    last_s.stop_id AS last_stop_id,\n" +
            "    st.departure_time,\n" +
            "    st.arrival_time,\n" +
            "    q.transportmode,\n" +
            "    r.route_long_name,\n" +
            "    s.stop_name,\n" +
            "    s.stop_lat,\n" +
            "    s.stop_lon,\n" +
            "    first_s.stop_name AS first_stop_name,\n" +
            "    last_s.stop_name AS last_stop_name,\n" +
            "    q.disabledaccessible,\n" +
            "    q.visuallyaccessible\n" +
            "   FROM stopplaces sp\n" +
            "     JOIN quays q ON sp.id = q.stopplace_id\n" +
            "     JOIN dataowner d ON q.quayownercode = d.daowcode\n" +
            "     JOIN stops s ON regexp_replace(q.quaycode, '^.*:', '') = s.stop_code\n" +
            "     JOIN stop_times st ON s.stop_id = st.stop_id\n" +
            "     JOIN trips t ON st.trip_id = t.trip_id\n" +
            "     JOIN routes r ON t.route_id = r.route_id\n" +
            "     JOIN stop_times first_st ON t.trip_id = first_st.trip_id\n" +
            "     JOIN stops first_s ON first_st.stop_id = first_s.stop_id\n" +
            "     JOIN stop_times last_st ON t.trip_id = last_st.trip_id\n" +
            "     JOIN stops last_s ON last_st.stop_id = last_s.stop_id\n" +
            "     JOIN calendar_dates cd ON t.service_id = cd.service_id\n" +
            "  WHERE q.quaystatus = 'available' AND first_st.stop_sequence = (( SELECT min(stop_times.stop_sequence) AS min\n" +
            "           FROM stop_times\n" +
            "          WHERE t.trip_id = stop_times.trip_id)) AND last_st.stop_sequence = (( SELECT max(stop_times.stop_sequence) AS max\n" +
            "           FROM stop_times\n" +
            "          WHERE t.trip_id = stop_times.trip_id)) AND sp.id = (( SELECT q_1.stopplace_id\n" +
            "           FROM stops s_1\n" +
            "             JOIN passengerstopassignment psa_1 ON s_1.stop_code = psa_1.userstopcode\n" +
            "             JOIN quays q_1 ON psa_1.quaycode = q_1.quaycode AND s_1.stop_id = :stop_id\n" +
            "         LIMIT 1))\n" +
            "  order by s.stop_id, cd.date, st.arrival_time asc", nativeQuery = true)
    List<ConnectionTimetable> findConnectionTimetableByParam(@Param("stop_id") String stop_id);

    @Modifying
    @Query(value = "SELECT to_r.route_id, to_r.route_short_name, to_r.route_long_name, to_r.route_type " +
            "FROM transfers tf " +
            "INNER JOIN stop_times to_st on tf.from_stop_id = to_st.stop_id " +
            "INNER JOIN trips to_t on to_st.trip_id = to_t.trip_id " +
            "INNER JOIN routes to_r on to_t.route_id = to_r.route_id " +
            "WHERE to_r.route_id != :route_id " +
            "AND tf.from_stop_id = :stop_id " +
            "AND tf.transfer_type IS DISTINCT FROM 3 " +
            "GROUP BY to_r.route_id, to_r.route_short_name, to_r.route_type, to_r.route_long_name", nativeQuery = true)
    List<Tuple> findConnectionRoutesByParam(@Param("route_id") String routeId,
                                            @Param("stop_id") String stopId);
}
