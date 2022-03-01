package com.example.healthmonitoring.common.domain.repository;

import com.example.healthmonitoring.common.domain.entity.Credentials;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface CredentialsRepository extends ReactiveCrudRepository<Credentials, UUID> {

    Mono<UserDetails> findByUsername(final String username);

    Mono<Boolean> existsByUsername(final String username);

}
