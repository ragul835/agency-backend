package com.agency.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AgencyBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgencyBackendApplication.class, args);
    }
}
