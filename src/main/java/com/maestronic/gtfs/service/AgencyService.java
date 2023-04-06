package com.maestronic.gtfs.service;

import com.maestronic.gtfs.entity.Agency;
import com.maestronic.gtfs.repository.AgencyRepository;
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
import java.io.*;

@Service
public class AgencyService {

    @PersistenceContext
    private EntityManager entityManager;
    private Session session;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;
    @Autowired
    private AgencyRepository agencyRepository;

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
//            agencyRepository.deleteAllData();

            // Insert new data
            for (CSVRecord csvRecord : csvParser) {
                Agency agency = new Agency(
                        csvRecord.get("agency_id"),
                        csvRecord.get("agency_name"),
                        csvRecord.get("agency_url"),
                        csvRecord.get("agency_timezone"),
                        csvParser.getHeaderMap().containsKey("agency_lang") ? csvRecord.get("agency_lang") : "",
                        csvParser.getHeaderMap().containsKey("agency_phone") ? csvRecord.get("agency_phone") : "",
                        csvParser.getHeaderMap().containsKey("agency_fare_url") ? csvRecord.get("agency_fare_url") : "",
                        csvParser.getHeaderMap().containsKey("agency_email") ? csvRecord.get("agency_email") : ""
                );

                session.saveOrUpdate(agency);
                // compare batch saved count
                dataCount++;
                checkBatchSize(dataCount);
            }

            session.flush();
            session.clear();
            Logger.info("Parse and save " + GlobalVariable.AGENCY + " file is complete.");
            return dataCount;
        } catch (Exception e) {
            String logMessage = "Fail to parse and save " + GlobalVariable.AGENCY + " file. " + e.getMessage();
            throw new RuntimeException(logMessage);
        }
    }
}
