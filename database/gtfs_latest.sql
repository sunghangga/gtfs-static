--
-- PostgreSQL database dump
--

-- Dumped from database version 13.2
-- Dumped by pg_dump version 13.2 (Ubuntu 13.2-1.pgdg20.04+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: postgis; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;


--
-- Name: EXTENSION postgis; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION postgis IS 'PostGIS geometry and geography spatial types and functions';


--
-- Name: connection_timetable(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.connection_timetable(param_stop_id character varying) RETURNS TABLE(stop_id character varying, date numeric, direction_id smallint, trip_id character varying, route_id character varying, agency_id character varying, stop_sequence integer, trip_headsign character varying, first_stop_id character varying, last_stop_id character varying, departure_time interval, arrival_time interval, transportmode character varying, route_long_name character varying, stop_name character varying, stop_lat double precision, stop_lon double precision, first_stop_name character varying, last_stop_name character varying, disabledaccessible character varying, visuallyaccessible character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY 
		 SELECT DISTINCT ON (s.stop_id) s.stop_id,
    cd.date,
    t.direction_id,
    t.trip_id,
    r.route_id,
    r.agency_id,
    st.stop_sequence,
    t.trip_headsign,
    first_s.stop_id AS first_stop_id,
    last_s.stop_id AS last_stop_id,
    st.departure_time,
    st.arrival_time,
    q.transportmode,
    r.route_long_name,
    s.stop_name,
    s.stop_lat,
    s.stop_lon,
    first_s.stop_name AS first_stop_name,
    last_s.stop_name AS last_stop_name,
    q.disabledaccessible,
    q.visuallyaccessible
   FROM stopplaces sp
     JOIN quays q ON sp.id = q.stopplace_id
     JOIN dataowner d ON q.quayownercode::text = d.daowcode::text
     JOIN stops s ON regexp_replace(q.quaycode::text, '^.*:'::text, ''::text) = s.stop_code::text
     JOIN stop_times st ON s.stop_id::text = st.stop_id::text
     JOIN trips t ON st.trip_id::text = t.trip_id::text
     JOIN routes r ON t.route_id::text = r.route_id::text
     JOIN stop_times first_st ON t.trip_id::text = first_st.trip_id::text
     JOIN stops first_s ON first_st.stop_id::text = first_s.stop_id::text
     JOIN stop_times last_st ON t.trip_id::text = last_st.trip_id::text
     JOIN stops last_s ON last_st.stop_id::text = last_s.stop_id::text
     JOIN calendar_dates cd ON t.service_id::text = cd.service_id::text
  WHERE q.quaystatus::text = 'available'::text AND first_st.stop_sequence = (( SELECT min(stop_times.stop_sequence) AS min
           FROM stop_times
          WHERE t.trip_id::text = stop_times.trip_id::text)) AND last_st.stop_sequence = (( SELECT max(stop_times.stop_sequence) AS max
           FROM stop_times
          WHERE t.trip_id::text = stop_times.trip_id::text)) AND sp.id = (( SELECT q_1.stopplace_id
           FROM stops s_1
             JOIN passengerstopassignment psa_1 ON s_1.stop_code::text = psa_1.userstopcode::text
             JOIN quays q_1 ON psa_1.quaycode::text = q_1.quaycode::text AND s_1.stop_id::text = param_stop_id
         LIMIT 1))
  order by s.stop_id, cd.date, st.arrival_time asc;
END; $$;


ALTER FUNCTION public.connection_timetable(param_stop_id character varying) OWNER TO postgres;

--
-- Name: next_bus_map_by_stop(character varying, character varying[], date, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.next_bus_map_by_stop(_stopcode character varying, _arrayserviceid character varying[], _tripstartdate date, _timezone character varying) RETURNS TABLE(route_short_name character varying, trip_schedule_relationship character varying, stop_schedule_relationship character varying, rounded_minute integer)
    LANGUAGE plpgsql
    AS $$
begin
	return query
	select
	x.route_short_name,
	x.trip_schedule_relationship,
	x.stop_schedule_relationship,
	cast(
		(case when extract(second from x.diff) > 40 then extract(minute from x.diff) + 1 else extract(minute from x.diff) end)
		+ (extract(hour from x.diff) * 60) + (extract(day from x.diff) * 24 * 60) as int
	) as rounded_minute
	
	from (
		select
		r.route_short_name,
		COALESCE(tu.schedule_relationship, '') as trip_schedule_relationship,
		COALESCE(stu.schedule_relationship, '') as stop_schedule_relationship,
		(_tripstartdate + st.departure_time) + (interval '1 seconds' * COALESCE(stu.departure_delay, 0)) as departure_date_time_delay,
		(_tripstartdate + st.departure_time) + (interval '1 seconds' * COALESCE(stu.departure_delay, 0)) - (timezone(_timezone, CURRENT_TIMESTAMP(0))) as diff
		
		from stops s
		join stop_times st on st.stop_id = s.stop_id
		join trips t on t.trip_id = st.trip_id
		join routes r on r.route_id = t.route_id
		
		left join trip_updates tu on tu.trip_id = t.trip_id
		left join stop_time_updates stu on stu.trip_update_id = tu.id and stu.stop_id = s.stop_id
		
		where st.pickup_type is distinct from '1'
		and st.drop_off_type is distinct from '1'
		and r.route_type = '3'
		and s.stop_code = _stopcode
		and t.service_id = any(_arrayserviceid)
	) as x
	where x.departure_date_time_delay >= timezone(_timezone, CURRENT_TIMESTAMP(0));
end;
$$;


ALTER FUNCTION public.next_bus_map_by_stop(_stopcode character varying, _arrayserviceid character varying[], _tripstartdate date, _timezone character varying) OWNER TO postgres;

--
-- Name: next_bus_per_route(character varying, character varying, character varying[], date, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.next_bus_per_route(_routeshortname character varying, _stopcode character varying, _arrayserviceid character varying[], _tripstartdate date, _timezone character varying) RETURNS TABLE(departure_date_time timestamp without time zone, departure_date_time_delay timestamp without time zone, trip_schedule_relationship character varying, stop_schedule_relationship character varying, rounded_minute integer)
    LANGUAGE plpgsql
    AS $$
begin
	return query
	select
	x.departure_date_time,
	x.departure_date_time_delay,
	x.trip_schedule_relationship,
	x.stop_schedule_relationship,
	cast(
		(case when extract(second from x.diff) > 40 then extract(minute from x.diff) + 1 else extract(minute from x.diff) end)
		+ (extract(hour from x.diff) * 60) + (extract(day from x.diff) * 24 * 60) as int
	) as rounded_minute
	
	from (
		select
		COALESCE(tu.schedule_relationship, '') as trip_schedule_relationship,
		COALESCE(stu.schedule_relationship, '') as stop_schedule_relationship,
		(_tripstartdate + st.departure_time) as departure_date_time,
		(_tripstartdate + st.departure_time) + (interval '1 seconds' * COALESCE(stu.departure_delay, 0)) as departure_date_time_delay,
		(_tripstartdate + st.departure_time) + (interval '1 seconds' * COALESCE(stu.departure_delay, 0)) - (timezone(_timezone, CURRENT_TIMESTAMP(0))) as diff
		
		from stop_times st
		join trips t on t.trip_id = st.trip_id
		join routes r on r.route_id = t.route_id
		join stops s on s.stop_id = st.stop_id
		
		left join trip_updates tu on tu.trip_id = t.trip_id
		left join stop_time_updates stu on stu.trip_update_id = tu.id and stu.stop_id = s.stop_id
		
		where st.pickup_type is distinct from '1'
		and st.drop_off_type is distinct from '1'
		and r.route_type = '3'
		and r.route_short_name = _routeshortname
		and s.stop_code = _stopcode
		and t.service_id = any(_arrayserviceid)
	) as x
	where x.departure_date_time_delay >= timezone(_timezone, CURRENT_TIMESTAMP(0));
end;
$$;


ALTER FUNCTION public.next_bus_per_route(_routeshortname character varying, _stopcode character varying, _arrayserviceid character varying[], _tripstartdate date, _timezone character varying) OWNER TO postgres;

--
-- Name: next_bus_per_trip_head_sign(character varying, character varying, character varying, character varying[], date, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.next_bus_per_trip_head_sign(_routeshortname character varying, _stopcode character varying, _tripheadsign character varying, _arrayserviceid character varying[], _tripstartdate date, _timezone character varying) RETURNS TABLE(departure_date_time timestamp without time zone, departure_date_time_delay timestamp without time zone, trip_schedule_relationship character varying, stop_schedule_relationship character varying, rounded_minute integer)
    LANGUAGE plpgsql
    AS $$
begin
	return query
	select
	x.departure_date_time,
	x.departure_date_time_delay,
	x.trip_schedule_relationship,
	x.stop_schedule_relationship,
	cast(
		(case when extract(second from x.diff) > 40 then extract(minute from x.diff) + 1 else extract(minute from x.diff) end)
		+ (extract(hour from x.diff) * 60) + (extract(day from x.diff) * 24 * 60) as int
	) as rounded_minute
	
	from (
		select
		COALESCE(tu.schedule_relationship, '') as trip_schedule_relationship,
		COALESCE(stu.schedule_relationship, '') as stop_schedule_relationship,
		(_tripstartdate + st.departure_time) as departure_date_time,
		(_tripstartdate + st.departure_time) + (interval '1 seconds' * COALESCE(stu.departure_delay, 0)) as departure_date_time_delay,
		(_tripstartdate + st.departure_time) + (interval '1 seconds' * COALESCE(stu.departure_delay, 0)) - (timezone(_timezone, CURRENT_TIMESTAMP(0))) as diff
		
		from stop_times st
		join trips t on t.trip_id = st.trip_id
		join routes r on r.route_id = t.route_id
		join stops s on s.stop_id = st.stop_id
		
		left join trip_updates tu on tu.trip_id = t.trip_id
		left join stop_time_updates stu on stu.trip_update_id = tu.id and stu.stop_id = s.stop_id
		
		where st.pickup_type is distinct from '1'
		and st.drop_off_type is distinct from '1'
		and r.route_type = '3'
		and r.route_short_name = _routeshortname
		and s.stop_code = _stopcode
		and t.trip_headsign = _tripheadsign
		and t.service_id = any(_arrayserviceid)
	) as x
	where x.departure_date_time_delay >= timezone(_timezone, CURRENT_TIMESTAMP(0));
end;
$$;


ALTER FUNCTION public.next_bus_per_trip_head_sign(_routeshortname character varying, _stopcode character varying, _tripheadsign character varying, _arrayserviceid character varying[], _tripstartdate date, _timezone character varying) OWNER TO postgres;

--
-- Name: vehicle_monitoring(character varying, bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.vehicle_monitoring(param_agency_id character varying, param_current_timestamp bigint) RETURNS TABLE(vehicle_label character varying, trip_start_date character varying, agency_id character varying, route_id character varying, route_long_name character varying, trip_id character varying, trip_headsign character varying, wheelchair_boarding integer, stop_id character varying, stop_name character varying, position_latitude double precision, position_longitude double precision, stop_sequence integer, first_stop_id character varying, first_stop_name character varying, last_stop_id character varying, last_stop_name character varying, aimed_departure_time interval, departure_delay integer, expected_departure_time bigint, aimed_arrival_time interval, arrival_delay integer, expected_arrival_time bigint, trip_schedule_relationship character varying, stop_schedule_relationship character varying, direction_id integer, current_status character varying, "timestamp" bigint)
    LANGUAGE plpgsql
    AS $$
begin
    return QUERY 
		  select
	vp.vehicle_label,
	vp.trip_start_date,
	r.agency_id,
	vp.route_id,
	r.route_long_name,
	tu.trip_id,
	t.trip_headsign,
	s.wheelchair_boarding,
	stu.stop_id,
	s.stop_name,
	vp.position_latitude,
	vp.position_longitude,
	stu.stop_sequence,
	first_s.stop_id as first_stop_id,
	first_s.stop_name as first_stop_name,
	last_s.stop_id as last_stop_id,
	last_s.stop_name as last_stop_name,
	st.departure_time as aimed_departure_time,
	stu.departure_delay,
	stu.departure_time as expected_departure_time,
	st.arrival_time as aimed_arrival_time,
	stu.arrival_delay,
	stu.arrival_time as expected_arrival_time,
	tu.schedule_relationship AS trip_schedule_relationship,
    stu.schedule_relationship AS stop_schedule_relationship,
	vp.direction_id,
	vp.current_status,
	vp."timestamp"
from
	vehicle_positions vp
join trip_updates tu on
	vp.trip_id::text = tu.trip_id::text
join stop_time_updates stu on
	tu.id = stu.trip_update_id
join trips t on
	tu.trip_id::text = t.trip_id::text
join routes r on
	t.route_id::text = r.route_id::text
join stop_times st on
	tu.trip_id::text = st.trip_id::text
	and stu.stop_id::text = st.stop_id::text
	and st.stop_sequence = stu.stop_sequence
join stops s on
	stu.stop_id::text = s.stop_id::text
join stop_times first_st on
	tu.trip_id::text = first_st.trip_id::text
join stops first_s on
	first_st.stop_id::text = first_s.stop_id::text
join stop_times last_st on
	tu.trip_id::text = last_st.trip_id::text
join stops last_s on
	last_st.stop_id::text = last_s.stop_id::text
where
	stu.stop_sequence >= vp.current_stop_sequence
	and vp."timestamp" >= param_current_timestamp
--	and (stu.arrival_time >= param_current_timestamp or stu.departure_time >= param_current_timestamp)
	and first_st.stop_sequence = (
	select
		min(stop_times.stop_sequence) as min
	from
		stop_times
	where
		tu.trip_id::text = stop_times.trip_id::text)
	and last_st.stop_sequence = (
	select
		max(stop_times.stop_sequence) as max
	from
		stop_times
	where
		tu.trip_id::text = stop_times.trip_id::text)
	and r.agency_id = param_agency_id
order by
	vp.vehicle_label,
	tu.id,
	stu.stop_sequence;
end;

$$;


ALTER FUNCTION public.vehicle_monitoring(param_agency_id character varying, param_current_timestamp bigint) OWNER TO postgres;

--
-- Name: vehicle_monitoring(character varying, character varying, bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.vehicle_monitoring(param_agency_id character varying, param_vehicle_id character varying, param_current_timestamp bigint) RETURNS TABLE(vehicle_label character varying, trip_start_date character varying, agency_id character varying, route_id character varying, route_long_name character varying, trip_id character varying, trip_headsign character varying, wheelchair_boarding integer, stop_id character varying, stop_name character varying, position_latitude double precision, position_longitude double precision, stop_sequence integer, first_stop_id character varying, first_stop_name character varying, last_stop_id character varying, last_stop_name character varying, aimed_departure_time interval, departure_delay integer, expected_departure_time bigint, aimed_arrival_time interval, arrival_delay integer, expected_arrival_time bigint, trip_schedule_relationship character varying, stop_schedule_relationship character varying, direction_id integer, current_status character varying, "timestamp" bigint)
    LANGUAGE plpgsql
    AS $$
begin
    return QUERY 
		  select
	vp.vehicle_label,
	vp.trip_start_date,
	r.agency_id,
	vp.route_id,
	r.route_long_name,
	tu.trip_id,
	t.trip_headsign,
	s.wheelchair_boarding,
	stu.stop_id,
	s.stop_name,
	vp.position_latitude,
	vp.position_longitude,
	stu.stop_sequence,
	first_s.stop_id as first_stop_id,
	first_s.stop_name as first_stop_name,
	last_s.stop_id as last_stop_id,
	last_s.stop_name as last_stop_name,
	st.departure_time as aimed_departure_time,
	stu.departure_delay,
	stu.departure_time as expected_departure_time,
	st.arrival_time as aimed_arrival_time,
	stu.arrival_delay,
	stu.arrival_time as expected_arrival_time,
	tu.schedule_relationship AS trip_schedule_relationship,
    stu.schedule_relationship AS stop_schedule_relationship,
	vp.direction_id,
	vp.current_status,
	vp."timestamp"
from
	vehicle_positions vp
join trip_updates tu on
	vp.trip_id::text = tu.trip_id::text
join stop_time_updates stu on
	tu.id = stu.trip_update_id
join trips t on
	tu.trip_id::text = t.trip_id::text
join routes r on
	t.route_id::text = r.route_id::text
join stop_times st on
	tu.trip_id::text = st.trip_id::text
	and stu.stop_id::text = st.stop_id::text
	and st.stop_sequence = stu.stop_sequence
join stops s on
	stu.stop_id::text = s.stop_id::text
join stop_times first_st on
	tu.trip_id::text = first_st.trip_id::text
join stops first_s on
	first_st.stop_id::text = first_s.stop_id::text
join stop_times last_st on
	tu.trip_id::text = last_st.trip_id::text
join stops last_s on
	last_st.stop_id::text = last_s.stop_id::text
where
	stu.stop_sequence >= vp.current_stop_sequence
	and vp."timestamp" >= param_current_timestamp
--	and (stu.arrival_time >= param_current_timestamp or stu.departure_time >= param_current_timestamp)
	and first_st.stop_sequence = (
	select
		min(stop_times.stop_sequence) as min
	from
		stop_times
	where
		tu.trip_id::text = stop_times.trip_id::text)
	and last_st.stop_sequence = (
	select
		max(stop_times.stop_sequence) as max
	from
		stop_times
	where
		tu.trip_id::text = stop_times.trip_id::text)
	and r.agency_id = param_agency_id
	and vp.vehicle_label = param_vehicle_id
order by
	vp.vehicle_label,
	tu.id,
	stu.stop_sequence;
end;

$$;


ALTER FUNCTION public.vehicle_monitoring(param_agency_id character varying, param_vehicle_id character varying, param_current_timestamp bigint) OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: agency; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.agency (
    agency_id character varying(255),
    agency_name character varying(255) NOT NULL,
    agency_url character varying(255) NOT NULL,
    agency_timezone character varying(50) NOT NULL,
    agency_lang character varying(20),
    agency_phone character varying(50),
    agency_fare_url character varying(255),
    agency_email character varying(255)
);


ALTER TABLE public.agency OWNER TO postgres;

--
-- Name: alerts; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.alerts (
    id bigint NOT NULL,
    start bigint,
    "end" bigint,
    cause character varying(20),
    effect character varying(20),
    url character varying(300),
    header_text character varying(1000),
    description_text character varying(4000),
    entity_id character varying(100),
    "timestamp" bigint
);


ALTER TABLE public.alerts OWNER TO postgres;

--
-- Name: alerts_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.alerts_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    CYCLE;


ALTER TABLE public.alerts_id_seq OWNER TO postgres;

--
-- Name: alerts_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.alerts_id_seq OWNED BY public.alerts.id;


--
-- Name: attributions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.attributions (
    attribution_id character varying(255),
    agency_id character varying(255),
    route_id character varying(255),
    trip_id character varying(255),
    organization_name character varying(255) NOT NULL,
    is_producer smallint,
    is_operator smallint,
    is_authority smallint,
    attribution_url character varying(255),
    attribution_email character varying(255),
    attribution_phone character varying(50)
);


ALTER TABLE public.attributions OWNER TO postgres;

--
-- Name: calendar; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.calendar (
    service_id character varying(255) NOT NULL,
    monday smallint,
    tuesday smallint,
    wednesday smallint,
    thursday smallint,
    friday smallint,
    saturday smallint,
    sunday smallint,
    start_date numeric(8,0),
    end_date numeric(8,0)
);


ALTER TABLE public.calendar OWNER TO postgres;

--
-- Name: calendar_dates; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.calendar_dates (
    service_id character varying(255) NOT NULL,
    date numeric(8,0) NOT NULL,
    exception_type integer NOT NULL
);


ALTER TABLE public.calendar_dates OWNER TO postgres;

--
-- Name: dataowner; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.dataowner (
    id integer NOT NULL,
    daowcode character varying(255),
    daowname character varying(255),
    daowtype character varying(255)
);


ALTER TABLE public.dataowner OWNER TO postgres;

--
-- Name: dataowner_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.dataowner_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    CYCLE;


ALTER TABLE public.dataowner_id_seq OWNER TO postgres;

--
-- Name: dataowner_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.dataowner_id_seq OWNED BY public.dataowner.id;


--
-- Name: direction_names_exceptions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.direction_names_exceptions (
    route_name character varying(255) NOT NULL,
    direction_id smallint NOT NULL,
    direction_name character varying(255),
    direction_do smallint
);


ALTER TABLE public.direction_names_exceptions OWNER TO postgres;

--
-- Name: directions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.directions (
    route_id character varying(255) NOT NULL,
    direction_id smallint NOT NULL,
    direction character varying(255),
    route_short_name character varying(255)
);


ALTER TABLE public.directions OWNER TO postgres;

--
-- Name: entity_selectors; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.entity_selectors (
    id bigint NOT NULL,
    agency_id character varying(15),
    route_id character varying(64),
    route_type integer,
    stop_id character varying(10),
    trip_id character varying(64),
    trip_route_id character varying(64),
    direction_id integer,
    trip_start_time character varying(8),
    trip_start_date character varying(10),
    schedule_relationship character varying(9),
    alert_id integer
);


ALTER TABLE public.entity_selectors OWNER TO postgres;

--
-- Name: entity_selectors_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.entity_selectors_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    CYCLE;


ALTER TABLE public.entity_selectors_id_seq OWNER TO postgres;

--
-- Name: entity_selectors_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.entity_selectors_id_seq OWNED BY public.entity_selectors.id;


--
-- Name: fare_attributes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.fare_attributes (
    fare_id character varying(255) NOT NULL,
    price numeric(10,2) NOT NULL,
    currency_type character varying(255) NOT NULL,
    payment_method smallint NOT NULL,
    transfers integer,
    agency_id character varying(255),
    transfer_duration integer
);


ALTER TABLE public.fare_attributes OWNER TO postgres;

--
-- Name: fare_rules; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.fare_rules (
    fare_id character varying(255) NOT NULL,
    route_id character varying(255),
    origin_id character varying(50),
    destination_id character varying(50),
    contains_id character varying(50)
);


ALTER TABLE public.fare_rules OWNER TO postgres;

--
-- Name: feed_info; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.feed_info (
    feed_id character varying(64),
    feed_publisher_name character varying(255) NOT NULL,
    feed_publisher_url character varying(255) NOT NULL,
    feed_lang character varying(20) NOT NULL,
    default_lang character varying(20),
    feed_start_date numeric(8,0),
    feed_end_date numeric(8,0),
    feed_version character varying(255),
    feed_contact_email character varying(255),
    feed_contact_url character varying(255)
);


ALTER TABLE public.feed_info OWNER TO postgres;

--
-- Name: frequencies; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.frequencies (
    trip_id character varying(255) NOT NULL,
    start_time interval NOT NULL,
    end_time interval NOT NULL,
    headway_secs integer NOT NULL,
    exact_times smallint
);


ALTER TABLE public.frequencies OWNER TO postgres;

--
-- Name: import; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.import (
    id integer NOT NULL,
    task_name character varying(255),
    file_name character varying(255),
    file_type character varying(255),
    status character varying(12),
    release_date timestamp(6) without time zone,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone
);


ALTER TABLE public.import OWNER TO postgres;

--
-- Name: import_detail; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.import_detail (
    id integer NOT NULL,
    last_state character varying(15),
    detail text,
    import_id integer,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone
);


ALTER TABLE public.import_detail OWNER TO postgres;

--
-- Name: import_detail_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.import_detail_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.import_detail_id_seq OWNER TO postgres;

--
-- Name: import_detail_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.import_detail_id_seq OWNED BY public.import_detail.id;


--
-- Name: import_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.import_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.import_id_seq OWNER TO postgres;

--
-- Name: import_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.import_id_seq OWNED BY public.import.id;


--
-- Name: levels; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.levels (
    level_id character varying(255) NOT NULL,
    level_index double precision NOT NULL,
    level_name character varying(255)
);


ALTER TABLE public.levels OWNER TO postgres;

--
-- Name: passengerstopassignment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.passengerstopassignment (
    id integer NOT NULL,
    quaycode character varying(255),
    dataownercode character varying(255),
    userstopcode character varying(255),
    validfrom timestamp without time zone,
    validthru timestamp without time zone
);


ALTER TABLE public.passengerstopassignment OWNER TO postgres;

--
-- Name: passengerstopassignment_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.passengerstopassignment_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    CYCLE;


ALTER TABLE public.passengerstopassignment_id_seq OWNER TO postgres;

--
-- Name: passengerstopassignment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.passengerstopassignment_id_seq OWNED BY public.passengerstopassignment.id;


--
-- Name: pathways; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pathways (
    pathway_id character varying(255) NOT NULL,
    from_stop_id character varying(255),
    to_stop_id character varying(255),
    pathway_mode integer NOT NULL,
    is_bidirectional smallint NOT NULL,
    length double precision,
    traversal_time integer,
    stair_count integer,
    max_slope double precision,
    min_width double precision,
    signposted_as character varying(255),
    reversed_signposted_as character varying(255)
);


ALTER TABLE public.pathways OWNER TO postgres;

--
-- Name: pattern_id; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pattern_id (
    trip_id character varying NOT NULL,
    pattern_id character varying NOT NULL,
    pattern_name character varying
);


ALTER TABLE public.pattern_id OWNER TO postgres;

--
-- Name: places; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.places (
    id integer NOT NULL,
    daowcode character varying(255),
    placecode character varying(255),
    publicname character varying(255),
    town character varying(255)
);


ALTER TABLE public.places OWNER TO postgres;

--
-- Name: places_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.places_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    CYCLE;


ALTER TABLE public.places_id_seq OWNER TO postgres;

--
-- Name: places_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.places_id_seq OWNED BY public.places.id;


--
-- Name: quays; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.quays (
    id integer NOT NULL,
    stopplace_id integer NOT NULL,
    quaycode character varying(255),
    quaytype character varying(255),
    transportmode character varying(255),
    quaystatus character varying(255),
    rd_x integer,
    rd_y integer,
    town character varying(255),
    level character varying(10),
    street character varying(255),
    location character varying(255),
    compassdirection integer,
    visuallyaccessible character varying(5),
    disabledaccessible character varying(5),
    onlygetout boolean,
    municipalitycode character varying(255),
    quayownercode character varying(255),
    concessionprovidercode character varying(255),
    quayname character varying(255),
    stopsidecode character varying(10),
    quayshapetype character varying(255),
    baylength double precision,
    markedkerb boolean,
    lift boolean,
    guidelines boolean,
    groundsurfaceindicator boolean,
    stopplaceaccessroute boolean,
    embaymentwidth double precision,
    bayentranceangles double precision,
    bayexitangles double precision,
    kerbheight double precision,
    boardingpositionwidth double precision,
    alightingpositionwidth double precision,
    liftedpartlength double precision,
    narrowestpassagewidth double precision,
    fulllengthguideline boolean,
    guidelinestopplaceconnection boolean,
    tactilegroundsurfaceindicator boolean,
    ramp boolean,
    ramplength double precision,
    heightwithenvironment double precision,
    rampwidth double precision,
    stopsign boolean,
    audiobutton boolean,
    stopsigntype character varying(255),
    shelter boolean,
    shelterpublicity boolean,
    illuminatedstop boolean,
    seatavailable boolean,
    leantosupport boolean,
    timetableinformation boolean,
    infounit boolean,
    routenetworkmap boolean,
    passengerinformationdisplay boolean,
    passengerinformationdisplaytype character varying(255),
    bicycleparking boolean,
    numberofbicycleplaces integer,
    bins boolean,
    ovccico boolean,
    ovccharging boolean,
    remarks text,
    roadcode character varying(255),
    hectometersign character varying(255),
    greenstop boolean,
    liftedbicyclepath boolean
);


ALTER TABLE public.quays OWNER TO postgres;

--
-- Name: quays_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.quays_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    CYCLE;


ALTER TABLE public.quays_id_seq OWNER TO postgres;

--
-- Name: quays_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.quays_id_seq OWNED BY public.quays.id;


--
-- Name: routes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.routes (
    route_id character varying(255) NOT NULL,
    agency_id character varying(255),
    route_short_name character varying(255),
    route_long_name character varying(255),
    route_desc character varying(255),
    route_type integer NOT NULL,
    route_url character varying(255),
    route_color character varying(6),
    route_text_color character varying(6),
    route_sort_order integer,
    continuous_pickup integer,
    continuous_drop_off integer
);


ALTER TABLE public.routes OWNER TO postgres;

--
-- Name: shapes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.shapes (
    shape_id character varying(255) NOT NULL,
    shape_pt_lat double precision NOT NULL,
    shape_pt_lon double precision NOT NULL,
    shape_pt_sequence integer NOT NULL,
    shape_dist_traveled double precision
);


ALTER TABLE public.shapes OWNER TO postgres;

--
-- Name: signup_periods; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.signup_periods (
    sign_id character varying NOT NULL,
    from_date bigint,
    to_date bigint
);


ALTER TABLE public.signup_periods OWNER TO postgres;

--
-- Name: stop_time_updates; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.stop_time_updates (
    id bigint NOT NULL,
    stop_sequence integer,
    stop_id character varying(30),
    arrival_delay integer,
    arrival_time bigint,
    arrival_uncertainty integer,
    departure_delay integer,
    departure_time bigint,
    departure_uncertainty integer,
    schedule_relationship character varying(9),
    trip_update_id bigint
);


ALTER TABLE public.stop_time_updates OWNER TO postgres;

--
-- Name: stop_times; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.stop_times (
    trip_id character varying(255) NOT NULL,
    arrival_time interval,
    departure_time interval,
    stop_id character varying(255) NOT NULL,
    stop_sequence integer NOT NULL,
    stop_headsign character varying(255),
    pickup_type integer,
    drop_off_type integer,
    continuous_pickup integer,
    continuous_drop_off integer,
    shape_dist_traveled double precision,
    fare_units_traveled double precision,
    timepoint smallint
);


ALTER TABLE public.stop_times OWNER TO postgres;

--
-- Name: stops; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.stops (
    stop_id character varying(255) NOT NULL,
    stop_code character varying(50),
    stop_name character varying(255),
    tts_stop_name character varying(255),
    stop_desc character varying(255),
    stop_lat double precision,
    stop_lon double precision,
    zone_id character varying(50),
    stop_url character varying(255),
    location_type integer,
    parent_station character varying(255),
    stop_timezone character varying(50),
    wheelchair_boarding integer,
    level_id character varying(255),
    platform_code character varying(50)
);


ALTER TABLE public.stops OWNER TO postgres;

--
-- Name: trip_updates; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.trip_updates (
    id bigint NOT NULL,
    trip_id character varying(64),
    route_id character varying(64),
    direction_id integer,
    trip_start_time character varying(8),
    trip_start_date character varying(10),
    schedule_relationship character varying(9),
    vehicle_id character varying(64),
    vehicle_label character varying(255),
    vehicle_license_plate character varying(10),
    delay integer,
    "timestamp" bigint,
    entity_id character varying(100)
);


ALTER TABLE public.trip_updates OWNER TO postgres;

--
-- Name: vehicle_positions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vehicle_positions (
    id bigint NOT NULL,
    trip_id character varying(64),
    route_id character varying(64),
    direction_id integer,
    trip_start_time character varying(8),
    trip_start_date character varying(10),
    schedule_relationship character varying(9),
    vehicle_id character varying(64),
    vehicle_label character varying(255),
    vehicle_license_plate character varying(10),
    position_latitude double precision,
    position_longitude double precision,
    position_bearing double precision,
    position_odometer double precision,
    position_speed double precision,
    current_stop_sequence integer,
    stop_id character varying(64),
    current_status character varying(15),
    occupancy_status character varying(27),
    congestion_level character varying(25),
    "timestamp" bigint,
    entity_id character varying(100)
);


ALTER TABLE public.vehicle_positions OWNER TO postgres;

--
-- Name: stop_monitoring; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.stop_monitoring AS
 SELECT COALESCE(vp.vehicle_label, tu.vehicle_label) AS vehicle_label,
    stu.stop_id,
    tu.trip_id,
    tu.direction_id,
    tu.trip_start_date,
    tu.trip_start_time,
    tu.route_id,
    r.agency_id,
    COALESCE(vp.current_stop_sequence, 0) AS current_stop_sequence,
    stu.stop_sequence,
    st_ori.stop_sequence AS first_stop_sequence,
    st_dest.stop_sequence AS last_stop_sequence,
    tu.schedule_relationship AS trip_schedule_relationship,
    stu.schedule_relationship AS stop_schedule_relationship,
    vp.current_status,
    s.stop_name,
    vp.occupancy_status,
    st.arrival_time AS aimed_arrival_time,
    stu.arrival_time AS expected_arrival_time,
    stu.arrival_delay,
    st.departure_time AS aimed_departure_time,
    stu.departure_time AS expected_departure_time,
    stu.departure_delay,
    st_ori.stop_id AS origin_stop_id,
    s_ori.stop_name AS origin_stop_name,
    st_dest.stop_id AS destination_stop_id,
    s_dest.stop_name AS destination_stop_name,
    r.route_long_name,
    vp.congestion_level,
    COALESCE(vp.position_longitude, (0)::double precision) AS position_longitude,
    COALESCE(vp.position_latitude, (0)::double precision) AS position_latitude,
    COALESCE(vp.position_bearing, (0)::double precision) AS position_bearing,
    s.stop_lon,
    s.stop_lat,
    tu."timestamp"
   FROM (((((((((public.trip_updates tu
     LEFT JOIN public.stop_time_updates stu ON ((stu.trip_update_id = tu.id)))
     LEFT JOIN public.vehicle_positions vp ON (((vp.trip_id)::text = (tu.trip_id)::text)))
     JOIN public.routes r ON (((r.route_id)::text = (tu.route_id)::text)))
     JOIN public.stop_times st ON ((((st.trip_id)::text = (tu.trip_id)::text) AND ((st.stop_id)::text = (stu.stop_id)::text) AND (st.stop_sequence = stu.stop_sequence))))
     JOIN public.stops s ON (((s.stop_id)::text = (stu.stop_id)::text)))
     JOIN public.stop_times st_ori ON (((st_ori.trip_id)::text = (tu.trip_id)::text)))
     JOIN public.stops s_ori ON (((s_ori.stop_id)::text = (st_ori.stop_id)::text)))
     JOIN public.stop_times st_dest ON (((st_dest.trip_id)::text = (tu.trip_id)::text)))
     JOIN public.stops s_dest ON (((s_dest.stop_id)::text = (st_dest.stop_id)::text)))
  WHERE (((stu.stop_sequence >= vp.current_stop_sequence) OR (vp.current_stop_sequence IS NULL)) AND (st_ori.stop_sequence = ( SELECT min(stop_times.stop_sequence) AS min
           FROM public.stop_times
          WHERE ((stop_times.trip_id)::text = (tu.trip_id)::text))) AND (st_dest.stop_sequence = ( SELECT max(stop_times.stop_sequence) AS max
           FROM public.stop_times
          WHERE ((stop_times.trip_id)::text = (tu.trip_id)::text))));


ALTER TABLE public.stop_monitoring OWNER TO postgres;

--
-- Name: stop_order_exceptions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.stop_order_exceptions (
    stop_id character varying(255) NOT NULL,
    route_name character varying(255),
    direction_name character varying(255),
    direction_do smallint,
    stop_name character varying(255),
    stop_do smallint
);


ALTER TABLE public.stop_order_exceptions OWNER TO postgres;

--
-- Name: stop_time_updates_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.stop_time_updates_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    CYCLE;


ALTER TABLE public.stop_time_updates_id_seq OWNER TO postgres;

--
-- Name: stop_time_updates_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.stop_time_updates_id_seq OWNED BY public.stop_time_updates.id;


--
-- Name: stopplaces; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.stopplaces (
    id integer NOT NULL,
    placecode character varying(255),
    stopplacecode character varying(255),
    stopplacetype character varying(255),
    publicname character varying(255),
    town character varying(255),
    street character varying(255),
    stopplacestatus character varying(255),
    uiccode bigint,
    level character varying(10),
    rd_x integer,
    rd_y integer,
    stopplaceownercode character varying(255),
    timetableinformation boolean,
    passengerinformationdisplay boolean,
    passengerinformationdisplaytype character varying(255),
    bicycleparking boolean,
    numberofbicycleplaces integer,
    toiletfacility boolean,
    ptbikerental boolean,
    bins boolean,
    ovccico boolean,
    ovccharging boolean
);


ALTER TABLE public.stopplaces OWNER TO postgres;

--
-- Name: stopplaces_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.stopplaces_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    CYCLE;


ALTER TABLE public.stopplaces_id_seq OWNER TO postgres;

--
-- Name: stopplaces_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.stopplaces_id_seq OWNED BY public.stopplaces.id;


--
-- Name: transfers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.transfers (
    from_stop_id character varying(255) NOT NULL,
    to_stop_id character varying(255) NOT NULL,
    from_route_id character varying(255),
    to_route_id character varying(255),
    from_trip_id character varying(255),
    to_trip_id character varying(255),
    transfer_type integer,
    min_transfer_time integer
);


ALTER TABLE public.transfers OWNER TO postgres;

--
-- Name: translations; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.translations (
    table_name character varying(255) NOT NULL,
    field_name character varying(255) NOT NULL,
    language character varying(20) NOT NULL,
    translation character varying(255) NOT NULL,
    record_id character varying(255),
    record_sub_id character varying(255),
    field_value character varying(255)
);


ALTER TABLE public.translations OWNER TO postgres;

--
-- Name: trip_updates_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.trip_updates_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    CYCLE;


ALTER TABLE public.trip_updates_id_seq OWNER TO postgres;

--
-- Name: trip_updates_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.trip_updates_id_seq OWNED BY public.trip_updates.id;


--
-- Name: trips; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.trips (
    trip_id character varying(255) NOT NULL,
    route_id character varying(255) NOT NULL,
    service_id character varying(255) NOT NULL,
    realtime_trip_id character varying(255),
    trip_headsign character varying(255),
    trip_short_name character varying(255),
    trip_long_name character varying(255),
    direction_id smallint,
    block_id character varying(255),
    shape_id character varying(255),
    wheelchair_accessible integer,
    bikes_allowed integer
);


ALTER TABLE public.trips OWNER TO postgres;

--
-- Name: tts_file; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tts_file (
    id integer NOT NULL,
    category character varying(255) NOT NULL,
    text text NOT NULL,
    file_name character varying(255) NOT NULL,
    expired_at timestamp(6) without time zone NOT NULL,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone
);


ALTER TABLE public.tts_file OWNER TO postgres;

--
-- Name: tts_file_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tts_file_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tts_file_id_seq OWNER TO postgres;

--
-- Name: tts_file_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tts_file_id_seq OWNED BY public.tts_file.id;


--
-- Name: vehicle_positions_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.vehicle_positions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    CYCLE;


ALTER TABLE public.vehicle_positions_id_seq OWNER TO postgres;

--
-- Name: vehicle_positions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.vehicle_positions_id_seq OWNED BY public.vehicle_positions.id;


--
-- Name: vehicles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vehicles (
    vehicle_id character varying NOT NULL
);


ALTER TABLE public.vehicles OWNER TO postgres;

--
-- Name: alerts id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.alerts ALTER COLUMN id SET DEFAULT nextval('public.alerts_id_seq'::regclass);


--
-- Name: dataowner id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dataowner ALTER COLUMN id SET DEFAULT nextval('public.dataowner_id_seq'::regclass);


--
-- Name: entity_selectors id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.entity_selectors ALTER COLUMN id SET DEFAULT nextval('public.entity_selectors_id_seq'::regclass);


--
-- Name: import id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.import ALTER COLUMN id SET DEFAULT nextval('public.import_id_seq'::regclass);


--
-- Name: import_detail id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.import_detail ALTER COLUMN id SET DEFAULT nextval('public.import_detail_id_seq'::regclass);


--
-- Name: passengerstopassignment id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.passengerstopassignment ALTER COLUMN id SET DEFAULT nextval('public.passengerstopassignment_id_seq'::regclass);


--
-- Name: places id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.places ALTER COLUMN id SET DEFAULT nextval('public.places_id_seq'::regclass);


--
-- Name: quays id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.quays ALTER COLUMN id SET DEFAULT nextval('public.quays_id_seq'::regclass);


--
-- Name: stop_time_updates id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.stop_time_updates ALTER COLUMN id SET DEFAULT nextval('public.stop_time_updates_id_seq'::regclass);


--
-- Name: stopplaces id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.stopplaces ALTER COLUMN id SET DEFAULT nextval('public.stopplaces_id_seq'::regclass);


--
-- Name: trip_updates id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trip_updates ALTER COLUMN id SET DEFAULT nextval('public.trip_updates_id_seq'::regclass);


--
-- Name: tts_file id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tts_file ALTER COLUMN id SET DEFAULT nextval('public.tts_file_id_seq'::regclass);


--
-- Name: vehicle_positions id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vehicle_positions ALTER COLUMN id SET DEFAULT nextval('public.vehicle_positions_id_seq'::regclass);


--
-- Name: agency agency_agency_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.agency
    ADD CONSTRAINT agency_agency_id_key UNIQUE (agency_id);


--
-- Name: alerts alerts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.alerts
    ADD CONSTRAINT alerts_pkey PRIMARY KEY (id);


--
-- Name: calendar_dates calendar_dates_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.calendar_dates
    ADD CONSTRAINT calendar_dates_pkey PRIMARY KEY (service_id, date);


--
-- Name: calendar calendar_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.calendar
    ADD CONSTRAINT calendar_pkey PRIMARY KEY (service_id);


--
-- Name: dataowner dataowner_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dataowner
    ADD CONSTRAINT dataowner_pkey PRIMARY KEY (id);


--
-- Name: direction_names_exceptions direction_names_exceptions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.direction_names_exceptions
    ADD CONSTRAINT direction_names_exceptions_pkey PRIMARY KEY (route_name, direction_id);


--
-- Name: directions directions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.directions
    ADD CONSTRAINT directions_pkey PRIMARY KEY (route_id, direction_id);


--
-- Name: entity_selectors entity_selectors_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.entity_selectors
    ADD CONSTRAINT entity_selectors_pkey PRIMARY KEY (id);


--
-- Name: fare_attributes fare_attributes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.fare_attributes
    ADD CONSTRAINT fare_attributes_pkey PRIMARY KEY (fare_id);


--
-- Name: feed_info feed_info_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.feed_info
    ADD CONSTRAINT feed_info_pkey PRIMARY KEY (feed_publisher_name);


--
-- Name: import_detail import_detail_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.import_detail
    ADD CONSTRAINT import_detail_pkey PRIMARY KEY (id);


--
-- Name: import import_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.import
    ADD CONSTRAINT import_pkey PRIMARY KEY (id);


--
-- Name: levels levels_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.levels
    ADD CONSTRAINT levels_pkey PRIMARY KEY (level_id);


--
-- Name: passengerstopassignment passengerstopassignment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.passengerstopassignment
    ADD CONSTRAINT passengerstopassignment_pkey PRIMARY KEY (id);


--
-- Name: pathways pathways_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pathways
    ADD CONSTRAINT pathways_pkey PRIMARY KEY (pathway_id);


--
-- Name: pattern_id pattern_id_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pattern_id
    ADD CONSTRAINT pattern_id_pkey PRIMARY KEY (trip_id, pattern_id);


--
-- Name: places places_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.places
    ADD CONSTRAINT places_pkey PRIMARY KEY (id);


--
-- Name: quays quays_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.quays
    ADD CONSTRAINT quays_pkey PRIMARY KEY (id);


--
-- Name: routes routes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.routes
    ADD CONSTRAINT routes_pkey PRIMARY KEY (route_id);


--
-- Name: shapes shapes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shapes
    ADD CONSTRAINT shapes_pkey PRIMARY KEY (shape_id, shape_pt_sequence);


--
-- Name: signup_periods signup_periods_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.signup_periods
    ADD CONSTRAINT signup_periods_pkey PRIMARY KEY (sign_id);


--
-- Name: stop_order_exceptions stop_order_exceptions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.stop_order_exceptions
    ADD CONSTRAINT stop_order_exceptions_pkey PRIMARY KEY (stop_id);


--
-- Name: stop_time_updates stop_time_updates_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.stop_time_updates
    ADD CONSTRAINT stop_time_updates_pkey PRIMARY KEY (id);


--
-- Name: stop_times stop_times_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.stop_times
    ADD CONSTRAINT stop_times_pkey PRIMARY KEY (trip_id, stop_sequence);


--
-- Name: stopplaces stopplaces_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.stopplaces
    ADD CONSTRAINT stopplaces_pkey PRIMARY KEY (id);


--
-- Name: stops stops_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.stops
    ADD CONSTRAINT stops_pkey PRIMARY KEY (stop_id);


--
-- Name: trip_updates trip_updates_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trip_updates
    ADD CONSTRAINT trip_updates_pkey PRIMARY KEY (id);


--
-- Name: trips trips_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trips
    ADD CONSTRAINT trips_pkey PRIMARY KEY (trip_id);


--
-- Name: tts_file tts_file_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tts_file
    ADD CONSTRAINT tts_file_pkey PRIMARY KEY (id);


--
-- Name: vehicle_positions vehicle_positions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vehicle_positions
    ADD CONSTRAINT vehicle_positions_pkey PRIMARY KEY (id);


--
-- Name: vehicles vehicles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vehicles
    ADD CONSTRAINT vehicles_pkey PRIMARY KEY (vehicle_id);


--
-- Name: shapes_shape_id_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX shapes_shape_id_idx ON public.shapes USING btree (shape_id);


--
-- Name: stop_time_updates_index1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX stop_time_updates_index1 ON public.stop_time_updates USING btree (trip_update_id, stop_id, stop_sequence, schedule_relationship);


--
-- Name: stop_times_arrival_time_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX stop_times_arrival_time_idx ON public.stop_times USING btree (stop_id, arrival_time, stop_sequence);


--
-- Name: trip_updates_trip_id_schedule_relationship_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX trip_updates_trip_id_schedule_relationship_idx ON public.trip_updates USING btree (trip_id, schedule_relationship);


--
-- Name: attributions attributions_agency_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.attributions
    ADD CONSTRAINT attributions_agency_id_fkey FOREIGN KEY (agency_id) REFERENCES public.agency(agency_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: attributions attributions_route_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.attributions
    ADD CONSTRAINT attributions_route_id_fkey FOREIGN KEY (route_id) REFERENCES public.routes(route_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: attributions attributions_trip_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.attributions
    ADD CONSTRAINT attributions_trip_id_fkey FOREIGN KEY (trip_id) REFERENCES public.trips(trip_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: entity_selectors entity_selectors_alert_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.entity_selectors
    ADD CONSTRAINT entity_selectors_alert_id_fkey FOREIGN KEY (alert_id) REFERENCES public.alerts(id) ON DELETE CASCADE;


--
-- Name: fare_attributes fare_attributes_agency_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.fare_attributes
    ADD CONSTRAINT fare_attributes_agency_id_fkey FOREIGN KEY (agency_id) REFERENCES public.agency(agency_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: fare_rules fare_rules_fare_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.fare_rules
    ADD CONSTRAINT fare_rules_fare_id_fkey FOREIGN KEY (fare_id) REFERENCES public.fare_attributes(fare_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: fare_rules fare_rules_route_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.fare_rules
    ADD CONSTRAINT fare_rules_route_id_fkey FOREIGN KEY (route_id) REFERENCES public.routes(route_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: frequencies frequencies_trip_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.frequencies
    ADD CONSTRAINT frequencies_trip_id_fkey FOREIGN KEY (trip_id) REFERENCES public.trips(trip_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: import_detail import_detail_import_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.import_detail
    ADD CONSTRAINT import_detail_import_id_fkey FOREIGN KEY (import_id) REFERENCES public.import(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: pathways pathways_from_stop_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pathways
    ADD CONSTRAINT pathways_from_stop_id_fkey FOREIGN KEY (from_stop_id) REFERENCES public.stops(stop_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: pathways pathways_to_stop_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pathways
    ADD CONSTRAINT pathways_to_stop_id_fkey FOREIGN KEY (to_stop_id) REFERENCES public.stops(stop_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: quays quays_stopplace_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.quays
    ADD CONSTRAINT quays_stopplace_id_fkey FOREIGN KEY (stopplace_id) REFERENCES public.stopplaces(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: routes routes_agency_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.routes
    ADD CONSTRAINT routes_agency_id_fkey FOREIGN KEY (agency_id) REFERENCES public.agency(agency_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: stop_time_updates stop_time_updates_trip_update_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.stop_time_updates
    ADD CONSTRAINT stop_time_updates_trip_update_id_fkey FOREIGN KEY (trip_update_id) REFERENCES public.trip_updates(id) ON DELETE CASCADE;


--
-- Name: stop_times stop_times_stop_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.stop_times
    ADD CONSTRAINT stop_times_stop_id_fkey FOREIGN KEY (stop_id) REFERENCES public.stops(stop_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: stop_times stop_times_trip_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.stop_times
    ADD CONSTRAINT stop_times_trip_id_fkey FOREIGN KEY (trip_id) REFERENCES public.trips(trip_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: transfers transfers_from_stop_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transfers
    ADD CONSTRAINT transfers_from_stop_id_fkey FOREIGN KEY (from_stop_id) REFERENCES public.stops(stop_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: transfers transfers_to_stop_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transfers
    ADD CONSTRAINT transfers_to_stop_id_fkey FOREIGN KEY (to_stop_id) REFERENCES public.stops(stop_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: trips trips_route_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trips
    ADD CONSTRAINT trips_route_id_fkey FOREIGN KEY (route_id) REFERENCES public.routes(route_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

