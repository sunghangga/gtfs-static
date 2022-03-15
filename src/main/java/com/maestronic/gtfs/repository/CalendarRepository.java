package com.maestronic.gtfs.repository;

import com.maestronic.gtfs.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Integer> {
    @Modifying
    @Query(value = "TRUNCATE TABLE calendar CASCADE", nativeQuery = true)
    void deleteAllData();
}
