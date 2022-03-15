package com.maestronic.gtfs.stoptime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StopTimeRepository extends JpaRepository<StopTime, StopTimeCompositeId> {
    @Modifying
    @Query(value = "TRUNCATE TABLE stop_times CASCADE", nativeQuery = true)
    void deleteAllData();
}
