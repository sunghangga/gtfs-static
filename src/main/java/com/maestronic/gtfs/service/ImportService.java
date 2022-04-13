package com.maestronic.gtfs.service;

import com.maestronic.gtfs.entity.FeedInfo;
import com.maestronic.gtfs.entity.Import;
import com.maestronic.gtfs.entity.ImportComponent;
import com.maestronic.gtfs.entity.ImportDetail;
import com.maestronic.gtfs.repository.FeedInfoRepository;
import com.maestronic.gtfs.repository.ImportDetailRepository;
import com.maestronic.gtfs.repository.ImportRepository;
import com.maestronic.gtfs.util.GlobalVariable;
import com.maestronic.gtfs.util.Logger;
import nl.connekt.bison.chb.Export;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ImportService implements GlobalVariable {

    @Autowired
    private ImportRepository importRepository;
    @Autowired
    private ImportDetailRepository importDetailRepository;
    @Autowired
    private FeedInfoRepository feedInfoRepository;
    @Autowired
    private ImportTransactionService importTransactionService;
    @Autowired
    private FileService fileServiceHelper;
    @Autowired
    private TimeService timeService;
    @Value("${file.upload-dir-gtfs}")
    private String destDirGtfs;
    @Value("${file.upload-dir-chb}")
    private String destDirChb;
    @Value("${file.upload-dir-psa}")
    private String destDirPsa;
    private ImportComponent importComponent;

    public long countByStatusAndFileType(String status, String fileType) {
        return importRepository.countByStatusAndFileType(status, fileType);
    }

    public Boolean processDataGtfs(ArrayList<String> listPath, Import importInit) {

        String logMessage = IMPORT_DETAIL_SAVE;
        Logger.info(logMessage);
        boolean bool;

        try {
            // Parse data and save it
            importTransactionService.parseSaveDataGtfs(listPath, importComponent);
            // Set boolean
            bool = true;
        } catch (Exception e) {
            logMessage = "Parse and save process failed! " + e.getMessage();
            Logger.error(logMessage);
            // Update import status
            importInit.setStatus(IMPORT_STATUS_FAILED);
            bool = false;
        }

        // Update import status
        importInit.setUpdatedAt(LocalDateTime.now());
        importRepository.save(importInit);
        // Save import detail
        importDetailRepository.save(new ImportDetail(
                IMPORT_STATE_SAVE,
                logMessage,
                importInit,
                LocalDateTime.now(),
                LocalDateTime.now())
        );

        return bool;
    }

    public Export unMarshallChb(ArrayList<String> listPath, Import importInit) {
        String logMessage = IMPORT_DETAIL_UNMARSHALL;
        Logger.info(logMessage);
        Export export = null;

        try {
            // Use for big XML data
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new FileReader(listPath.get(0)));

            JAXBContext jaxbContext = JAXBContext.newInstance(Export.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            export = (Export) unmarshaller.unmarshal(xmlStreamReader);
        } catch (Exception e) {
            logMessage = "Unmarshalling process failed! " + e.getMessage();
            Logger.error(logMessage);
            // Update import status
            importInit.setStatus(IMPORT_STATUS_FAILED);
        }

        // Update import status
        importInit.setUpdatedAt(LocalDateTime.now());
        importRepository.save(importInit);
        // Save import detail
        importDetailRepository.save(new ImportDetail(
                IMPORT_STATE_UNMARSHALL,
                logMessage,
                importInit,
                LocalDateTime.now(),
                LocalDateTime.now())
        );

        return export;
    }

    public Boolean processDataChb(Export exportXml, Import importInit) {
        String logMessage = IMPORT_DETAIL_SAVE;
        Logger.info(logMessage);
        boolean bool;

        try {
            // Parse data and save it
            importTransactionService.parseSaveDataChb(exportXml, importComponent);
            // Set boolean
            bool = true;
        } catch (Exception e) {
            logMessage = "Parse and save process failed! " + e.getMessage();
            Logger.error(logMessage);
            // Update import status
            importInit.setStatus(IMPORT_STATUS_FAILED);
            bool = false;
        }

        // Update import status
        importInit.setUpdatedAt(LocalDateTime.now());
        importRepository.save(importInit);
        // Save import detail
        importDetailRepository.save(new ImportDetail(
                IMPORT_STATE_SAVE,
                logMessage,
                importInit,
                LocalDateTime.now(),
                LocalDateTime.now())
        );

        return bool;
    }

    public nl.connekt.bison.psa.Export unMarshallPsa(ArrayList<String> listPath, Import importInit) {
        String logMessage = IMPORT_DETAIL_UNMARSHALL;
        Logger.info(logMessage);
        nl.connekt.bison.psa.Export export = null;

        try {
            // Use for big XML data
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new FileReader(listPath.get(0)));

            JAXBContext jaxbContext = JAXBContext.newInstance(nl.connekt.bison.psa.Export.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            export = (nl.connekt.bison.psa.Export) unmarshaller.unmarshal(xmlStreamReader);
        } catch (Exception e) {
            logMessage = "Unmarshalling process failed! " + e.getMessage();
            Logger.error(logMessage);
            // Update import status
            importInit.setStatus(IMPORT_STATUS_FAILED);
        }

        // Update import status
        importInit.setUpdatedAt(LocalDateTime.now());
        importRepository.save(importInit);
        // Save import detail
        importDetailRepository.save(new ImportDetail(
                IMPORT_STATE_UNMARSHALL,
                logMessage,
                importInit,
                LocalDateTime.now(),
                LocalDateTime.now())
        );

        return export;
    }

    public Boolean processDataPsa(nl.connekt.bison.psa.Export exportXml, Import importInit) {
        String logMessage = IMPORT_DETAIL_SAVE;
        Logger.info(logMessage);
        boolean bool;

        try {
            // Parse data and save it
            importTransactionService.parseSaveDataPsa(exportXml, importComponent);
            // Set boolean
            bool = true;
        } catch (Exception e) {
            logMessage = "Parse and save process failed! " + e.getMessage();
            Logger.error(logMessage);
            // Update import status
            importInit.setStatus(IMPORT_STATUS_FAILED);
            bool = false;
        }

        // Update import status
        importInit.setUpdatedAt(LocalDateTime.now());
        importRepository.save(importInit);
        // Save import detail
        importDetailRepository.save(new ImportDetail(
                IMPORT_STATE_SAVE,
                logMessage,
                importInit,
                LocalDateTime.now(),
                LocalDateTime.now())
        );

        return bool;
    }

    private String uploadFileProc(MultipartFile file, String destDir, Import importInit) {

        String logMessage = IMPORT_DETAIL_UPLOAD + " '" + file.getOriginalFilename() + "' to directory '" + destDir + "'. ";
        Logger.info(logMessage);

        Map<String, String> result = fileServiceHelper.uploadFile(file, destDir);
        importInit.setStatus(result.get("status"));
        logMessage += result.get("message");
        importInit.setUpdatedAt(LocalDateTime.now());
        importRepository.save(importInit);
        // Save import detail
        importDetailRepository.save(new ImportDetail(
                IMPORT_STATE_UPLOAD,
                logMessage,
                importInit,
                LocalDateTime.now(),
                LocalDateTime.now())
        );

        return result.get("data");
    }

    private ArrayList<String> extractArchiveProc(String archivePath, String destDir, Import importInit) {

        String logMessage = IMPORT_DETAIL_EXTRACT + " '" + archivePath + "' to directory '" + destDir + "'. ";
        Logger.info(logMessage);

        Map<String, Object> result = fileServiceHelper.extractArchive(archivePath, destDir);
        importInit.setStatus(result.get("status").toString());
        logMessage += result.get("message").toString();
        importInit.setUpdatedAt(LocalDateTime.now());
        importRepository.save(importInit);
        // Save import detail
        importDetailRepository.save(new ImportDetail(
                IMPORT_STATE_EXTRACT,
                logMessage,
                importInit,
                LocalDateTime.now(),
                LocalDateTime.now())
        );

        return (ArrayList<String>) result.get("data");
    }

    public void deleteUploadedFiles(String destDir, Import importInit) {
        String logMessage = IMPORT_DETAIL_DELETE + " from directory '" + destDir + "'";
        Logger.info(logMessage);

        // Update import status
        importInit.setUpdatedAt(LocalDateTime.now());
        importRepository.save(importInit);
        // Save import detail
        importDetailRepository.save(new ImportDetail(
                IMPORT_STATE_DELETE,
                logMessage,
                importInit,
                LocalDateTime.now(),
                LocalDateTime.now())
        );

        java.io.File folder = new java.io.File(destDir);
        if (!folder.exists()) {
            Logger.warn("Directory '" + destDir + "' doesn't exists!");
            return;
        }

        String[] files = folder.list();
        if (files == null) {
            Logger.warn("No file found in directory '" + destDir + "'");
        } else {
            for (String file : files) {
                java.io.File currentFile = new java.io.File(folder.getPath(), file);
                currentFile.delete();
            }
            Logger.info("File has been deleted");
        }
    }

    private Boolean checkValidVersion(ArrayList<String> listPath, Import importInit) {

        String logMessage = IMPORT_DETAIL_VALIDATE;
        Logger.info(logMessage);

        boolean isFeedInfoFound = false;
        for (String path: listPath) {
            if (path.substring(path.lastIndexOf(java.io.File.separator) + 1).equals(FEED_INFO)) {
                isFeedInfoFound = true;
                try (BufferedReader fileReader = new BufferedReader(new FileReader(path));
                     CSVParser csvParser = new CSVParser(fileReader,
                             CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

                    Iterable<CSVRecord> csvRecords = csvParser.getRecords();
                    // Get latest GTFS data from database
                    FeedInfo feedInfo = feedInfoRepository.findTop1ByOrderByFeedVersionDesc();
                    for (CSVRecord csvRecord : csvRecords) {
                        // Check if no feed in database
                        if (feedInfo == null) {
                            // Check if feed date is valid
                            if (timeService.localDateZoneGTFS() >= Integer.parseInt(csvRecord.get("feed_start_date"))) {
                                logMessage = "Check validation GTFS data is complete. GTFS data is valid, ready to import!";
                                Logger.info(logMessage);
                                importInit.setUpdatedAt(LocalDateTime.now());
                                importRepository.save(importInit);
                                // Save import detail
                                importDetailRepository.save(new ImportDetail(
                                        IMPORT_STATE_VALIDATE,
                                        logMessage,
                                        importInit,
                                        LocalDateTime.now(),
                                        LocalDateTime.now())
                                );
                                return true;
                            } else {
                                logMessage = "Validation GTFS data is complete. GTFS date or version is not valid! Please import valid GTFS.";
                                Logger.error(logMessage);
                                importInit.setUpdatedAt(LocalDateTime.now());
                                importRepository.save(importInit);
                                // Save import detail
                                importDetailRepository.save(new ImportDetail(
                                        IMPORT_STATE_VALIDATE,
                                        logMessage,
                                        importInit,
                                        LocalDateTime.now(),
                                        LocalDateTime.now())
                                );
                                return false;
                            }
                        }

                        // Special case for VIA GTFS data
                        String[] feedVersionCsv = csvRecord.get("feed_version").split("_");
                        String[] feedVersionDb = feedInfo.getFeedVersion().split("_");
                        // Check if version import is latest and start_date is valid
                        if (Integer.parseInt(feedVersionCsv[feedVersionCsv.length - 1]) >= Integer.parseInt(feedVersionDb[feedVersionDb.length - 1])
                                && timeService.localDateZoneGTFS() >= Integer.parseInt(csvRecord.get("feed_start_date"))) {
                            logMessage = "Check validation GTFS data is complete. GTFS data is valid, ready to import!";
                            Logger.info(logMessage);
                            importInit.setUpdatedAt(LocalDateTime.now());
                            importRepository.save(importInit);
                            // Save import detail
                            importDetailRepository.save(new ImportDetail(
                                    IMPORT_STATE_VALIDATE,
                                    logMessage,
                                    importInit,
                                    LocalDateTime.now(),
                                    LocalDateTime.now())
                            );
                            return true;
                        }
                    }
                    logMessage = "Validation GTFS data is complete. GTFS date or version is not valid! Please import valid GTFS.";
                    Logger.error(logMessage);
                    importInit.setUpdatedAt(LocalDateTime.now());
                    importRepository.save(importInit);
                    // Save import detail
                    importDetailRepository.save(new ImportDetail(
                            IMPORT_STATE_VALIDATE,
                            logMessage,
                            importInit,
                            LocalDateTime.now(),
                            LocalDateTime.now())
                    );
                    return false;
                } catch (Exception e) {
                    logMessage = "Fail to validate GTFS data. " + e.getMessage();
                    Logger.error(logMessage);
                    importInit.setUpdatedAt(LocalDateTime.now());
                    importRepository.save(importInit);
                    // Save import detail
                    importDetailRepository.save(new ImportDetail(
                            IMPORT_STATE_VALIDATE,
                            logMessage,
                            importInit,
                            LocalDateTime.now(),
                            LocalDateTime.now())
                    );
                    throw new RuntimeException(logMessage);
                }
            }
        }

        // Check if no feed info data
        if (!isFeedInfoFound) {
            logMessage = "Check validation GTFS data is complete. GTFS data is valid, ready to import!";
            Logger.info(logMessage);
            // Update import status
            importInit.setUpdatedAt(LocalDateTime.now());
            importRepository.save(importInit);
            // Save import detail
            importDetailRepository.save(new ImportDetail(
                    IMPORT_STATE_VALIDATE,
                    logMessage,
                    importInit,
                    LocalDateTime.now(),
                    LocalDateTime.now())
            );
            return true;
        }

        return false;
    }

    public void importGtfsData(Import importInit, MultipartFile file) {

        Logger.info("Import process is started.");
        Logger.info("ID = " + importInit.getId() + ", Task name = " + importInit.getTaskName());

        // Start stopwatch
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Import data");
        String logMessage;

        try {
            // Initialize variable
            destDirGtfs = new java.io.File(destDirGtfs).getAbsolutePath() + java.io.File.separator;
            importComponent = new ImportComponent(0, new ArrayList<>(), new ArrayList<>());
            Boolean procStatus;
            ArrayList<String> listPath;
            boolean isValid;

            // STEP 1 Clear destination directory
            fileServiceHelper.clearUploadDirectory(destDirGtfs);
            // STEP 2 Upload file to destination directory
            String filePath = uploadFileProc(file, destDirGtfs, importInit);
            // STEP 3 Extract compress file
            if (filePath != null) {
                listPath = extractArchiveProc(filePath, destDirGtfs, importInit);
            } else {
                throw new RuntimeException();
            }
            // STEP 4 check date and version validity of GTFS data
            if (listPath != null) {
                isValid = checkValidVersion(listPath, importInit);
            } else {
                throw new RuntimeException();
            }
            // STEP 5 Parse and save data after extract
            if (isValid) {
                procStatus = processDataGtfs(listPath, importInit);
            } else {
                throw new RuntimeException();
            }
            // STEP 6 Stop time execution import and check status import
            stopWatch.stop();
            if (procStatus) {
                logMessage = "Import process finished! "
                        + String.format("%,d", importComponent.getSaveCount()) + " data (" + importComponent.getEntityList().size() + " entity) have been saved. "
                        + "Process completed in " + timeService.calculateTime((long) stopWatch.getLastTaskInfo().getTimeSeconds());
                Logger.info(logMessage);
                importInit.setStatus(IMPORT_STATUS_SUCCEED);
            } else {
                logMessage = "Import process failed! Data has been rolled back.";
                Logger.error(logMessage);
                importInit.setStatus(IMPORT_STATUS_FAILED);
            }
            importInit.setUpdatedAt(LocalDateTime.now());
            importRepository.save(importInit);

            // Delete uploaded files
            deleteUploadedFiles(destDirGtfs, importInit);
        } catch (Exception e) {
            // Update import status
            logMessage = "Import process failed! Data has been rolled back.";
            Logger.error(logMessage);
            importInit.setStatus(IMPORT_STATUS_FAILED);
            importInit.setUpdatedAt(LocalDateTime.now());
            importRepository.save(importInit);
        }
    }

    public void importChbData(Import importInit, MultipartFile file) {

        Logger.info("Import process is started.");
        Logger.info("ID = " + importInit.getId() + ", Task name = " + importInit.getTaskName());

        // Start stopwatch
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Import data");
        String logMessage;
        Export exportXml;

        try {
            // Initialize variable
            destDirChb = new java.io.File(destDirChb).getAbsolutePath() + java.io.File.separator;
            importComponent = new ImportComponent(0, new ArrayList<>(), new ArrayList<>());
            Boolean procStatus;
            ArrayList<String> listPath;

            // STEP 1 Clear destination directory
            fileServiceHelper.clearUploadDirectory(destDirChb);
            // STEP 2 Upload file to destination directory
            String filePath = uploadFileProc(file, destDirChb, importInit);
            // STEP 3 Extract compress file
            if (filePath != null) {
                listPath = extractArchiveProc(filePath, destDirChb, importInit);
            } else {
                throw new RuntimeException();
            }
            // STEP 4 Unmarshall CHB
            if (listPath != null) {
                exportXml = unMarshallChb(listPath, importInit);
            } else {
                throw new RuntimeException();
            }
            // STEP 5 Parse and save data after extract
            if (exportXml != null) {
                procStatus = processDataChb(exportXml, importInit);
            } else {
                throw new RuntimeException();
            }
            // STEP 6 Stop time execution import and check status import
            stopWatch.stop();
            if (procStatus) {
                logMessage = "Import process finished! "
                        + String.format("%,d", importComponent.getSaveCount()) + " data (" + importComponent.getEntityList().size() + " entity) have been saved. "
                        + "Process completed in " + timeService.calculateTime((long) stopWatch.getLastTaskInfo().getTimeSeconds());
                Logger.info(logMessage);
                importInit.setStatus(IMPORT_STATUS_SUCCEED);
            } else {
                logMessage = "Import process failed! Data has been rolled back.";
                Logger.error(logMessage);
                importInit.setStatus(IMPORT_STATUS_FAILED);
            }
            importInit.setUpdatedAt(LocalDateTime.now());
            importRepository.save(importInit);

            // Delete uploaded files
            deleteUploadedFiles(destDirChb, importInit);
        } catch (Exception e) {
            // Update import status
            logMessage = "Import process failed! Data has been rolled back.";
            Logger.error(logMessage);
            importInit.setStatus(IMPORT_STATUS_FAILED);
            importInit.setUpdatedAt(LocalDateTime.now());
            importRepository.save(importInit);
        }
    }

    public void importPsaData(Import importInit, MultipartFile file) {

        Logger.info("Import process is started.");
        Logger.info("ID = " + importInit.getId() + ", Task name = " + importInit.getTaskName());

        // Start stopwatch
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Import data");
        String logMessage;
        nl.connekt.bison.psa.Export exportXml;

        try {
            // Initialize variable
            destDirPsa = new java.io.File(destDirPsa).getAbsolutePath() + java.io.File.separator;
            importComponent = new ImportComponent(0, new ArrayList<>(), new ArrayList<>());
            Boolean procStatus;
            ArrayList<String> listPath;

            // STEP 1 Clear destination directory
            fileServiceHelper.clearUploadDirectory(destDirPsa);
            // STEP 2 Upload file to destination directory
            String filePath = uploadFileProc(file, destDirPsa, importInit);
            // STEP 3 Extract compress file
            if (filePath != null) {
                listPath = extractArchiveProc(filePath, destDirPsa, importInit);
            } else {
                throw new RuntimeException();
            }
            // STEP 4 Unmarshall PSA
            if (listPath != null) {
                exportXml = unMarshallPsa(listPath, importInit);
            } else {
                throw new RuntimeException();
            }
            // STEP 5 Parse and save data after extract
            if (exportXml != null) {
                procStatus = processDataPsa(exportXml, importInit);
            } else {
                throw new RuntimeException();
            }
            // STEP 6 Stop time execution import and check status import
            stopWatch.stop();
            if (procStatus) {
                logMessage = "Import process finished! "
                        + String.format("%,d", importComponent.getSaveCount()) + " data (" + importComponent.getEntityList().size() + " entity) have been saved. "
                        + "Process completed in " + timeService.calculateTime((long) stopWatch.getLastTaskInfo().getTimeSeconds());
                Logger.info(logMessage);
                importInit.setStatus(IMPORT_STATUS_SUCCEED);
            } else {
                logMessage = "Import process failed! Data has been rolled back.";
                Logger.error(logMessage);
                importInit.setStatus(IMPORT_STATUS_FAILED);
            }
            importInit.setUpdatedAt(LocalDateTime.now());
            importRepository.save(importInit);

            // Delete uploaded files
            deleteUploadedFiles(destDirPsa, importInit);
        } catch (Exception e) {
            // Update import status
            logMessage = "Import process failed! Data has been rolled back.";
            Logger.error(logMessage);
            importInit.setStatus(IMPORT_STATUS_FAILED);
            importInit.setUpdatedAt(LocalDateTime.now());
            importRepository.save(importInit);
        }
    }

    public List<Import> getImportByParam(Integer id, String taskName, String status, String fileType) {
        return importRepository.findImportByIdAndTaskNameAndStatus(id, taskName, status, fileType);
    }
}
