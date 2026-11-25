package com.readingapp.reading_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class ReadingAppApplication {

	public static final String ZONA_HORARIA = "Europe/Madrid";

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone(ZONA_HORARIA));
		SpringApplication.run(ReadingAppApplication.class, args);
	}

}
