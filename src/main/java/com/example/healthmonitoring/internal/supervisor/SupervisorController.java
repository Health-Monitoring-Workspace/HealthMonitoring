package com.example.healthmonitoring.internal.supervisor;

import com.example.healthmonitoring.internal.supervisor.service.SupervisorDetailsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SupervisorController {

    SupervisorDetailsService detailsService;

    @GetMapping("/")
    public Mono<UserDetails> get() {
        return detailsService.findByUsername("patricia.burtic@gmail.com");
    }
}
