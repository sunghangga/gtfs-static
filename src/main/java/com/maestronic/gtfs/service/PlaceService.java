package com.maestronic.gtfs.service;

import com.maestronic.gtfs.entity.Place;
import com.maestronic.gtfs.repository.PlaceRepository;
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
public class PlaceService {

    @PersistenceContext
    private EntityManager entityManager;
    private Session session;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;
    @Autowired
    private PlaceRepository placeRepository;

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

    public Integer parseSaveData(List<nl.connekt.bison.chb.Place> xmlData) {

        getSession();
        int dataCount = 0;
        try{
            // Delete all data in table
            placeRepository.deleteAllData();
            for (nl.connekt.bison.chb.Place record : xmlData) {
                Place place = new Place(
                        record.getDaowcode(),
                        record.getPlacecode(),
                        record.getPublicname(),
                        record.getTown()
                );

                session.save(place);
                // compare batch saved count
                dataCount++;
                checkBatchSize(dataCount);
            }
            session.flush();
            session.clear();
            Logger.info("Parse and save " + GlobalVariable.PLACES + " file is complete.");
            return dataCount;
        } catch (Exception e) {
            String logMessage = "Fail to parse and save " + GlobalVariable.PLACES + ". " + e.getMessage();
            throw new RuntimeException(logMessage);
        }
    }
}
