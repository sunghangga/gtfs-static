package com.maestronic.gtfs.calendardate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarDateRepository extends JpaRepository<CalendarDate, CalendarDateCompositeId> {
    @Modifying
    @Query(value = "TRUNCATE TABLE calendar_dates CASCADE", nativeQuery = true)
    void deleteAllData();
}
