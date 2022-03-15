package com.maestronic.gtfs.service;

import com.maestronic.gtfs.entity.Calendar;
import com.maestronic.gtfs.repository.CalendarRepository;
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
public class CalendarService {

    @PersistenceContext
    private EntityManager entityManager;
    private Session session;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;
    @Autowired
    private CalendarRepository calendarRepository;

    private Session getSession() {
        if (session == null) session = entityManager.unwrap(Session.class);
        return session;
    }

    private void checkBatchSize(int dataCount) {
        if (dataCount % batchSize == 0) {
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
            calendarRepository.deleteAllData();

            for (CSVRecord csvRecord : csvParser) {
                Calendar calendar = new Calendar(
                        csvRecord.get("service_id"),
                        Integer.parseInt(csvRecord.get("monday")),
                        Integer.parseInt(csvRecord.get("tuesday")),
                        Integer.parseInt(csvRecord.get("wednesday")),
                        Integer.parseInt(csvRecord.get("thursday")),
                        Integer.parseInt(csvRecord.get("friday")),
                        Integer.parseInt(csvRecord.get("saturday")),
                        Integer.parseInt(csvRecord.get("sunday")),
                        Integer.parseInt(csvRecord.get("start_date")),
                        Integer.parseInt(csvRecord.get("end_date"))
                );

                session.save(calendar);
                // compare batch saved count
                dataCount++;
                checkBatchSize(dataCount);
            }

            session.flush();
            session.clear();
            Logger.info("Parse and save " + GlobalVariable.CALENDAR + " file is complete.");
            return dataCount;
        } catch (Exception e) {
            String logMessage = "Fail to parse and save " + GlobalVariable.CALENDAR + " file. " + e.getMessage();
            throw new RuntimeException(logMessage);
        }
    }
}
