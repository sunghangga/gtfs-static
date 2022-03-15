package com.maestronic.gtfs.service;

import com.maestronic.gtfs.entity.FareRules;
import com.maestronic.gtfs.repository.FareRulesRepository;
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
public class FareRulesService {

    @PersistenceContext
    private EntityManager entityManager;
    private Session session;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;
    @Autowired
    private FareRulesRepository fareRulesRepository;

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
            fareRulesRepository.deleteAllData();

            for (CSVRecord csvRecord : csvParser) {
                FareRules fareRules = new FareRules(
                        csvRecord.get("fare_id"),
                        csvRecord.get("route_id"),
                        csvRecord.get("origin_id"),
                        csvRecord.get("destination_id"),
                        csvRecord.get("contains_id")
                );

                session.save(fareRules);
                // compare batch saved count
                dataCount++;
                checkBatchSize(dataCount);
            }

            session.flush();
            session.clear();
            Logger.info("Parse and save " + GlobalVariable.FARE_RULES + " file is complete.");
            return dataCount;
        } catch (Exception e) {
            String logMessage = "Fail to parse and save " + GlobalVariable.FARE_RULES + " file. " + e.getMessage();
            throw new RuntimeException(logMessage);
        }
    }
}
