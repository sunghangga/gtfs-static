package com.maestronic.gtfs.repository;

import com.maestronic.gtfs.entity.PassengerStopAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerStopAssignmentRepository extends JpaRepository<PassengerStopAssignment, Integer> {
    @Modifying
    @Query(value = "TRUNCATE TABLE passengerstopassignment CASCADE", nativeQuery = true)
    void deleteAllData();
}
