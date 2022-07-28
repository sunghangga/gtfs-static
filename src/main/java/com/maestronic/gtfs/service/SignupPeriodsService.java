package com.maestronic.gtfs.service;

import com.maestronic.gtfs.entity.SignupPeriods;
import com.maestronic.gtfs.util.GlobalVariable;
import com.maestronic.gtfs.util.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.BufferedReader;
import java.io.FileReader;

@Service
public class SignupPeriodsService {

    @PersistenceContext
    private EntityManager entityManager;
    private Session session;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;

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

            // Insert new data
            for (CSVRecord csvRecord : csvParser) {
                SignupPeriods signupPeriods = new SignupPeriods(
                        csvRecord.get("sign_id"),
                        Integer.parseInt(csvRecord.get("from_date")),
                        Integer.parseInt(csvRecord.get("to_date"))
                );

                session.saveOrUpdate(signupPeriods);
                // compare batch saved count
                dataCount++;
                checkBatchSize(dataCount);
            }

            session.flush();
            session.clear();
            Logger.info("Parse and save " + GlobalVariable.SIGNUP_PERIODS + " file is complete.");
            return dataCount;
        } catch (Exception e) {
            String logMessage = "Fail to parse and save " + GlobalVariable.SIGNUP_PERIODS + " file. " + e.getMessage();
            throw new RuntimeException(logMessage);
        }
    }
}
