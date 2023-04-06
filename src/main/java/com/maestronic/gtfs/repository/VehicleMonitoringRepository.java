package com.maestronic.gtfs.repository;

import com.maestronic.gtfs.compositeid.VehicleMonitoringCompositeId;
import com.maestronic.gtfs.entity.VehicleMonitoring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleMonitoringRepository extends JpaRepository<VehicleMonitoring, VehicleMonitoringCompositeId> {

    @Modifying
    @Query(value = "select\n" +
            "vp.vehicle_label,\n" +
            "vp.trip_start_date,\n" +
            "r.agency_id,\n" +
            "vp.route_id,\n" +
            "r.route_long_name,\n" +
            "tu.trip_id,\n" +
            "t.trip_headsign,\n" +
            "s.wheelchair_boarding,\n" +
            "stu.stop_id,\n" +
            "s.stop_name,\n" +
            "vp.position_latitude,\n" +
            "vp.position_longitude,\n" +
            "stu.stop_sequence,\n" +
            "first_s.stop_id as first_stop_id,\n" +
            "first_s.stop_name as first_stop_name,\n" +
            "last_s.stop_id as last_stop_id,\n" +
            "last_s.stop_name as last_stop_name,\n" +
            "st.departure_time as aimed_departure_time,\n" +
            "stu.departure_delay,\n" +
            "stu.departure_time as expected_departure_time,\n" +
            "st.arrival_time as aimed_arrival_time,\n" +
            "stu.arrival_delay,\n" +
            "stu.arrival_time as expected_arrival_time,\n" +
            "tu.schedule_relationship AS trip_schedule_relationship,\n" +
            "stu.schedule_relationship AS stop_schedule_relationship,\n" +
            "vp.direction_id,\n" +
            "vp.current_status,\n" +
            "vp.timestamp,\n" +
            "t.bikes_allowed,\n" +
            "r.route_color,\n" +
            "r.route_text_color\n" +
            "from\n" +
            "vehicle_positions vp\n" +
            "join trip_updates tu on\n" +
            "   vp.trip_id = tu.trip_id\n" +
            "join stop_time_updates stu on\n" +
            "   tu.id = stu.trip_update_id\n" +
            "join trips t on\n" +
            "   tu.trip_id = t.trip_id\n" +
            "join routes r on\n" +
            "   t.route_id = r.route_id\n" +
            "join stop_times st on\n" +
            "   tu.trip_id = st.trip_id\n" +
            "   and stu.stop_id = st.stop_id\n" +
            "   and st.stop_sequence = stu.stop_sequence\n" +
            "join stops s on\n" +
            "   stu.stop_id = s.stop_id\n" +
            "join stop_times first_st on\n" +
            "   tu.trip_id = first_st.trip_id\n" +
            "join stops first_s on\n" +
            "   first_st.stop_id = first_s.stop_id\n" +
            "join stop_times last_st on\n" +
            "   tu.trip_id = last_st.trip_id\n" +
            "join stops last_s on\n" +
            "   last_st.stop_id = last_s.stop_id\n" +
            "where\n" +
            "   stu.stop_sequence >= vp.current_stop_sequence\n" +
            "   and vp.timestamp >= :timestamp\n" +
            "   and first_st.stop_sequence = (\n" +
            "   select\n" +
            "       min(stop_times.stop_sequence) as min\n" +
            "   from\n" +
            "       stop_times\n" +
            "   where\n" +
            "       tu.trip_id = stop_times.trip_id)\n" +
            "   and last_st.stop_sequence = (\n" +
            "   select\n" +
            "       max(stop_times.stop_sequence) as max\n" +
            "   from\n" +
            "       stop_times\n" +
            "   where\n" +
            "       tu.trip_id = stop_times.trip_id)\n" +
            "   and r.agency_id = :agency_id\n" +
            "   and (:vehicle_label IS NULL OR vp.vehicle_label = cast(:vehicle_label as CHARACTER VARYING))\n" +
            "order by\n" +
            "   vp.vehicle_label,\n" +
            "   tu.id,\n" +
            "   stu.stop_sequence", nativeQuery = true)
    List<VehicleMonitoring> findVehicleMonitoringByParam(@Param("agency_id") String agency_id,
                                                         @Param("vehicle_label") String vehicle_label,
                                                         @Param("timestamp") long timestamp);
}
