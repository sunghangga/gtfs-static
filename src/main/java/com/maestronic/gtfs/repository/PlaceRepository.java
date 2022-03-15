package com.maestronic.gtfs.repository;

import com.maestronic.gtfs.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Integer> {
    @Modifying
    @Query(value = "TRUNCATE TABLE places CASCADE", nativeQuery = true)
    void deleteAllData();
}
