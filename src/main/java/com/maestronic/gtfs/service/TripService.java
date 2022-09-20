package com.maestronic.gtfs.service;

import com.maestronic.gtfs.dto.custom.TripDto;
import com.maestronic.gtfs.entity.Trip;
import com.maestronic.gtfs.enumerate.SortTypeEnum;
import com.maestronic.gtfs.repository.TripRepository;
import com.maestronic.gtfs.util.GlobalVariable;
import com.maestronic.gtfs.util.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

@Service
public class TripService {

    @PersistenceContext
    private EntityManager entityManager;
    private Session session;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;
    @Autowired
    private TripRepository tripRepository;

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
//            tripRepository.deleteAllData();

            for (CSVRecord csvRecord : csvParser) {
                Trip trip = new Trip(
                        csvRecord.get("trip_id"),
                        csvRecord.get("route_id"),
                        csvRecord.get("service_id"),
                        csvParser.getHeaderMap().containsKey("realtime_trip_id") ? csvRecord.get("realtime_trip_id") : null,
                        csvRecord.get("trip_headsign"),
                        csvRecord.get("trip_short_name"),
                        csvParser.getHeaderMap().containsKey("trip_long_name") ? csvRecord.get("trip_long_name") : null,
                        Integer.parseInt(csvRecord.get("direction_id")),
                        csvRecord.get("block_id"),
                        csvRecord.get("shape_id"),
                        Integer.parseInt(csvRecord.get("wheelchair_accessible")),
                        csvRecord.get("bikes_allowed").isEmpty() ? null : Integer.parseInt(csvRecord.get("bikes_allowed"))
                );

                session.saveOrUpdate(trip);
                // compare batch saved count
                dataCount++;
                checkBatchSize(dataCount);
            }

            session.flush();
            session.clear();
            Logger.info("Parse and save " + GlobalVariable.TRIPS + " file is complete.");
            return dataCount;
        } catch (Exception e) {
            String logMessage = "Fail to parse and save " + GlobalVariable.TRIPS + " file. " + e.getMessage();
            throw new RuntimeException(logMessage);
        }
    }

    public List<TripDto> getTrips(int pageNo, int pageSize, String sortType) {
        Sort sort;
        if (sortType.equals(SortTypeEnum.ASC.name())) {
            sort = Sort.by("tripId").ascending();
        } else {
            sort = Sort.by("tripId").descending();
        }
        Pageable pageSort = PageRequest.of(pageNo, pageSize, sort);
        return tripRepository.findAllTrips(pageSort);
    }

    public TripDto getTripByTripId(String tripId) {
        return tripRepository.findTripsByTripId(tripId);
    }
}
