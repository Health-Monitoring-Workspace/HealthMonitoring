package com.example.healthmonitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableR2dbcAuditing
@EnableWebFluxSecurity
@SpringBootApplication
public class HealthMonitoringApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthMonitoringApplication.class, args);
    }

}
