package com.maestronic.gtfs.repository;

import com.maestronic.gtfs.entity.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportRepository extends JpaRepository<Import, Integer> {

    long countByStatusAndFileType(String status, String fileType);

    List<Import> findByStatus(String status);

    @Query("SELECT i FROM Import i WHERE (:id IS NULL OR i.id = :id)" +
            " AND (:taskName IS NULL OR i.taskName = :taskName)" +
            " AND i.fileType = :fileType" +
            " AND (:status IS NULL OR i.status = :status)")
    List<Import> findImportByIdAndTaskNameAndStatus(@Param("id") Integer id,
                                                    @Param("taskName") String taskName,
                                                    @Param("status") String status,
                                                    @Param("fileType") String fileType);

    @Query("SELECT i FROM Import i WHERE i.fileType = :fileType" +
            " ORDER BY i.id DESC")
    List<Import> findLastDateImportByFileType(@Param("fileType") String fileType, Pageable pageable);
}
