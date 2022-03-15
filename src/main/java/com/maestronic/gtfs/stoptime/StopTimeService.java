package com.maestronic.gtfs.stoptime;

import com.maestronic.gtfs.util.GlobalVariable;
import com.maestronic.gtfs.util.Logger;
import com.maestronic.gtfs.util.Time;
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
public class StopTimeService {

    @PersistenceContext
    private EntityManager entityManager;
    private Session session;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;
    @Autowired
    private StopTimeRepository stopTimeRepository;

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
            stopTimeRepository.deleteAllData();

            for (CSVRecord csvRecord : csvParser) {
                StopTime stopTime = new StopTime(
                        csvRecord.get("trip_id"),
                        Time.strTimeToDuration(csvRecord.get("arrival_time")),
                        Time.strTimeToDuration(csvRecord.get("departure_time")),
                        csvRecord.get("stop_id"),
                        Integer.parseInt(csvRecord.get("stop_sequence")),
                        csvRecord.get("stop_headsign"),
                        Integer.parseInt(csvRecord.get("pickup_type")),
                        Integer.parseInt(csvRecord.get("drop_off_type")),
                        csvParser.getHeaderMap().containsKey("continuous_pickup") ? Integer.parseInt(csvRecord.get("continuous_pickup")) : null,
                        csvParser.getHeaderMap().containsKey("continuous_drop_off") ? Integer.parseInt(csvRecord.get("continuous_drop_off")) : null,
                        csvRecord.get("shape_dist_traveled").isEmpty() ? null : Double.parseDouble(csvRecord.get("shape_dist_traveled")),
                        csvRecord.get("fare_units_traveled").isEmpty() ? null : Double.parseDouble(csvRecord.get("fare_units_traveled")),
                        Integer.parseInt(csvRecord.get("timepoint"))
                );

                session.save(stopTime);
                // compare batch saved count
                dataCount++;
                checkBatchSize(dataCount);
            }

            session.flush();
            session.clear();
            Logger.info("Parse and save " + GlobalVariable.STOP_TIMES + " file is complete.");
            return dataCount;
        } catch (Exception e) {
            String logMessage = "Fail to parse and save " + GlobalVariable.STOP_TIMES + " file. " + e.getMessage();
            throw new RuntimeException(logMessage);
        }
    }
}
