package com.maestronic.gtfs.service;

import com.maestronic.gtfs.entity.*;
import com.maestronic.gtfs.repository.ImportDetailRepository;
import com.maestronic.gtfs.repository.ImportRepository;
import com.maestronic.gtfs.util.GlobalVariable;
import com.maestronic.gtfs.util.Logger;
import nl.connekt.bison.chb.Export;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ImportTransactionService implements GlobalVariable {

    @Autowired
    private AgencyService agencyService;
    @Autowired
    private CalendarService calendarService;
    @Autowired
    private CalendarDateService calendarDateService;
    @Autowired
    private FareRulesService fareRulesService;
    @Autowired
    private FareAttributesService fareAttributesService;
    @Autowired
    private FeedInfoService feedInfoService;
    @Autowired
    private RouteService routeService;
    @Autowired
    private ShapeService shapeService;
    @Autowired
    private StopTimeService stopTimeService;
    @Autowired
    private StopService stopService;
    @Autowired
    private TripService tripService;
    @Autowired
    private TransferService transferService;
    @Autowired
    private DirectionNamesExceptionsService directionNamesExceptionsService;
    @Autowired
    private DirectionsService directionsService;
    @Autowired
    private StopOrderExceptionsService stopOrderExceptionsService;
    @Autowired
    private DataownerService dataOwnerService;
    @Autowired
    private PlaceService placeService;
    @Autowired
    private StopplaceService stopplaceService;
    @Autowired
    private PassengerStopAssignmentService passengerStopAssignmentService;
    @Autowired
    private ImportRepository importRepository;
    @Autowired
    private ImportDetailRepository importDetailRepository;
    private List<Stop> stopLocations = new ArrayList<>();

    /**
     * Parsing and save data with transactional (rollback purpose)
     * @param listPath
     * @param importComponent
     */
    public void parseSaveDataGtfs(ArrayList<String> listPath, ImportComponent importComponent) {
        // Initial variable
        int dataCount = 0;
        Map<String, String> fileInfo = new HashMap<>();

        try {
            for (String path: listPath) {
                fileInfo.put(path.substring(path.lastIndexOf(java.io.File.separator) + 1), path);
            }

            // Check if filepath contain agency data
            if (fileInfo.containsKey(AGENCY)) {
                dataCount += agencyService.parseSaveData(fileInfo.get(AGENCY));
                importComponent.getEntityList().add(Agency.TABLE_NAME);
            }
            // Check if filepath contain calendar data
            if (fileInfo.containsKey(CALENDAR)) {
                dataCount += calendarService.parseSaveData(fileInfo.get(CALENDAR));
                importComponent.getEntityList().add(Calendar.TABLE_NAME);
            }
            // Check if filepath contain calendar_dates
            if (fileInfo.containsKey(CALENDAR_DATES)) {
                dataCount += calendarDateService.parseSaveData(fileInfo.get(CALENDAR_DATES));
                importComponent.getEntityList().add(CalendarDate.TABLE_NAME);
            }
            // Check if filepath contain fare_attributes
            if (fileInfo.containsKey(FARE_ATTRIBUTES)) {
                dataCount += fareAttributesService.parseSaveData(fileInfo.get(FARE_ATTRIBUTES));
                importComponent.getEntityList().add(FareAttributes.TABLE_NAME);
            }
            // Check if filepath contain feed_info
            if (fileInfo.containsKey(FEED_INFO)) {
                dataCount += feedInfoService.parseSaveData(fileInfo.get(FEED_INFO));
                importComponent.getEntityList().add(FeedInfo.TABLE_NAME);
            }
            // Check if filepath contain routes
            if (fileInfo.containsKey(ROUTES)) {
                dataCount += routeService.parseSaveData(fileInfo.get(ROUTES));
                importComponent.getEntityList().add(Route.TABLE_NAME);
            }
            // Check if filepath contain fare_rules
            if (fileInfo.containsKey(FARE_RULES)) {
                dataCount += fareRulesService.parseSaveData(fileInfo.get(FARE_RULES));
                importComponent.getEntityList().add(FareRules.TABLE_NAME);
            }
            // Check if filepath contain shapes
            if (fileInfo.containsKey(SHAPES)) {
                dataCount += shapeService.parseSaveData(fileInfo.get(SHAPES));
                importComponent.getEntityList().add(Shape.TABLE_NAME);
            }
            // Check if filepath contain trips
            if (fileInfo.containsKey(TRIPS)) {
                dataCount += tripService.parseSaveData(fileInfo.get(TRIPS));
                importComponent.getEntityList().add(Trip.TABLE_NAME);
            }
            // Check if filepath contain stops
            if (fileInfo.containsKey(STOPS)) {
                dataCount += stopService.parseSaveData(fileInfo.get(STOPS), stopLocations);
                importComponent.getEntityList().add(Stop.TABLE_NAME);
            }
            // Check if filepath contain transfers
            if (fileInfo.containsKey(TRANSFERS)) {
                dataCount += transferService.parseSaveData(fileInfo.get(TRANSFERS), stopLocations);
                importComponent.getEntityList().add(Transfer.TABLE_NAME);
            }
            // Check if filepath contain stop_times
            if (fileInfo.containsKey(STOP_TIMES)) {
                dataCount += stopTimeService.parseSaveData(fileInfo.get(STOP_TIMES));
                importComponent.getEntityList().add(StopTime.TABLE_NAME);
            }
            // Check if filepath contain direction_names_exceptions
            if (fileInfo.containsKey(DIRECTION_NAMES_EXCEPTIONS)) {
                dataCount += directionNamesExceptionsService.parseSaveData(fileInfo.get(DIRECTION_NAMES_EXCEPTIONS));
                importComponent.getEntityList().add(DirectionNamesExceptions.TABLE_NAME);
            }
            // Check if filepath contain direction_names_exceptions
            if (fileInfo.containsKey(DIRECTIONS)) {
                dataCount += directionsService.parseSaveData(fileInfo.get(DIRECTIONS));
                importComponent.getEntityList().add(Directions.TABLE_NAME);
            }
            // Check if filepath contain direction_names_exceptions
            if (fileInfo.containsKey(STOP_ORDER_EXCEPTIONS)) {
                dataCount += stopOrderExceptionsService.parseSaveData(fileInfo.get(STOP_ORDER_EXCEPTIONS));
                importComponent.getEntityList().add(StopOrderExceptions.TABLE_NAME);
            }

            // Set save data count
            importComponent.setSaveCount(dataCount);
        } catch (Exception e) {
            // Rollback processed data
            throw new RuntimeException(e.getMessage());
        }
    }

    public void parseSaveDataChb(Export exportXml, ImportComponent importComponent) {
        // Initial variable
        int dataCount = 0;

        try {
            // Parse and save dataowner
            if (exportXml.getDataowners().size() > 0) {
                for (int i = 0; i < exportXml.getDataowners().size(); i++) {
                    dataCount += dataOwnerService.parseSaveData(exportXml.getDataowners().get(i).getDataowner());
                }
                importComponent.getEntityList().add(Dataowner.TABLE_NAME);
            }
            // Parse and save places
            if (exportXml.getPlaces().size() > 0) {
                for (int i = 0; i < exportXml.getPlaces().size(); i++) {
                    dataCount += placeService.parseSaveData(exportXml.getPlaces().get(i).getPlace());
                }
                importComponent.getEntityList().add(Place.TABLE_NAME);
            }
            // Parse and save stopplaces
            if (exportXml.getStopplaces().size() > 0) {
                for (int i = 0; i < exportXml.getStopplaces().size(); i++) {
                    dataCount += stopplaceService.parseSaveData(exportXml.getStopplaces().get(i).getStopplace());
                }
                importComponent.getEntityList().add(Place.TABLE_NAME);
            }

            // Set save data count
            importComponent.setSaveCount(dataCount);
        } catch (Exception e) {
            // Rollback processed data
            throw new RuntimeException(e.getMessage());
        }
    }

    public void parseSaveDataPsa(nl.connekt.bison.psa.Export exportXml, ImportComponent importComponent) {
        // Initial variable
        int dataCount = 0;

        try {
            // Parse and save dataowner
            if (exportXml.getQuays().getQuay().size() > 0) {
                dataCount += passengerStopAssignmentService.parseSaveData(exportXml.getQuays().getQuay());
                importComponent.getEntityList().add(PassengerStopAssignment.TABLE_NAME);
            }

            // Set save data count
            importComponent.setSaveCount(dataCount);
        } catch (Exception e) {
            // Rollback processed data
            throw new RuntimeException(e.getMessage());
        }
    }

    public void killRunProcByStatus() {
        // Get all in progress process
        List<Import> inProgressImport = importRepository.findByStatus(IMPORT_STATUS_IN_PROGRESS);
        // Check in progress import exists
        if (inProgressImport.isEmpty()) {
            return;
        }

        for (Import importData : inProgressImport ) {
            importData.setStatus(IMPORT_STATUS_FAILED);
            importData.setUpdatedAt(LocalDateTime.now());
            importRepository.save(importData);
            // Save import detail
            importDetailRepository.save(new ImportDetail(
                    IMPORT_STATE_TERMINATE,
                    "Fail to process next step (program terminated).",
                    importData,
                    LocalDateTime.now(),
                    LocalDateTime.now())
            );

            Logger.error("Terminate import process with ID " + importData.getId());
        }
    }
}
