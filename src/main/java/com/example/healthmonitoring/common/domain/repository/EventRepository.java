package com.example.healthmonitoring.common.domain.repository;

import com.example.healthmonitoring.common.domain.entity.Event;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface EventRepository extends ReactiveCrudRepository<Event, UUID> {
}
