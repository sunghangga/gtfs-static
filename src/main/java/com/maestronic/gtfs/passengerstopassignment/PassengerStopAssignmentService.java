package com.maestronic.gtfs.passengerstopassignment;

import com.maestronic.gtfs.util.GlobalVariable;
import com.maestronic.gtfs.util.Logger;
import com.maestronic.gtfs.util.Time;
import nl.connekt.bison.psa.Userstopcodedata;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class PassengerStopAssignmentService {

    @PersistenceContext
    private EntityManager entityManager;
    private Session session;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;
    @Autowired
    private PassengerStopAssignmentRepository passengerStopAssignmentRepository;

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

    public Integer parseSaveData(List<nl.connekt.bison.psa.Quay> xmlData) {

        getSession();
        int dataCount = 0;
        try {
            // Delete all data in table
            passengerStopAssignmentRepository.deleteAllData();
            for (nl.connekt.bison.psa.Quay record : xmlData) {
                for (Userstopcodedata userStopCode : record.getUserstopcodes().getUserstopcodedata()) {
                    PassengerStopAssignment passengerStopAssignment = new PassengerStopAssignment(
                            record.getQuaycode(),
                            userStopCode.getDataownercode(),
                            userStopCode.getUserstopcode(),
                            userStopCode.getValidfrom() == null ? null : Time.strToLocalDateTime(userStopCode.getValidfrom()),
                            userStopCode.getValidthru() == null ? null : Time.strToLocalDateTime(userStopCode.getValidthru())
                    );

                    session.save(passengerStopAssignment);
                    // compare batch saved count
                    dataCount++;
                    checkBatchSize(dataCount);
                }
            }
            session.flush();
            session.clear();
            Logger.info("Parse and save " + GlobalVariable.PASSENGERSTOPASSIGNMENT + " file is complete.");
            return dataCount;
        } catch (Exception e) {
            String logMessage = "Fail to parse and save " + GlobalVariable.PASSENGERSTOPASSIGNMENT + ". " + e.getMessage();
            throw new RuntimeException(logMessage);
        }
    }
}
