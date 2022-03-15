package com.maestronic.gtfs.service;

import com.maestronic.gtfs.util.GlobalVariable;
import com.maestronic.gtfs.util.Logger;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class FileService implements GlobalVariable {

    public String getFileType(String filePath) {
        try {
            Tika tika = new Tika();
            return tika.detect(new java.io.File(filePath));
        } catch (IOException e) {
            Logger.error("Error while checking file type! " + e.getMessage());
        }
        return null;
    }

    public String getFileType(MultipartFile file) {
        try {
            Tika tika = new Tika();
            return tika.detect(file.getInputStream());
        } catch (IOException e) {
            Logger.error("Error while checking file type! " + e.getMessage());
        }
        return null;
    }

    public void clearUploadDirectory(String folderPath) {
        java.io.File folder = new java.io.File(folderPath);
        if (folder.exists()) {
            String[] files = folder.list();
            if (files != null) {
                for (String file : files) {
                    if(new java.io.File(folder.getPath(), file).delete()) Logger.info("FileService '" + file + "' deleted");
                }
            }
        }
    }

    public Map<String, String> uploadFile(MultipartFile file, String destDir) {

        String logMessage;
        Map<String, String> objectResponse = new HashMap<>();

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(destDir + file.getOriginalFilename());
            java.io.File dir = new java.io.File(destDir);

            // Check directory exists
            if (!dir.exists()) if (!dir.mkdirs()) {
                logMessage = "Upload failed! Cannot create directory '" + destDir + "'!";
                Logger.error(logMessage);
                // Return update message if failed
                objectResponse.put("status", IMPORT_STATUS_FAILED);
                objectResponse.put("message", logMessage);
                objectResponse.put("data", null);
                return objectResponse;
            }

            // Write into directory
            Files.write(path, bytes);
            logMessage = "Upload succeed!";
            Logger.info(logMessage);
            // Return update detail success upload
            objectResponse.put("status", IMPORT_STATUS_IN_PROGRESS);
            objectResponse.put("message", logMessage);
            objectResponse.put("data", path.toString());
            return objectResponse;

        } catch (IOException e) {
            logMessage = "Upload failed! " + e.getMessage();
            Logger.error(logMessage);
            // Return update message if failed
            objectResponse.put("status", IMPORT_STATUS_FAILED);
            objectResponse.put("message", logMessage);
            objectResponse.put("data", null);
            return null;
        }
    }

    public Map<String, Object> unZip(String zipPath, String unzipDirectory) {
        String logMessage;
        Map<String, Object> objectResponse = new HashMap<>();

        try {
            ArrayList<String> filePath = new ArrayList<>();
            java.io.File destDir = new java.io.File(unzipDirectory);
            byte[] buffer = new byte[1024];
            FileInputStream fis = new FileInputStream(zipPath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                java.io.File newFile = new java.io.File(destDir, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        logMessage = "Extract failed! Cannot create directory '" + newFile + "'.";
                        Logger.error(logMessage);
                        objectResponse.put("status", IMPORT_STATUS_FAILED);
                        objectResponse.put("message", logMessage);
                        objectResponse.put("data", null);
                        return objectResponse;
                    }
                } else {
                    // fix for Windows-created archives
                    java.io.File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        logMessage = "Extract failed! Cannot create directory '" + parent + "'.";
                        Logger.error(logMessage);
                        objectResponse.put("status", IMPORT_STATUS_FAILED);
                        objectResponse.put("message", logMessage);
                        objectResponse.put("data", null);
                        return objectResponse;
                    }

                    // write file content
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                filePath.add(newFile.getPath());
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            fis.close();

            logMessage = "Extract succeed!";
            Logger.info(logMessage);
            objectResponse.put("status", IMPORT_STATUS_IN_PROGRESS);
            objectResponse.put("message", logMessage);
            objectResponse.put("data", filePath);
            return objectResponse;

        } catch (IOException e) {
            logMessage = "Extract failed! " + e.getMessage();
            Logger.error(logMessage);
            objectResponse.put("status", IMPORT_STATUS_FAILED);
            objectResponse.put("message", logMessage);
            objectResponse.put("data", null);
            return objectResponse;
        }
    }

    public Map<String, Object> unGzip(String gzipPath, String unGzipDirectory) {
        String logMessage;
        Map<String, Object> objectResponse = new HashMap<>();
        Path source = Paths.get(gzipPath);
        String destFile = unGzipDirectory + CHB_FILENAME;
        Path target = Paths.get(destFile);
        ArrayList<String> filePath = new ArrayList<>();
        filePath.add(destFile);

        try (GZIPInputStream gis = new GZIPInputStream(
                new FileInputStream(source.toFile()));
             FileOutputStream fos = new FileOutputStream(target.toFile())) {

            // Copy GZIPInputStream to FileOutputStream
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }

            logMessage = "Extract succeed!";
            Logger.info(logMessage);
            objectResponse.put("status", IMPORT_STATUS_IN_PROGRESS);
            objectResponse.put("message", logMessage);
            objectResponse.put("data", filePath);

            return objectResponse;
        } catch (IOException e) {
            logMessage = "Extract failed! " + e.getMessage() + ".";
            Logger.error(logMessage);
            objectResponse.put("status", IMPORT_STATUS_FAILED);
            objectResponse.put("message", logMessage);
            objectResponse.put("data", null);
            return objectResponse;
        }
    }

    public Map<String, Object> extractArchive(String archivePath, String destDir) {

        String logMessage;
        Map<String, Object> objectResponse = new HashMap<>();
        Map<String, Object> data;

        // Check if file exists
        java.io.File zipFile = new java.io.File(archivePath);
        if (!zipFile.exists()) {
            logMessage = "Extract failed! FileService '" + archivePath + "' is not exists!";
            Logger.error(logMessage);
            objectResponse.put("status", IMPORT_STATUS_FAILED);
            objectResponse.put("message", logMessage);
            objectResponse.put("data", null);
            return objectResponse;
        }

        // Check file type and extract
        try {
            String fileType = getFileType(archivePath);
            switch (fileType) {
                case "application/zip":
                    data = unZip(archivePath, destDir);
                    objectResponse.put("status", data.get("status"));
                    objectResponse.put("message", data.get("message"));
                    objectResponse.put("data", data.get("data"));
                    return objectResponse;
                case "application/gzip":
                    data = unGzip(archivePath, destDir);
                    objectResponse.put("status", data.get("status"));
                    objectResponse.put("message", data.get("message"));
                    objectResponse.put("data", data.get("data"));
                    return objectResponse;
                default:
                    logMessage = "Extract failed. Compressed file type '" + fileType + "' is not supported!";
                    Logger.error(logMessage);
                    objectResponse.put("status", IMPORT_STATUS_FAILED);
                    objectResponse.put("message", logMessage);
                    objectResponse.put("data", null);
                    return objectResponse;
            }
        } catch (NullPointerException e) {
            logMessage = "Extract failed. Unknown file type!";
            Logger.error(logMessage);
            objectResponse.put("status", IMPORT_STATUS_FAILED);
            objectResponse.put("message", logMessage);
            objectResponse.put("data", null);
            return objectResponse;
        }
    }
}
