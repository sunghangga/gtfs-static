package com.maestronic.gtfs.repository;

import com.maestronic.gtfs.dto.custom.VehicleMonitoringDummyDto;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.ResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.Tuple;
import java.util.List;

@Repository
public class VehicleMonitoringDummyRepository {

    private final EntityManagerFactory emf;

    @Autowired
    public VehicleMonitoringDummyRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public List<VehicleMonitoringDummyDto> findDummyVehicleMonitoringByParam(String agencyId,
                                                                             int date,
                                                                             String day,
                                                                             String startTime,
                                                                             String endTime) {

        EntityManager em = emf.createEntityManager();

        // Get trip location
        String queryString = "select r.agency_id, " +
                "r.route_id, r.route_long_name, t.trip_id, t.trip_headsign, s.wheelchair_boarding, s.stop_id, " +
                "s.stop_name, st.stop_sequence, s.stop_lat, s.stop_lon,\n" +
                "first_s.stop_id as first_stop_id, first_s.stop_name as first_stop_name,\n" +
                "last_s.stop_id as last_stop_id, last_s.stop_name as last_stop_name,\n" +
                "cast(date_part('epoch', st.arrival_time) * INTERVAL '1 second' as varchar) as aimed_arrival_time, \n" +
                "cast(date_part('epoch', st.departure_time) * INTERVAL '1 second' as varchar) as aimed_departure_time, \n" +
                "t.direction_id\n" +
                "from routes r \n" +
                "inner join trips t on r.route_id = t.route_id\n" +
                "inner join (SELECT service_id FROM calendar\n" +
                "      WHERE start_date <= " + date + "\n" +
                "        AND end_date >= " + date + "\n" +
                "        AND " + day + " = 1\n" +
                "      UNION\n" +
                "        SELECT service_id FROM calendar_dates\n" +
                "          WHERE date = " + date + "\n" +
                "            AND exception_type = 1\n" +
                "      EXCEPT\n" +
                "        SELECT service_id FROM calendar_dates\n" +
                "          WHERE date = " + date + "\n" +
                "            AND exception_type = 2\n" +
                "   ) c on c.service_id = t.service_id\n" +
                "inner join stop_times st on t.trip_id = st.trip_id\n" +
                "inner join stops s on st.stop_id = s.stop_id \n" +
                "inner join stop_times first_st on t.trip_id = first_st.trip_id \n" +
                "inner join stops first_s on first_st.stop_id = first_s.stop_id \n" +
                "inner join stop_times last_st on t.trip_id = last_st.trip_id \n" +
                "inner join stops last_s on last_st.stop_id = last_s.stop_id\n" +
                "where first_st.stop_sequence = (\n" +
                "   select\n" +
                "       min(stop_times.stop_sequence) as min\n" +
                "   from\n" +
                "       stop_times\n" +
                "   where\n" +
                "       t.trip_id = stop_times.trip_id)\n" +
                "   and last_st.stop_sequence = (\n" +
                "   select\n" +
                "       max(stop_times.stop_sequence) as max\n" +
                "   from\n" +
                "       stop_times\n" +
                "   where\n" +
                "       t.trip_id = stop_times.trip_id)\n" +
                "   and st.departure_time >= cast('" + startTime + "' as interval)\n" +
                "   and st.arrival_time <= cast('" + endTime + "' as interval)\n" +
                "   and r.agency_id = '" + agencyId + "'\n" +
                "order by\n" +
                "   r.route_id,\n" +
                "   first_st.arrival_time,\n" +
                "   t.trip_id,\n" +
                "   st.stop_sequence asc";

        // Execute query builder
        Query query = em.createNativeQuery(queryString).unwrap(org.hibernate.query.Query.class).setResultTransformer(new ResultTransformer() {

            private static final long serialVersionUID = -1L;

            @Override
            public VehicleMonitoringDummyDto transformTuple(Object[] tuple, String[] aliases) {

                VehicleMonitoringDummyDto dto = new VehicleMonitoringDummyDto(
                        tuple[0].toString(),
                        tuple[1].toString(),
                        tuple[2].toString(),
                        tuple[3].toString(),
                        tuple[4].toString(),
                        Integer.parseInt(tuple[5].toString()),
                        tuple[6].toString(),
                        tuple[7].toString(),
                        Integer.parseInt(tuple[8].toString()),
                        (double) tuple[9],
                        (double) tuple[10],
                        tuple[11].toString(),
                        tuple[12].toString(),
                        tuple[13].toString(),
                        tuple[14].toString(),
                        tuple[15].toString(),
                        tuple[16].toString(),
                        Integer.parseInt(tuple[17].toString())
                );

                return dto;
            }

            @Override
            public List transformList(List collection) {
                return collection;
            }
        });

        List<VehicleMonitoringDummyDto> dataList = query.getResultList();

        em.close();

        return dataList;
    }
}
