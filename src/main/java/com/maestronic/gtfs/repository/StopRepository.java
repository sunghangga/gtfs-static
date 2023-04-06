package com.maestronic.gtfs.repository;

import com.maestronic.gtfs.dto.custom.StopDto;
import com.maestronic.gtfs.entity.Stop;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.util.List;

@Repository
public interface StopRepository extends JpaRepository<Stop, String> {
    @Modifying
    @Query(value = "TRUNCATE TABLE stops CASCADE", nativeQuery = true)
    void deleteAllData();

    @Query("SELECT new com.maestronic.gtfs.dto.custom.StopDto(s.stopId," +
            "s.stopCode," +
            "s.stopName," +
            "s.ttsStopName," +
            "s.stopDesc," +
            "s.stopLat," +
            "s.stopLon," +
            "s.zoneId," +
            "s.stopUrl," +
            "s.locationType," +
            "s.parentStation," +
            "s.stopTimezone," +
            "s.wheelchairBoarding," +
            "s.levelId," +
            "s.platformCode) FROM Stop s")
    List<StopDto> findAllStops(Pageable pageable);

    @Query("SELECT new com.maestronic.gtfs.dto.custom.StopDto(s.stopId," +
            "s.stopCode," +
            "s.stopName," +
            "s.ttsStopName," +
            "s.stopDesc," +
            "s.stopLat," +
            "s.stopLon," +
            "s.zoneId," +
            "s.stopUrl," +
            "s.locationType," +
            "s.parentStation," +
            "s.stopTimezone," +
            "s.wheelchairBoarding," +
            "s.levelId," +
            "s.platformCode) FROM Stop s WHERE s.stopId = :stopId")
    StopDto findStopsByStopId(@Param("stopId") String stopId);
}
