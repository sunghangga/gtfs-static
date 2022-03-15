package com.maestronic.gtfs.trip;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends JpaRepository<Trip, String> {
    @Modifying
    @Query(value = "TRUNCATE TABLE trips CASCADE", nativeQuery = true)
    void deleteAllData();
}
