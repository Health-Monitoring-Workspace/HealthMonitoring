package com.example.healthmonitoring.common.domain.repository;

import com.example.healthmonitoring.common.domain.entity.Report;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.UUID;

public interface ReportsRepository extends ReactiveCrudRepository<Report, UUID> {

    Flux<Report> findAllByPatientAndDate(UUID patient, LocalDate date);
}
