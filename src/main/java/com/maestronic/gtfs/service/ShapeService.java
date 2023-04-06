package com.maestronic.gtfs.service;

import com.maestronic.gtfs.entity.Shape;
import com.maestronic.gtfs.repository.ShapeRepository;
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
public class ShapeService {

    @Autowired
    private ShapeRepository shapeRepository;
    @PersistenceContext
    private EntityManager entityManager;
    private Session session;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;

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
//            shapeRepository.deleteAllData();

            for (CSVRecord csvRecord : csvParser) {
                Shape shape = new Shape(
                        csvRecord.get("shape_id"),
                        Double.parseDouble(csvRecord.get("shape_pt_lat")),
                        Double.parseDouble(csvRecord.get("shape_pt_lon")),
                        Integer.parseInt(csvRecord.get("shape_pt_sequence")),
                        Double.parseDouble(csvRecord.get("shape_dist_traveled"))
                );

                session.saveOrUpdate(shape);
                // compare batch saved count
                dataCount++;
                checkBatchSize(dataCount);
            }

            session.flush();
            session.clear();
            Logger.info("Parse and save " + GlobalVariable.SHAPES + " file is complete.");
            return dataCount;
        } catch (Exception e) {
            String logMessage = "Fail to parse and save " + GlobalVariable.SHAPES + " file. " + e.getMessage();
            throw new RuntimeException(logMessage);
        }
    }
}
