package com.maestronic.gtfs.service;

import com.maestronic.gtfs.entity.Stop;
import com.maestronic.gtfs.repository.StopRepository;
import com.maestronic.gtfs.util.GlobalVariable;
import com.maestronic.gtfs.util.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.BufferedReader;
import java.io.FileReader;

@Service
public class StopService {

    @PersistenceContext
    private EntityManager entityManager;
    private Session session;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;
    @Autowired
    private StopRepository stopRepository;

    private Session getSession() {
        if (session == null) session = entityManager.unwrap(Session.class);
        return session;
    }

    private void checkBatchSize(int savedCount) {
        if (savedCount % batchSize == 0) {
            session.flush();
            session.clear();
        }
    }

    public Integer parseSaveData(String path) {

        getSession();
        int dataCount = 0;
        try (BufferedReader fileReader = new BufferedReader(new FileReader(path));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            // Delete all data in table
//            stopRepository.deleteAllData();

            for (CSVRecord csvRecord : csvParser) {
                Stop stop = new Stop(
                        csvRecord.get("stop_id"),
                        csvRecord.get("stop_code"),
                        csvRecord.get("stop_name"),
                        csvParser.getHeaderMap().containsKey("tts_stop_name") ? csvRecord.get("tts_stop_name") : "",
                        csvParser.getHeaderMap().containsKey("stop_desc") ? csvRecord.get("stop_desc") : "",
                        Double.parseDouble(csvRecord.get("stop_lat")),
                        Double.parseDouble(csvRecord.get("stop_lon")),
                        csvRecord.get("zone_id"),
                        csvParser.getHeaderMap().containsKey("stop_url") ? csvRecord.get("stop_url") : "",
                        csvRecord.get("location_type").isEmpty() ? null : Integer.parseInt(csvRecord.get("location_type")),
                        csvRecord.get("parent_station"),
                        csvRecord.get("stop_timezone"),
                        csvRecord.get("wheelchair_boarding").isEmpty() ? null : Integer.parseInt(csvRecord.get("wheelchair_boarding")),
                        csvParser.getHeaderMap().containsKey("level_id") ? csvRecord.get("level_id") : "",
                        csvParser.getHeaderMap().containsKey("platform_code") ? csvRecord.get("platform_code") : ""
                );

                session.saveOrUpdate(stop);
                // compare batch saved count
                dataCount++;
                checkBatchSize(dataCount);
            }

            session.flush();
            session.clear();
            Logger.info("Parse and save " + GlobalVariable.STOPS + " file is complete.");
            return dataCount;
        } catch (Exception e) {
            String logMessage = "Fail to parse and save " + GlobalVariable.STOPS + " file. " + e.getMessage();
            throw new RuntimeException(logMessage);
        }
    }
}
