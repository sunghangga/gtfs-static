package com.maestronic.gtfs.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = FeedInfo.TABLE_NAME)
public class FeedInfo {

    public static final String TABLE_NAME= "feed_info";

    @Id
    @Column(name = "feed_publisher_name")
    private String feedPublisherName;

    @Column(name = "feed_id")
    private String feedId;

    @Column(name = "feed_publisher_url")
    private String feedPublisherUrl;

    @Column(name = "feed_lang")
    private String feedLang;

    @Column(name = "feed_start_date")
    private Integer feedStartDate;

    @Column(name = "feed_end_date")
    private Integer feedEndDate;

    @Column(name = "feed_version")
    private String feedVersion;

    @Column(name = "feed_contact_email")
    private String feedContactEmail;

    @Column(name = "feed_contact_url")
    private String feedContactUrl;

    public FeedInfo() {
    }

    public FeedInfo(String feedId, String feedPublisherName, String feedPublisherUrl, String feedLang, Integer feedStartDate, Integer feedEndDate, String feedVersion, String feedContactEmail, String feedContactUrl) {
        this.feedId = feedId;
        this.feedPublisherName = feedPublisherName;
        this.feedPublisherUrl = feedPublisherUrl;
        this.feedLang = feedLang;
        this.feedStartDate = feedStartDate;
        this.feedEndDate = feedEndDate;
        this.feedVersion = feedVersion;
        this.feedContactEmail = feedContactEmail;
        this.feedContactUrl = feedContactUrl;
    }

    public String getFeedVersion() {
        return feedVersion;
    }
}
