package com.example.healthmonitoring.internal.supervisor.security;

import com.example.healthmonitoring.common.domain.repository.CredentialsRepository;
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
public class CredentialsService implements ReactiveUserDetailsService {

    CredentialsRepository credentialsRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return credentialsRepository.findByUsername(username);
    }
}
