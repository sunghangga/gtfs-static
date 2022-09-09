package com.maestronic.gtfs.repository;

import com.maestronic.gtfs.dto.custom.RouteDto;
import com.maestronic.gtfs.entity.Route;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, String> {
    @Modifying
    @Query(value = "TRUNCATE TABLE routes CASCADE", nativeQuery = true)
    void deleteAllData();

    @Query("SELECT new com.maestronic.gtfs.dto.custom.RouteDto(r.routeId," +
            "r.agencyId," +
            "r.routeShortName," +
            "r.routeLongName," +
            "r.routeDesc," +
            "r.routeType," +
            "r.routeUrl," +
            "r.routeColor," +
            "r.routeTextColor," +
            "r.routeSortOrder," +
            "r.continuousPickup," +
            "r.continuousDropOff) FROM Route r")
    List<RouteDto> findAllRoutes(Pageable pageable);

    @Query("SELECT new com.maestronic.gtfs.dto.custom.RouteDto(r.routeId," +
            "r.agencyId," +
            "r.routeShortName," +
            "r.routeLongName," +
            "r.routeDesc," +
            "r.routeType," +
            "r.routeUrl," +
            "r.routeColor," +
            "r.routeTextColor," +
            "r.routeSortOrder," +
            "r.continuousPickup," +
            "r.continuousDropOff) FROM Route r WHERE r.routeId = :routeId")
    RouteDto findRoutesByRouteId(@Param("routeId") String routeId);
}
