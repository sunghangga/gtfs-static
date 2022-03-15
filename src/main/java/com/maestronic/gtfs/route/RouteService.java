package com.maestronic.gtfs.route;

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

@Service
public class RouteService {

    @PersistenceContext
    private EntityManager entityManager;
    private Session session;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;
    @Autowired
    private RouteRepository routeRepository;

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
            routeRepository.deleteAllData();

            for (CSVRecord csvRecord : csvParser) {
                Route route = new Route(
                        csvRecord.get("route_id"),
                        csvRecord.get("agency_id"),
                        csvRecord.get("route_short_name"),
                        csvRecord.get("route_long_name"),
                        csvRecord.get("route_desc"),
                        Integer.parseInt(csvRecord.get("route_type")),
                        csvRecord.get("route_url"),
                        csvRecord.get("route_color"),
                        csvRecord.get("route_text_color"),
                        csvParser.getHeaderMap().containsKey("route_sort_order") ? Integer.parseInt(csvRecord.get("route_sort_order")) : null,
                        csvParser.getHeaderMap().containsKey("continuous_pickup") ? Integer.parseInt(csvRecord.get("continuous_pickup")) : null,
                        csvParser.getHeaderMap().containsKey("continuous_drop_off") ? Integer.parseInt(csvRecord.get("continuous_drop_off")) : null
                );

                session.save(route);
                // compare batch saved count
                dataCount++;
                checkBatchSize(dataCount);
            }

            session.flush();
            session.clear();
            Logger.info("Parse and save " + GlobalVariable.ROUTES + " file is complete.");
            return dataCount;
        } catch (Exception e) {
            String logMessage = "Fail to parse and save " + GlobalVariable.ROUTES + " file. " + e.getMessage();
            throw new RuntimeException(logMessage);
        }
    }
}
