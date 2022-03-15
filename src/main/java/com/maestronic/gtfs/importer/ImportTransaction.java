package com.maestronic.gtfs.importer;

import com.maestronic.gtfs.agency.Agency;
import com.maestronic.gtfs.agency.AgencyService;
import com.maestronic.gtfs.calendardate.CalendarDate;
import com.maestronic.gtfs.calendardate.CalendarDateService;
import com.maestronic.gtfs.dataowner.Dataowner;
import com.maestronic.gtfs.dataowner.DataownerService;
import com.maestronic.gtfs.feedinfo.FeedInfo;
import com.maestronic.gtfs.feedinfo.FeedInfoService;
import com.maestronic.gtfs.passengerstopassignment.PassengerStopAssignment;
import com.maestronic.gtfs.passengerstopassignment.PassengerStopAssignmentService;
import com.maestronic.gtfs.place.Place;
import com.maestronic.gtfs.place.PlaceService;
import com.maestronic.gtfs.route.Route;
import com.maestronic.gtfs.route.RouteService;
import com.maestronic.gtfs.shape.Shape;
import com.maestronic.gtfs.shape.ShapeService;
import com.maestronic.gtfs.stop.Stop;
import com.maestronic.gtfs.stop.StopService;
import com.maestronic.gtfs.stopplace.StopplaceService;
import com.maestronic.gtfs.stoptime.StopTime;
import com.maestronic.gtfs.stoptime.StopTimeService;
import com.maestronic.gtfs.transfer.Transfer;
import com.maestronic.gtfs.transfer.TransferService;
import com.maestronic.gtfs.trip.Trip;
import com.maestronic.gtfs.trip.TripService;
import com.maestronic.gtfs.util.GlobalVariable;
import com.maestronic.gtfs.util.Logger;
import nl.connekt.bison.chb.Export;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class ImportTransaction implements GlobalVariable {

    @Autowired
    private AgencyService agencyService;
    @Autowired
    private CalendarDateService calendarDateService;
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
    private DataownerService dataOwnerService;
    @Autowired
    private PlaceService placeService;
    @Autowired
    private StopplaceService stopplaceService;
    @Autowired
    private PassengerStopAssignmentService passengerStopAssignmentService;
    @Autowired
    private ImportRepository importRepository;

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
            // Check if filepath contain calendar_dates
            if (fileInfo.containsKey(CALENDAR_DATES)) {
                dataCount += calendarDateService.parseSaveData(fileInfo.get(CALENDAR_DATES));
                importComponent.getEntityList().add(CalendarDate.TABLE_NAME);
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
                dataCount += stopService.parseSaveData(fileInfo.get(STOPS));
                importComponent.getEntityList().add(Stop.TABLE_NAME);
            }
            // Check if filepath contain transfers
            if (fileInfo.containsKey(TRANSFERS)) {
                dataCount += transferService.parseSaveData(fileInfo.get(TRANSFERS));
                importComponent.getEntityList().add(Transfer.TABLE_NAME);
            }
            // Check if filepath contain stop_times
            if (fileInfo.containsKey(STOP_TIMES)) {
                dataCount += stopTimeService.parseSaveData(fileInfo.get(STOP_TIMES));
                importComponent.getEntityList().add(StopTime.TABLE_NAME);
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
            importData.setDetail(importData.getDetail() + " Fail to process (program terminated).");
            importData.setUpdatedAt(LocalDateTime.now());
            importRepository.save(importData);

            Logger.error("Terminate import process with ID " + importData.getId());
        }
    }
}
