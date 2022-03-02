package com.example.healthmonitoring.common.domain.repository;

import com.example.healthmonitoring.common.domain.entity.MedicalRecord;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface MedicalRecordRepository extends ReactiveCrudRepository<MedicalRecord, UUID> {
}