package com.example.healthmonitoring.common.domain.repository;

import com.example.healthmonitoring.common.domain.entity.EmergencyContact;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface EmergencyContactRepository extends ReactiveCrudRepository<EmergencyContact, UUID> {
}
