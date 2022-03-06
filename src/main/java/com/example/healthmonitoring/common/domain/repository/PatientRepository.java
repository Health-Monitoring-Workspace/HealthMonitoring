package com.example.healthmonitoring.common.domain.repository;

import com.example.healthmonitoring.common.domain.entity.Patient;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PatientRepository extends ReactiveCrudRepository<Patient, UUID> {
}
