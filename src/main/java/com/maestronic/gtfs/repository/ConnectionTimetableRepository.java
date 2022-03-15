package com.maestronic.gtfs.repository;

import com.maestronic.gtfs.compositeid.ConnectionTimetableCompositeId;
import com.maestronic.gtfs.entity.ConnectionTimetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnectionTimetableRepository extends JpaRepository<ConnectionTimetable, ConnectionTimetableCompositeId> {

    @Modifying
    @Query(value = "SELECT * FROM connection_timetable(:stop_id)", nativeQuery = true)
    List<ConnectionTimetable> findConnectionTimetableByParam(@Param("stop_id") String stop_id);
}
