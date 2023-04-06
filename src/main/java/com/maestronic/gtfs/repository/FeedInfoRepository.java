package com.maestronic.gtfs.repository;

import com.maestronic.gtfs.entity.FeedInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedInfoRepository extends JpaRepository<FeedInfo, String> {
    @Modifying
    @Query(value = "TRUNCATE TABLE feed_info CASCADE", nativeQuery = true)
    void deleteAllData();

    FeedInfo findTop1ByOrderByFeedVersionDesc();
}
