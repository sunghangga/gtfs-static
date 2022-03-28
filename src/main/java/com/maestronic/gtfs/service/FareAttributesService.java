package com.maestronic.gtfs.service;

import com.maestronic.gtfs.entity.FareAttributes;
import com.maestronic.gtfs.repository.FareAttributesRepository;
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
import java.math.BigDecimal;

@Service
public class FareAttributesService {

    @PersistenceContext
    private EntityManager entityManager;
    private Session session;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;
    @Autowired
    private FareAttributesRepository fareAttributesRepository;

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
//            fareAttributesRepository.deleteAllData();

            for (CSVRecord csvRecord : csvParser) {
                FareAttributes fareAttributes = new FareAttributes(
                        csvRecord.get("fare_id"),
                        new BigDecimal(csvRecord.get("price")),
                        csvRecord.get("currency_type"),
                        Integer.parseInt(csvRecord.get("payment_method")),
                        csvRecord.get("transfers").isEmpty() ? null : Integer.parseInt(csvRecord.get("transfers")),
                        csvParser.getHeaderMap().containsKey("agency_id") ? csvRecord.get("agency_id") : null,
                        csvRecord.get("transfer_duration").isEmpty() ? null : Integer.parseInt(csvRecord.get("transfer_duration"))
                );

                session.saveOrUpdate(fareAttributes);
                // compare batch saved count
                dataCount++;
                checkBatchSize(dataCount);
            }

            session.flush();
            session.clear();
            Logger.info("Parse and save " + GlobalVariable.FARE_ATTRIBUTES + " file is complete.");
            return dataCount;
        } catch (Exception e) {
            String logMessage = "Fail to parse and save " + GlobalVariable.FARE_ATTRIBUTES + " file. " + e.getMessage();
            throw new RuntimeException(logMessage);
        }
    }
}
