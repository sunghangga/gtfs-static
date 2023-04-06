package com.maestronic.gtfs.config;

import com.maestronic.gtfs.bean.EventBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfig {

    @Bean
    public EventBean getReadyEventBean() {
        return new EventBean();
    }
}
