package com.maestronic.gtfs.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Adapter1 extends XmlAdapter<String, ZonedDateTime> {
    public Adapter1() {
    }

    public ZonedDateTime unmarshal(String value) {
        return ZonedDateTimeAdapter.parse(value);
    }

    public String marshal(ZonedDateTime value) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(value);
    }
}

