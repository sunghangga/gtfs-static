package com.maestronic.gtfs.importer;

import com.maestronic.gtfs.util.GlobalVariable;
import com.maestronic.gtfs.util.Thread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
public class ImportInitialize implements GlobalVariable {

    @Autowired
    private Thread thread;
    @Autowired
    private ImportRepository importRepository;

    public Import initializeImport(String taskName, MultipartFile file, String fileType, LocalDateTime releaseDate) {
        // Save import status
        Import importInit = new Import(
                taskName,
                file.getOriginalFilename(),
                fileType,
                IMPORT_DETAIL_PREPARE,
                IMPORT_STATE_PREPARE,
                IMPORT_STATUS_IN_PROGRESS,
                LocalDateTime.now(),
                LocalDateTime.now(),
                releaseDate);
        importRepository.save(importInit);

        // Start import process GTFS
        if (fileType.equals(GTFS_FILE_TYPE)) {
            thread.importGtfsThread(importInit, file);
        } else if (fileType.equals(CHB_FILE_TYPE)) { // Start import process CHB
            thread.importChbThread(importInit, file);
        } else { // Start import process PSA
            thread.importPsaThread(importInit, file);
        }

        return importInit;
    }
}
