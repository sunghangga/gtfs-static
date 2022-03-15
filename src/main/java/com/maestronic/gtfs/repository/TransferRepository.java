package com.maestronic.gtfs.repository;

import com.maestronic.gtfs.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Integer> {
    @Modifying
    @Query(value = "TRUNCATE TABLE transfers CASCADE", nativeQuery = true)
    void deleteAllData();
}
