package com.maestronic.gtfs.repository;

import com.maestronic.gtfs.entity.Stopplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StopplaceRepository extends JpaRepository<Stopplace, Integer> {
    @Modifying
    @Query(value = "TRUNCATE TABLE stopplaces CASCADE", nativeQuery = true)
    void deleteAllData();
}
