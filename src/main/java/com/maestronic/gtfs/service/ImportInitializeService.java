package com.maestronic.gtfs.service;

import com.maestronic.gtfs.entity.Import;
import com.maestronic.gtfs.entity.ImportDetail;
import com.maestronic.gtfs.repository.ImportDetailRepository;
import com.maestronic.gtfs.repository.ImportRepository;
import com.maestronic.gtfs.util.GlobalVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImportInitializeService implements GlobalVariable {

    @Autowired
    private ThreadService threadService;
    @Autowired
    private ImportRepository importRepository;
    @Autowired
    private ImportDetailRepository importDetailRepository;

    public Import initializeImport(String taskName, MultipartFile file, String fileType, LocalDateTime releaseDate) {
        // Save import status
        Import importInit = new Import(
                taskName,
                file.getOriginalFilename(),
                fileType,
                IMPORT_STATUS_IN_PROGRESS,
                LocalDateTime.now(),
                LocalDateTime.now(),
                releaseDate);
        importRepository.save(importInit);
        // Save import detail
        importDetailRepository.save(new ImportDetail(
                IMPORT_STATE_PREPARE,
                IMPORT_DETAIL_PREPARE,
                importInit,
                LocalDateTime.now(),
                LocalDateTime.now())
        );

        // Start import process GTFS
        if (fileType.equals(GTFS_FILE_TYPE)) {
            threadService.importGtfsThread(importInit, file);
        } else if (fileType.equals(CHB_FILE_TYPE)) { // Start import process CHB
            threadService.importChbThread(importInit, file);
        } else { // Start import process PSA
            threadService.importPsaThread(importInit, file);
        }

        return importInit;
    }
}
