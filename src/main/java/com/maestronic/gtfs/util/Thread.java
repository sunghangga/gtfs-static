package com.maestronic.gtfs.util;

import com.maestronic.gtfs.importer.Import;
import com.maestronic.gtfs.importer.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class Thread {

    @Autowired
    private ImportService importService;

    @Async
    public void importGtfsThread(Import importInit, MultipartFile file) {
        importService.importGtfsData(importInit, file);
    }

    @Async
    public void importChbThread(Import importInit, MultipartFile file) {
        importService.importChbData(importInit, file);
    }

    @Async
    public void importPsaThread(Import importInit, MultipartFile file) {
        importService.importPsaData(importInit, file);
    }
}
