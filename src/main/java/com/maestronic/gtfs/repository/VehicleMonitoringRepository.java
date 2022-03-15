package com.maestronic.gtfs.repository;

import com.maestronic.gtfs.compositeid.VehicleMonitoringCompositeId;
import com.maestronic.gtfs.entity.VehicleMonitoring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleMonitoringRepository extends JpaRepository<VehicleMonitoring, VehicleMonitoringCompositeId> {

    @Modifying
    @Query(value = "SELECT * FROM vehicle_monitoring(:agency_id)", nativeQuery = true)
    List<VehicleMonitoring> findVehicleMonitoringByAgency(@Param("agency_id") String agency_id);

    @Modifying
    @Query(value = "SELECT * FROM vehicle_monitoring(:agency_id, :vehicle_label)", nativeQuery = true)
    List<VehicleMonitoring> findVehicleMonitoringByParam(@Param("agency_id") String agency_id,
                                                         @Param("vehicle_label") String vehicle_label);
}
