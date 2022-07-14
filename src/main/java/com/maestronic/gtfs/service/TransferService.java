package com.maestronic.gtfs.service;

import com.maestronic.gtfs.entity.Stop;
import com.maestronic.gtfs.entity.Transfer;
import com.maestronic.gtfs.repository.TransferRepository;
import com.maestronic.gtfs.util.GlobalHelper;
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
import java.util.List;
import java.util.Map;

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

    private Integer mapLocationTransferTime(List<Stop> stops, String fromStopId, String toStopId) {
        int count = 0;
        Integer transferTime = -1;
        double fromLat = 0;
        double fromLon = 0;
        double toLat = 0;
        double toLon = 0;
        for (Stop stop : stops) {
            if (stop.getStopId().equals(fromStopId)) {
                fromLat = stop.getStopLat();
                fromLon = stop.getStopLon();
                count++;
            } else if (stop.getStopId().equals(toStopId)) {
                toLat = stop.getStopLat();
                toLon = stop.getStopLon();
                count++;
            }

            // Calculate transfer time by distance (using normal walking speed)
            if (count > 1) {
                transferTime = (int) (GlobalHelper.computeGreatCircleDistance(fromLat, fromLon, toLat, toLon)/GlobalVariable.NORMAL_WALKING_SPEED);
                break;
            }
        }

        // If stop not found return null
        if (transferTime == -1) {
            transferTime = null;
        }

        return transferTime;
    }

    public Integer parseSaveData(String path, List<Stop> stopLocations) {

        getSession();
        int dataCount = 0;
        try (BufferedReader fileReader = new BufferedReader(new FileReader(path));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            // Delete all data in table
//            transferRepository.deleteAllData();

            Integer transferTime;
            for (CSVRecord csvRecord : csvParser) {

                // Count transfer time between stops
                if (csvParser.getHeaderMap().containsKey("min_transfer_time")) {
                    if (csvRecord.get("min_transfer_time").isEmpty()) {
                        transferTime = mapLocationTransferTime(stopLocations,
                                csvRecord.get("from_stop_id"),
                                csvRecord.get("to_stop_id"));
                    } else {
                        transferTime = Integer.parseInt(csvRecord.get("min_transfer_time"));
                    }
                } else {
                    transferTime = null;
                }

                Transfer transfer = new Transfer(
                        csvRecord.get("from_stop_id"),
                        csvRecord.get("to_stop_id"),
                        csvParser.getHeaderMap().containsKey("from_route_id") ? csvRecord.get("from_route_id") : "",
                        csvParser.getHeaderMap().containsKey("to_route_id") ? csvRecord.get("to_route_id") : "",
                        csvParser.getHeaderMap().containsKey("from_trip_id") ? csvRecord.get("from_trip_id") : "",
                        csvParser.getHeaderMap().containsKey("to_trip_id") ? csvRecord.get("to_trip_id") : "",
                        csvRecord.get("transfer_type").isEmpty() ? null : Integer.parseInt(csvRecord.get("transfer_type")),
                        transferTime
                );

                session.saveOrUpdate(transfer);
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
