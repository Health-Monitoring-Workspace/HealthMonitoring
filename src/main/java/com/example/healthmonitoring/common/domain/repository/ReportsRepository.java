package com.example.healthmonitoring.common.domain.repository;

import com.example.healthmonitoring.common.domain.entity.Report;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.UUID;

public interface ReportsRepository extends ReactiveCrudRepository<Report, UUID> {

    Flux<Report> findAllByPatientAndDate(UUID patient, LocalDate date);

    @Query("select * from generate_report(now()::date)")
    @Scheduled(cron = "59 23 * * *")
    void generateReport();


}
