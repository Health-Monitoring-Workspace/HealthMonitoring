package com.example.healthmonitoring.common.domain.repository;

import com.example.healthmonitoring.common.domain.entity.Supervisor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SupervisorRepository extends ReactiveCrudRepository<Supervisor, UUID> {

    Mono<UserDetails> findByEmail(String username);

}
