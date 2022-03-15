package com.maestronic.gtfs.repository;

import com.maestronic.gtfs.compositeid.ShapeCompositeId;
import com.maestronic.gtfs.entity.Shape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShapeRepository extends JpaRepository<Shape, ShapeCompositeId> {
    @Modifying
    @Query(value = "TRUNCATE TABLE shapes CASCADE", nativeQuery = true)
    void deleteAllData();
}
