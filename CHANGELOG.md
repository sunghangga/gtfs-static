# Changelog

All notable changes to this application will be documented in this file.

## Note

- This application used for import GTFS data to the database and provide API.

## [2.1.0] - 2022-04-12

#### Updated

- Add parameter current time for Vehicle Moniroting API
- Change data type id sequence in database from serial to bigserial for realtime data
- Delete unused log
- Add Scheduled Relationship status in API VM (Vehicle Monitoring)

## [2.0.1] - 2022-03-30

#### Added

- Import option for VIA and Translink GTFS data

#### Updated

- Update all API response for VIA and Translink GTFS data

## [2.0.0] - 2022-03-21

#### Added

- Docker running application option

#### Updated

- Improve query performance of GTFS Vehicle Monitoring API
- Improve query performance of GTFS Connection Timetable API

#### Fixed

- Error response XML for GTFS Vehicle Monitoring API

## [1.0.0] - 2022-01-01

#### Added

- Import API GTFS data into database
- Import API CHB data into database
- Import API PSA data into database
- Vehicle Monitoring API
- Connection Timetable API