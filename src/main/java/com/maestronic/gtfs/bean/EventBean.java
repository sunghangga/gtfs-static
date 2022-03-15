package com.maestronic.gtfs.bean;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

public class EventBean {

    @EventListener(ApplicationReadyEvent.class)
    public void appReadyMessage() {
        System.out.println("\nGTFS binding is ready to accept connection\n");
    }

}
