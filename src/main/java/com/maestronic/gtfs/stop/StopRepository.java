package com.maestronic.gtfs.stop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StopRepository extends JpaRepository<Stop, String> {
    @Modifying
    @Query(value = "TRUNCATE TABLE stops CASCADE", nativeQuery = true)
    void deleteAllData();
}
