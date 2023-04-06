package com.maestronic.gtfs.repository;

import com.maestronic.gtfs.dto.custom.TripDto;
import com.maestronic.gtfs.entity.Trip;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, String> {
    @Modifying
    @Query(value = "TRUNCATE TABLE trips CASCADE", nativeQuery = true)
    void deleteAllData();

    @Query("SELECT new com.maestronic.gtfs.dto.custom.TripDto(t.tripId," +
            "t.routeId," +
            "t.serviceId," +
            "t.realtimeTripId," +
            "t.tripHeadsign," +
            "t.tripShortName," +
            "t.tripLongName," +
            "t.directionId," +
            "t.blockId," +
            "t.shapeId," +
            "t.wheelchairAccessible," +
            "t.bikesAllowed) FROM Trip t")
    List<TripDto> findAllTrips(Pageable pageable);

    @Query("SELECT new com.maestronic.gtfs.dto.custom.TripDto(t.tripId," +
            "t.routeId," +
            "t.serviceId," +
            "t.realtimeTripId," +
            "t.tripHeadsign," +
            "t.tripShortName," +
            "t.tripLongName," +
            "t.directionId," +
            "t.blockId," +
            "t.shapeId," +
            "t.wheelchairAccessible," +
            "t.bikesAllowed) FROM Trip t WHERE t.tripId = :tripId")
    TripDto findTripsByTripId(@Param("tripId") String tripId);
}
