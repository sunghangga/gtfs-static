package com.maestronic.gtfs.util;

public interface GlobalVariable {

    String XML_TYPE_DATA = "xml";
    String JSON_TYPE_DATA = "json";

    String GTFS_FILE_TYPE = "GTFS";
    String CHB_FILE_TYPE = "CHB";
    String PSA_FILE_TYPE = "PSA";

    String CHB_FILENAME = "ExportCHBLatest.xml";

    String INBOUND = "INBOUND";
    String OUTBOUND = "OUTBOUND";

    String INCOMING_AT = "INCOMING_AT";
    String STOPPED_AT = "STOPPED_AT";
    String IN_TRANSIT_TO = "IN_TRANSIT_TO";

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
    String CALENDAR_DATES = "calendar_dates.txt";
    String SHAPES = "shapes.txt";
    String TRANSFERS = "transfers.txt";
    String FEED_INFO = "feed_info.txt";
    String DATAOWNER = "dataowner";
    String PLACES = "places";
    String STOPPLACES = "stopplaces";
    String PASSENGERSTOPASSIGNMENT = "passengerstopassignment";

    // XML properties
    String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
}
