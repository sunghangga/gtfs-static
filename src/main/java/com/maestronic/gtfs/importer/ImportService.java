package com.maestronic.gtfs.importer;

import com.maestronic.gtfs.feedinfo.FeedInfo;
import com.maestronic.gtfs.feedinfo.FeedInfoRepository;
import com.maestronic.gtfs.util.File;
import com.maestronic.gtfs.util.GlobalVariable;
import com.maestronic.gtfs.util.Logger;
import com.maestronic.gtfs.util.Time;
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
    private FeedInfoRepository feedInfoRepository;
    @Autowired
    private ImportTransaction importTransaction;
    @Autowired
    private File fileHelper;
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

        // Update import status
        importInit.setLastState(IMPORT_STATE_SAVE);
        importInit.setDetail(logMessage);
        importInit.setUpdatedAt(LocalDateTime.now());
        importRepository.save(importInit);

        try {
            // Parse data and save it
            importTransaction.parseSaveDataGtfs(listPath, importComponent);
            // Set boolean
            bool = true;
        } catch (Exception e) {
            logMessage = "Parse and save process failed! " + e.getMessage();
            Logger.error(logMessage);
            // Update import status
            importInit.setStatus(IMPORT_STATUS_FAILED);
            importInit.setDetail(logMessage);
            importInit.setUpdatedAt(LocalDateTime.now());
            importRepository.save(importInit);
            bool = false;
        }

        return bool;
    }

    public Export unMarshallChb(ArrayList<String> listPath, Import importInit) {
        String logMessage = IMPORT_DETAIL_UNMARSHALL;
        Logger.info(logMessage);

        // Update import status
        importInit.setLastState(IMPORT_STATE_UNMARSHALL);
        importInit.setDetail(logMessage);
        importInit.setUpdatedAt(LocalDateTime.now());
        importRepository.save(importInit);

        try {
            // Use for big XML data
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new FileReader(listPath.get(0)));

            JAXBContext jaxbContext = JAXBContext.newInstance(Export.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (Export) unmarshaller.unmarshal(xmlStreamReader);
        } catch (Exception e) {
            logMessage = "Unmarshalling process failed! " + e.getMessage();
            Logger.error(logMessage);
            // Update import status
            importInit.setStatus(IMPORT_STATUS_FAILED);
            importInit.setDetail(logMessage);
            importInit.setUpdatedAt(LocalDateTime.now());
            importRepository.save(importInit);
            return null;
        }
    }

    public Boolean processDataChb(Export exportXml, Import importInit) {
        String logMessage = IMPORT_DETAIL_SAVE;
        Logger.info(logMessage);
        boolean bool;

        // Update import status
        importInit.setLastState(IMPORT_STATE_SAVE);
        importInit.setDetail(logMessage);
        importInit.setUpdatedAt(LocalDateTime.now());
        importRepository.save(importInit);

        try {
            // Parse data and save it
            importTransaction.parseSaveDataChb(exportXml, importComponent);
            // Set boolean
            bool = true;
        } catch (Exception e) {
            logMessage = "Parse and save process failed! " + e.getMessage();
            Logger.error(logMessage);
            // Update import status
            importInit.setStatus(IMPORT_STATUS_FAILED);
            importInit.setDetail(logMessage);
            importInit.setUpdatedAt(LocalDateTime.now());
            importRepository.save(importInit);
            bool = false;
        }

        return bool;
    }

    public nl.connekt.bison.psa.Export unMarshallPsa(ArrayList<String> listPath, Import importInit) {
        String logMessage = IMPORT_DETAIL_UNMARSHALL;
        Logger.info(logMessage);

        // Update import status
        importInit.setLastState(IMPORT_STATE_UNMARSHALL);
        importInit.setDetail(logMessage);
        importInit.setUpdatedAt(LocalDateTime.now());
        importRepository.save(importInit);

        try {
            // Use for big XML data
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new FileReader(listPath.get(0)));

            JAXBContext jaxbContext = JAXBContext.newInstance(nl.connekt.bison.psa.Export.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (nl.connekt.bison.psa.Export) unmarshaller.unmarshal(xmlStreamReader);
        } catch (Exception e) {
            logMessage = "Unmarshalling process failed! " + e.getMessage();
            Logger.error(logMessage);
            // Update import status
            importInit.setStatus(IMPORT_STATUS_FAILED);
            importInit.setDetail(logMessage);
            importInit.setUpdatedAt(LocalDateTime.now());
            importRepository.save(importInit);
            return null;
        }
    }

    public Boolean processDataPsa(nl.connekt.bison.psa.Export exportXml, Import importInit) {
        String logMessage = IMPORT_DETAIL_SAVE;
        Logger.info(logMessage);
        boolean bool;

        // Update import status
        importInit.setLastState(IMPORT_STATE_SAVE);
        importInit.setDetail(logMessage);
        importInit.setUpdatedAt(LocalDateTime.now());
        importRepository.save(importInit);

        try {
            // Parse data and save it
            importTransaction.parseSaveDataPsa(exportXml, importComponent);
            // Set boolean
            bool = true;
        } catch (Exception e) {
            logMessage = "Parse and save process failed! " + e.getMessage();
            Logger.error(logMessage);
            // Update import status
            importInit.setStatus(IMPORT_STATUS_FAILED);
            importInit.setDetail(logMessage);
            importInit.setUpdatedAt(LocalDateTime.now());
            importRepository.save(importInit);
            bool = false;
        }

        return bool;
    }

    private String uploadFileProc(MultipartFile file, String destDir, Import importInit) {

        String logMessage = IMPORT_DETAIL_UPLOAD + " '" + file.getOriginalFilename() + "' to directory '" + destDir + "'";
        Logger.info(logMessage);

        // Update import status
        importInit.setLastState(IMPORT_STATE_UPLOAD);
        importInit.setDetail(logMessage);
        importInit.setUpdatedAt(LocalDateTime.now());
        importRepository.save(importInit);

        Map<String, String> result = fileHelper.uploadFile(file, destDir);
        importInit.setStatus(result.get("status"));
        importInit.setDetail(result.get("message"));
        importInit.setUpdatedAt(LocalDateTime.now());
        importRepository.save(importInit);

        return result.get("data");
    }

    private ArrayList<String> extractArchiveProc(String archivePath, String destDir, Import importInit) {

        String logMessage = IMPORT_DETAIL_EXTRACT + " '" + archivePath + "' to directory '" + destDir + "'";
        Logger.info(logMessage);

        // Update import status to extract
        importInit.setLastState(IMPORT_STATE_EXTRACT);
        importInit.setDetail(logMessage);
        importInit.setUpdatedAt(LocalDateTime.now());
        importRepository.save(importInit);

        Map<String, Object> result = fileHelper.extractArchive(archivePath, destDir);
        importInit.setStatus(result.get("status").toString());
        importInit.setDetail(result.get("message").toString());
        importInit.setUpdatedAt(LocalDateTime.now());
        importRepository.save(importInit);

        return (ArrayList<String>) result.get("data");
    }

    public void deleteUploadedFiles(String destDir, Import importInit) {
        String logMessage = IMPORT_DETAIL_DELETE + " from directory '" + destDir + "'";
        Logger.info(logMessage);

        // Update import status
        importInit.setLastState(IMPORT_STATE_DELETE);
        importInit.setDetail(logMessage);
        importInit.setUpdatedAt(LocalDateTime.now());
        importRepository.save(importInit);

        java.io.File folder = new java.io.File(destDir);
        if (!folder.exists()) {
            Logger.warn("Directory '" + destDir + "' doesn't exists!");
            return;
        }

        String[] files = folder.list();
        if (files == null) {
            logMessage = "No file found in directory '" + destDir + "'";
            Logger.warn(logMessage);
            importInit.setDetail(logMessage);
            importInit.setUpdatedAt(LocalDateTime.now());
            importRepository.save(importInit);
        } else {
            logMessage = files.length + " file will be deleted";
            Logger.info(logMessage);
            importInit.setDetail(logMessage);
            importInit.setUpdatedAt(LocalDateTime.now());
            importRepository.save(importInit);
            for (String file : files) {
                java.io.File currentFile = new java.io.File(folder.getPath(), file);
                if (currentFile.delete()) {
                    logMessage = "File '" + currentFile.getPath() + "' is deleted";
                    Logger.info(logMessage);
                } else {
                    logMessage = "Failed deleting file '" + currentFile.getPath() + "'!";
                    Logger.warn(logMessage);
                }
                importInit.setDetail(logMessage);
                importInit.setUpdatedAt(LocalDateTime.now());
                importRepository.save(importInit);
            }
        }
    }

    private Boolean checkValidVersion(ArrayList<String> listPath, Import importInit) {

        String logMessage = IMPORT_DETAIL_VALIDATE;
        Logger.info(logMessage);

        // Update import status
        importInit.setLastState(IMPORT_STATE_VALIDATE);
        importInit.setDetail(logMessage);
        importInit.setUpdatedAt(LocalDateTime.now());
        importRepository.save(importInit);

        for (String path: listPath) {
            if (path.substring(path.lastIndexOf(java.io.File.separator) + 1).equals(FEED_INFO)) {
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
                            if (Time.localDateZoneGTFS() >= Integer.parseInt(csvRecord.get("feed_start_date"))) {
                                logMessage = "Check validation GTFS data is complete. GTFS data is valid, ready to import!";
                                Logger.info(logMessage);
                                importInit.setDetail(logMessage);
                                importInit.setUpdatedAt(LocalDateTime.now());
                                importRepository.save(importInit);
                                return true;
                            } else {
                                logMessage = "Validation GTFS data is complete. GTFS date or version is not valid! Please import valid GTFS.";
                                Logger.error(logMessage);
                                importInit.setDetail(logMessage);
                                importInit.setUpdatedAt(LocalDateTime.now());
                                importRepository.save(importInit);
                                return false;
                            }
                        }
                        // Check if version import is latest and start_date is valid
                        if (Integer.parseInt(csvRecord.get("feed_version")) >= Integer.parseInt(feedInfo.getFeedVersion())
                                && Time.localDateZoneGTFS() >= Integer.parseInt(csvRecord.get("feed_start_date"))) {
                            logMessage = "Check validation GTFS data is complete. GTFS data is valid, ready to import!";
                            Logger.info(logMessage);
                            importInit.setDetail(logMessage);
                            importInit.setUpdatedAt(LocalDateTime.now());
                            importRepository.save(importInit);
                            return true;
                        }
                    }
                    logMessage = "Validation GTFS data is complete. GTFS date or version is not valid! Please import valid GTFS.";
                    Logger.error(logMessage);
                    importInit.setDetail(logMessage);
                    importInit.setUpdatedAt(LocalDateTime.now());
                    importRepository.save(importInit);
                    return false;
                } catch (Exception e) {
                    logMessage = "Fail to validate GTFS data. " + e.getMessage();
                    Logger.error(logMessage);
                    importInit.setDetail(logMessage);
                    importInit.setUpdatedAt(LocalDateTime.now());
                    importRepository.save(importInit);
                    throw new RuntimeException(logMessage);
                }
            }
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
            importComponent = new ImportComponent(0, new ArrayList<>(), new HashMap<>());
            Boolean procStatus;
            ArrayList<String> listPath;
            boolean isValid;

            // STEP 1 Clear destination directory
            fileHelper.clearUploadDirectory(destDirGtfs);
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
                        + "Process completed in " + Time.calculateTime((long) stopWatch.getLastTaskInfo().getTimeSeconds());
                Logger.info(logMessage);
                importInit.setStatus(IMPORT_STATUS_SUCCEED);
            } else {
                logMessage = "Import process failed! Data has been rolled back.";
                Logger.error(logMessage);
                importInit.setStatus(IMPORT_STATUS_FAILED);
            }
            importInit.setDetail(logMessage);
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
            importComponent = new ImportComponent(0, new ArrayList<>(), new HashMap<>());
            Boolean procStatus;
            ArrayList<String> listPath;

            // STEP 1 Clear destination directory
            fileHelper.clearUploadDirectory(destDirChb);
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
                        + "Process completed in " + Time.calculateTime((long) stopWatch.getLastTaskInfo().getTimeSeconds());
                Logger.info(logMessage);
                importInit.setStatus(IMPORT_STATUS_SUCCEED);
            } else {
                logMessage = "Import process failed! Data has been rolled back.";
                Logger.error(logMessage);
                importInit.setStatus(IMPORT_STATUS_FAILED);
            }
            importInit.setDetail(logMessage);
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
            importComponent = new ImportComponent(0, new ArrayList<>(), new HashMap<>());
            Boolean procStatus;
            ArrayList<String> listPath;

            // STEP 1 Clear destination directory
            fileHelper.clearUploadDirectory(destDirPsa);
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
                        + "Process completed in " + Time.calculateTime((long) stopWatch.getLastTaskInfo().getTimeSeconds());
                Logger.info(logMessage);
                importInit.setStatus(IMPORT_STATUS_SUCCEED);
            } else {
                logMessage = "Import process failed! Data has been rolled back.";
                Logger.error(logMessage);
                importInit.setStatus(IMPORT_STATUS_FAILED);
            }
            importInit.setDetail(logMessage);
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
