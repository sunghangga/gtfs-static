package com.maestronic.gtfs.repository;

import com.maestronic.gtfs.entity.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AgencyRepository extends JpaRepository<Agency, Integer> {
    @Modifying
    @Query(value = "TRUNCATE TABLE agency CASCADE", nativeQuery = true)
    void deleteAllData();
}
