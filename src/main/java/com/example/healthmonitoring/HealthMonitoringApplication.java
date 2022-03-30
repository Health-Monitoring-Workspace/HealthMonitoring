package com.example.healthmonitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

@EnableR2dbcAuditing
@SpringBootApplication(exclude= {UserDetailsServiceAutoConfiguration.class})
public class HealthMonitoringApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthMonitoringApplication.class, args);
    }

}
