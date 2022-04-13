package com.maestronic.gtfs;

import com.maestronic.gtfs.service.TTSService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableAsync
public class GtfsApplication {

	public static void main(String[] args) {
		new TTSService().textToSpeech("Hello World");
		SpringApplication.run(GtfsApplication.class, args);
	}

}
