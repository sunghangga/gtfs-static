package com.maestronic.gtfs.repository;

import com.maestronic.gtfs.compositeid.FareRulesCompositeId;
import com.maestronic.gtfs.entity.FareRules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FareRulesRepository extends JpaRepository<FareRules, FareRulesCompositeId> {
    @Modifying
    @Query(value = "TRUNCATE TABLE fare_rules CASCADE", nativeQuery = true)
    void deleteAllData();
}
