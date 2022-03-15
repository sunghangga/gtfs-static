package com.maestronic.gtfs.route;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends JpaRepository<Route, String> {
    @Modifying
    @Query(value = "TRUNCATE TABLE routes CASCADE", nativeQuery = true)
    void deleteAllData();
}
