package com.maestronic.gtfs.util;

import java.util.HashMap;
import java.util.Map;

public interface GlobalVariable {

    String XML_TYPE_DATA = "xml";
    String JSON_TYPE_DATA = "json";

    String GTFS_FILE_TYPE = "GTFS";
    String CHB_FILE_TYPE = "CHB";
    String PSA_FILE_TYPE = "PSA";

    String CHB_FILENAME = "ExportCHBLatest.xml";

    String INBOUND = "INBOUND";
    String OUTBOUND = "OUTBOUND";

    String STOPPED_AT = "STOPPED_AT";

    // Path variable
    String PATH_DUMMY_VM = "static/data/vehiclemonitoring.csv";
    String PATH_DUMMY_CT = "static/data/connectiontimetable.csv";

    // Import status
    String IMPORT_STATUS_IN_PROGRESS = "IN PROGRESS";
    String IMPORT_STATUS_SUCCEED = "SUCCEED";
    String IMPORT_STATUS_FAILED = "FAILED";

    // Import state
    String IMPORT_STATE_PREPARE = "PREPARE";
    String IMPORT_STATE_UPLOAD = "UPLOAD";
    String IMPORT_STATE_EXTRACT = "EXTRACT";
    String IMPORT_STATE_SAVE = "SAVE";
    String IMPORT_STATE_DELETE = "DELETE";
    String IMPORT_STATE_VALIDATE = "VALIDATE";
    String IMPORT_STATE_UNMARSHALL = "UNMARSHALL";
    String IMPORT_STATE_TERMINATE = "TERMINATE";

    // Import detail
    String IMPORT_DETAIL_PREPARE = "Preparing GTFS data.";
    String IMPORT_DETAIL_UPLOAD = "Uploading GTFS data.";
    String IMPORT_DETAIL_EXTRACT = "Extracting GTFS data.";
    String IMPORT_DETAIL_SAVE = "Parse and saving dataset into database.";
    String IMPORT_DETAIL_DELETE = "Deleting uploaded files.";
    String IMPORT_DETAIL_VALIDATE = "Validating GTFS data.";
    String IMPORT_DETAIL_UNMARSHALL = "Unmarshalling XML data.";

    // Filename
    String AGENCY = "agency.txt";
    String STOPS = "stops.txt";
    String ROUTES = "routes.txt";
    String TRIPS = "trips.txt";
    String STOP_TIMES = "stop_times.txt";
    String CALENDAR = "calendar.txt";
    String CALENDAR_DATES = "calendar_dates.txt";
    String FARE_ATTRIBUTES = "fare_attributes.txt";
    String FARE_RULES = "fare_rules.txt";
    String SHAPES = "shapes.txt";
    String TRANSFERS = "transfers.txt";
    String FEED_INFO = "feed_info.txt";
    String DIRECTION_NAMES_EXCEPTIONS = "direction_names_exceptions.txt";
    String DIRECTIONS = "directions.txt";
    String STOP_ORDER_EXCEPTIONS = "stop_order_exceptions.txt";
    String SIGNUP_PERIODS = "signup_periods.txt";
    String PATTERN_ID = "pattern_id.txt";
    String DATAOWNER = "dataowner";
    String PLACES = "places";
    String STOPPLACES = "stopplaces";
    String PASSENGERSTOPASSIGNMENT = "passengerstopassignment";

    // Trip planner
    int MAX_WALK_DISTANCE = 500;
    int DEFAULT_LIMIT_RESULT_TRIP_PLANNER = 5;
    String WALK_MODE = "walk";
    String DRIVE_MODE = "drive";
    Map<String, String> MODE = new HashMap<String, String>() {{
        put("walk", "Foot");
        put("0", "Tram");
        put("1", "Subway");
        put("2", "Rail");
        put("3", "Bus");
        put("4", "Ferry");
        put("5", "Cable Tram");
        put("6", "Aerial Lift");
        put("7", "Funicular");
        put("11", "Trolleybus");
        put("12", "Monorail");
    }};
    double NORMAL_WALKING_SPEED = 1.34112; // In meter per second (m/s)
    String DEPARTURE_TRIP = "departure";

    // Redis variable
    String PATH_DETAIL_WALK_KEY = "path_detail_walk";
    String PATH_DETAIL_DRIVE_KEY = "path_detail_drive";
}
