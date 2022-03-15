package com.maestronic.gtfs.transfer;

import com.maestronic.gtfs.shape.ShapeRepository;
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
public class TransferService {

    @PersistenceContext
    private EntityManager entityManager;
    private Session session;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;
    @Autowired
    private TransferRepository transferRepository;

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
            transferRepository.deleteAllData();

            for (CSVRecord csvRecord : csvParser) {
                Transfer transfer = new Transfer(
                        csvRecord.get("from_stop_id"),
                        csvRecord.get("to_stop_id"),
                        csvRecord.get("from_route_id"),
                        csvRecord.get("to_route_id"),
                        csvRecord.get("from_trip_id"),
                        csvRecord.get("to_trip_id"),
                        Integer.parseInt(csvRecord.get("transfer_type")),
                        csvParser.getHeaderMap().containsKey("min_transfer_time") ? Integer.parseInt(csvRecord.get("min_transfer_time")) : null
                );

                session.save(transfer);
                // compare batch saved count
                dataCount++;
                checkBatchSize(dataCount);
            }

            session.flush();
            session.clear();
            Logger.info("Parse and save " + GlobalVariable.TRANSFERS + " file is complete.");
            return dataCount;
        } catch (Exception e) {
            String logMessage = "Fail to parse and save " + GlobalVariable.TRANSFERS + " file. " + e.getMessage();
            throw new RuntimeException(logMessage);
        }
    }
}
