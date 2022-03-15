package com.maestronic.gtfs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableAsync
public class GtfsApplication {

	public static void main(String[] args) {

		SpringApplication.run(GtfsApplication.class, args);
	}

}
