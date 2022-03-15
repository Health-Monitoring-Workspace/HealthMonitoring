package com.example.healthmonitoring.common.domain.repository;

import com.example.healthmonitoring.common.domain.entity.Event;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface EventRepository extends ReactiveCrudRepository<Event, UUID> {

    Mono<Event> findFirstByDeviceIdAndVitalSignTypeOrderByCreatedAtDesc(UUID deviceID, String vitalSignType);

}
