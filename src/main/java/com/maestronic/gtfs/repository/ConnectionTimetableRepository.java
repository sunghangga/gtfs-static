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
