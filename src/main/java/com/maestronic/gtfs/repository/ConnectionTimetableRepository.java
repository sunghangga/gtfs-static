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
    @Query(value = "SELECT * FROM connection_timetable(:stop_id)", nativeQuery = true)
    List<ConnectionTimetable> findConnectionTimetableByParam(@Param("stop_id") String stop_id);

    @Query(value = "SELECT to_r.routeId, to_r.routeShortName, to_r.routeLongName, to_r.routeType " +
            "FROM Transfer tf " +
            "INNER JOIN StopTime from_st on tf.fromStopId = from_st.stopId " +
            "INNER JOIN StopTime to_st on tf.fromStopId = to_st.stopId " +
            "INNER JOIN Trip from_t on from_st.tripId = from_t.tripId " +
            "INNER JOIN Trip to_t on to_st.tripId = to_t.tripId " +
            "INNER JOIN Route from_r on from_t.routeId = from_r.routeId " +
            "INNER JOIN Route to_r on to_t.routeId = to_r.routeId " +
            "WHERE from_r.routeId != to_r.routeId " +
            "AND from_r.routeId = :route_id " +
            "AND tf.fromStopId = :stop_id " +
            "GROUP BY to_r.routeId, to_r.routeShortName, to_r.routeType, to_r.routeLongName")
    List<Tuple> findConnectionRoutesByParam(@Param("route_id") String routeId,
                                            @Param("stop_id") String stopId);
}
