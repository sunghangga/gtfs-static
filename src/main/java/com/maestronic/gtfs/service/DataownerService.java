package com.maestronic.gtfs.service;

import com.maestronic.gtfs.entity.Dataowner;
import com.maestronic.gtfs.repository.DataownerRepository;
import com.maestronic.gtfs.util.GlobalVariable;
import com.maestronic.gtfs.util.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class DataownerService {

    @PersistenceContext
    private EntityManager entityManager;
    private Session session;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;
    @Autowired
    private DataownerRepository dataownerRepository;

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

    public Integer parseSaveData(List<nl.connekt.bison.chb.Dataowner> xmlData) {

        getSession();
        int dataCount = 0;
        try {
            // Delete all data in table
            dataownerRepository.deleteAllData();
            for (nl.connekt.bison.chb.Dataowner record : xmlData) {
                Dataowner dataOwner = new Dataowner(
                        record.getDaowcode(),
                        record.getDaowname(),
                        record.getDaowtype()
                );

                session.save(dataOwner);
                // compare batch saved count
                dataCount++;
                checkBatchSize(dataCount);
            }
            session.flush();
            session.clear();
            Logger.info("Parse and save " + GlobalVariable.DATAOWNER + " file is complete.");
            return dataCount;
        } catch (Exception e) {
            String logMessage = "Fail to parse and save " + GlobalVariable.DATAOWNER + ". " + e.getMessage();
            throw new RuntimeException(logMessage);
        }
    }
}
