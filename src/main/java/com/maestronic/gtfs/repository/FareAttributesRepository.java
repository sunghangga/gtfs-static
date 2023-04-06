package com.maestronic.gtfs.repository;

import com.maestronic.gtfs.entity.FareAttributes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FareAttributesRepository extends JpaRepository<FareAttributes, String> {

    @Modifying
    @Query(value = "TRUNCATE TABLE fare_attributes CASCADE", nativeQuery = true)
    void deleteAllData();
}
