package com.example.healthmonitoring.common.domain.repository;

import com.example.healthmonitoring.common.domain.entity.Event;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface EventRepository extends ReactiveCrudRepository<Event, UUID> {

    @Query("select e.* from events e inner join devices dev on dev.id = e.device_id inner join patients p on dev.patient = p.id where p.id = :patientId and e.created_at > now() - interval '20 minutes'")
    Flux<Event> getRecentEventsForPatient(final @Param("patientId") UUID patientId);

}
