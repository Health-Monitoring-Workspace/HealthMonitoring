package com.example.healthmonitoring.internal.supervisor.service;

import com.example.healthmonitoring.common.domain.repository.SupervisorRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SupervisorDetailsService implements ReactiveUserDetailsService {

    SupervisorRepository supervisorRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return supervisorRepository.findByEmail(username);
    }
}
