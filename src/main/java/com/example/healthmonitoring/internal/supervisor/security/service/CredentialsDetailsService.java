package com.example.healthmonitoring.internal.supervisor.security.service;

import com.example.healthmonitoring.common.domain.repository.CredentialsRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CredentialsDetailsService implements UserDetailsService {

    CredentialsRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (repository.existsByUsername(username).block()) {
            return repository.findByUsername(username).block();
        } else {
            throw new UsernameNotFoundException("Bad credentials!");
        }
    }
}
