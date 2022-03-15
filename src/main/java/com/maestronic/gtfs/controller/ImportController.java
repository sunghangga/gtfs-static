package com.maestronic.gtfs.controller;

import com.maestronic.gtfs.entity.Import;
import com.maestronic.gtfs.service.FileService;
import com.maestronic.gtfs.service.ImportInitializeService;
import com.maestronic.gtfs.service.ImportService;
import com.maestronic.gtfs.mapclass.Gtfs;
import com.maestronic.gtfs.util.GlobalVariable;
import com.maestronic.gtfs.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
public class ImportController {

    @Autowired
    private ImportService importService;
    @Autowired
    private ImportInitializeService importInitializeService;
    @Autowired
    private FileService fileServiceHelper;

    @GetMapping("/api")
    public ResponseEntity<Object> getReady() {

        return new ResponseEntity<>(
                new HashMap<String, Object>() {{
                    put("api", "GTFS Static");
                    put("version", new Gtfs().getVersion());
                }},
                HttpStatus.OK);
    }

    @PostMapping(path = "api/gtfs/import")
    public ResponseEntity<Object> importGtfs(@RequestParam(required = true) MultipartFile file,
                                             @RequestParam(required = false) String task_name,
                                             @RequestParam(required = false) String release_date) {

        // Check if there is an import process in progress
        long inProgressImport = importService.countByStatusAndFileType(GlobalVariable.IMPORT_STATUS_IN_PROGRESS, GlobalVariable.GTFS_FILE_TYPE);
        if (inProgressImport > 0) {
            return new ResponseEntity<>(
                    ResponseMessage.exceptionErrorJson(
                            HttpStatus.BAD_REQUEST.value(),
                            "There is an import process still in progress! Please wait for the current process to complete."
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        // Check if file type is supported
        String fileType = fileServiceHelper.getFileType(file);
        if (!fileType.equals("application/zip")) {
            return new ResponseEntity<>(
                    ResponseMessage.exceptionErrorJson(
                            HttpStatus.BAD_REQUEST.value(),
                            "FileService with " + fileType + " extension is not supported! Please upload file with zip extension."
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        // Check if task name is null or empty
        if (task_name == null || task_name.isEmpty()) {
            task_name = "Import " + file.getOriginalFilename();
        }

        // Check if release date is null or empty
        LocalDateTime releaseDate = null;
        if (release_date != null && !release_date.isEmpty()) {
            releaseDate = LocalDateTime.parse(release_date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        }
        Import response = importInitializeService.initializeImport(task_name, file, GlobalVariable.GTFS_FILE_TYPE,
                releaseDate);
        return new ResponseEntity<>(
                ResponseMessage.retrieveDataJson(
                        HttpStatus.OK.value(),
                        "Retrieved data successfully.",
                        response
                ),
                HttpStatus.OK);
    }

    @GetMapping(path = "api/gtfs/import/status")
    public ResponseEntity<Object> importGtfsStatus(@RequestParam(required = false) Integer id,
                                                   @RequestParam(required = false) String task_name,
                                                   @RequestParam(required = false) String status) {

        List<Import> response = importService.getImportByParam(id, task_name, status, GlobalVariable.GTFS_FILE_TYPE);
        if (response.isEmpty()) {
            return new ResponseEntity<>(
                    ResponseMessage.retrieveDataJson(
                            HttpStatus.OK.value(),
                            "No data available.",
                            response
                    ),
                    HttpStatus.OK
            );
        }

        return new ResponseEntity<>(
                ResponseMessage.retrieveDataJson(
                        HttpStatus.OK.value(),
                        "Retrieved data successfully.",
                        response
                ),
                HttpStatus.OK
        );
    }

    @PostMapping(path = "api/chb/import")
    public ResponseEntity<Object> importChb(@RequestParam(required = true) MultipartFile file,
                                            @RequestParam(required = false) String task_name,
                                            @RequestParam(required = false) String release_date) {

        // Check if file type is supported
        String fileType = fileServiceHelper.getFileType(file);
        if (!fileType.equals("application/gzip")) {
            return new ResponseEntity<>(
                    ResponseMessage.exceptionErrorJson(
                            HttpStatus.BAD_REQUEST.value(),
                            "FileService with " + fileType + " extension is not supported! Please upload file with zip, gzip extension."
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        // Check if there is an import process in progress
        long inProgressImport = importService.countByStatusAndFileType(GlobalVariable.IMPORT_STATUS_IN_PROGRESS, GlobalVariable.CHB_FILE_TYPE);
        if (inProgressImport > 0) {
            return new ResponseEntity<>(
                    ResponseMessage.exceptionErrorJson(
                            HttpStatus.BAD_REQUEST.value(),
                            "There is an import process still in progress! Please wait for the current process to complete."
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        // Check if task name is null or empty
        if (task_name == null || task_name.isEmpty()) {
            task_name = "Import " + file.getOriginalFilename();
        }

        // Check if release date is null or empty
        LocalDateTime releaseDate = null;
        if (release_date != null && !release_date.isEmpty()) {
            releaseDate = LocalDateTime.parse(release_date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        }
        Import response = importInitializeService.initializeImport(task_name, file, GlobalVariable.CHB_FILE_TYPE,
                releaseDate);
        return new ResponseEntity<>(
                ResponseMessage.retrieveDataJson(
                        HttpStatus.OK.value(),
                        "Retrieved data successfully.",
                        response
                ),
                HttpStatus.OK);
    }

    @GetMapping(path = "api/chb/import/status")
    public ResponseEntity<Object> importChbStatus(@RequestParam(required = false) Integer id,
                                                   @RequestParam(required = false) String task_name,
                                                   @RequestParam(required = false) String status) {

        List<Import> response = importService.getImportByParam(id, task_name, status, GlobalVariable.CHB_FILE_TYPE);
        if (response.isEmpty()) {
            return new ResponseEntity<>(
                    ResponseMessage.retrieveDataJson(
                            HttpStatus.OK.value(),
                            "No data available.",
                            response
                    ),
                    HttpStatus.OK
            );
        }

        return new ResponseEntity<>(
                ResponseMessage.retrieveDataJson(
                        HttpStatus.OK.value(),
                        "Retrieved data successfully.",
                        response
                ),
                HttpStatus.OK
        );
    }

    @PostMapping(path = "api/psa/import")
    public ResponseEntity<Object> importPsa(@RequestParam(required = true) MultipartFile file,
                                            @RequestParam(required = false) String task_name,
                                            @RequestParam(required = false) String release_date) {

        // Check if file type is supported
        String fileType = fileServiceHelper.getFileType(file);
        if (!fileType.equals("application/gzip")) {
            return new ResponseEntity<>(
                    ResponseMessage.exceptionErrorJson(
                            HttpStatus.BAD_REQUEST.value(),
                            "FileService with " + fileType + " extension is not supported! Please upload file with zip, gzip extension."
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        // Check if there is an import process in progress
        long inProgressImport = importService.countByStatusAndFileType(GlobalVariable.IMPORT_STATUS_IN_PROGRESS, GlobalVariable.PSA_FILE_TYPE);
        if (inProgressImport > 0) {
            return new ResponseEntity<>(
                    ResponseMessage.exceptionErrorJson(
                            HttpStatus.BAD_REQUEST.value(),
                            "There is an import process still in progress! Please wait for the current process to complete."
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        // Check if task name is null or empty
        if (task_name == null || task_name.isEmpty()) {
            task_name = "Import " + file.getOriginalFilename();
        }

        // Check if release date is null or empty
        LocalDateTime releaseDate = null;
        if (release_date != null && !release_date.isEmpty()) {
            releaseDate = LocalDateTime.parse(release_date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        }
        Import response = importInitializeService.initializeImport(task_name, file, GlobalVariable.PSA_FILE_TYPE,
                releaseDate);
        return new ResponseEntity<>(
                ResponseMessage.retrieveDataJson(
                        HttpStatus.OK.value(),
                        "Retrieved data successfully.",
                        response
                ),
                HttpStatus.OK);
    }

    @GetMapping(path = "api/psa/import/status")
    public ResponseEntity<Object> importPsaStatus(@RequestParam(required = false) Integer id,
                                                  @RequestParam(required = false) String task_name,
                                                  @RequestParam(required = false) String status) {

        List<Import> response = importService.getImportByParam(id, task_name, status, GlobalVariable.PSA_FILE_TYPE);
        if (response.isEmpty()) {
            return new ResponseEntity<>(
                    ResponseMessage.retrieveDataJson(
                            HttpStatus.OK.value(),
                            "No data available.",
                            response
                    ),
                    HttpStatus.OK
            );
        }

        return new ResponseEntity<>(
                ResponseMessage.retrieveDataJson(
                        HttpStatus.OK.value(),
                        "Retrieved data successfully.",
                        response
                ),
                HttpStatus.OK
        );
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Object> multipartException(MultipartException e) {
        return new ResponseEntity<>(
                ResponseMessage.exceptionErrorJson(
                        HttpStatus.BAD_REQUEST.value(),
                        "Multipart file parameter is required!"
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<Object> missingServletRequestPartException(MissingServletRequestPartException e) {
        return new ResponseEntity<>(
                ResponseMessage.exceptionErrorJson(
                        HttpStatus.BAD_REQUEST.value(),
                        e.getMessage()
                ),
                HttpStatus.BAD_REQUEST
        );
    }
}
