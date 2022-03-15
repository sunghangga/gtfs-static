package com.maestronic.gtfs.repository;

import com.maestronic.gtfs.entity.ImportDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportDetailRepository extends JpaRepository<ImportDetail, Integer> {
}
