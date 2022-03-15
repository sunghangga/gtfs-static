package com.maestronic.gtfs.dataowner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DataownerRepository extends JpaRepository<Dataowner, Integer> {
    @Modifying
    @Query(value = "TRUNCATE TABLE dataowner CASCADE", nativeQuery = true)
    void deleteAllData();
}
