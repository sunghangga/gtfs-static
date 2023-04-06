package com.maestronic.gtfs.service;

import com.maestronic.gtfs.entity.FeedInfo;
import com.maestronic.gtfs.repository.FeedInfoRepository;
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
public class FeedInfoService {

    @PersistenceContext
    private EntityManager entityManager;
    private Session session;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;
    @Autowired
    private FeedInfoRepository feedInfoRepository;

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

            // Delete all row in table
//            feedInfoRepository.deleteAllData();

            for (CSVRecord csvRecord : csvParser) {
                FeedInfo feedInfo = new FeedInfo(
                        csvParser.getHeaderMap().containsKey("feed_id") ? csvRecord.get("feed_id") : "",
                        csvRecord.get("feed_publisher_name"),
                        csvRecord.get("feed_publisher_url"),
                        csvRecord.get("feed_lang"),
                        Integer.parseInt(csvRecord.get("feed_start_date")),
                        Integer.parseInt(csvRecord.get("feed_end_date")),
                        csvRecord.get("feed_version"),
                        csvParser.getHeaderMap().containsKey("feed_contact_email") ? csvRecord.get("feed_contact_email") : "",
                        csvParser.getHeaderMap().containsKey("feed_contact_url") ? csvRecord.get("feed_contact_url") : ""
                );

                session.saveOrUpdate(feedInfo);
                // compare batch saved count
                dataCount++;
                checkBatchSize(dataCount);
            }

            session.flush();
            session.clear();
            Logger.info("Parse and save " + GlobalVariable.FEED_INFO + " file is complete.");
            return dataCount;
        } catch (Exception e) {
            String logMessage = "Fail to parse and save " + GlobalVariable.FEED_INFO + " file. " + e.getMessage();
            throw new RuntimeException(logMessage);
        }
    }
}
