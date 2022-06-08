package com.example.healthmonitoring.common.domain.repository;

import com.example.healthmonitoring.common.domain.entity.Event;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface EventRepository extends ReactiveCrudRepository<Event, UUID> {

    @Query("select e.* from events e inner join devices dev on dev.id = e.device_id inner join patients p on dev.patient = p.id where p.id = :patientId and e.created_at > now() - interval '20 minutes'")
    Flux<Event> getRecentEventsForPatient(final @Param("patientId") UUID patientId);

    Mono<Void> deleteAllByDeviceId(UUID deviceId);

    @Query("delete from events e where e.created_at::timestamp - now()::timestamp + 1 < 90")
    @Scheduled(cron = "59 23 * * *")
    Mono<Void> deleteDataOlderThan90Days();

}
