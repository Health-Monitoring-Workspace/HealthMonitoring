package com.example.healthmonitoring.internal.supervisor.security.utils;

import com.example.healthmonitoring.common.domain.entity.Credentials;
import com.example.healthmonitoring.internal.supervisor.dto.SupervisorDTO;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

@UtilityClass
public class AuthenticationUtils {

    public static Mono<SupervisorDTO> getLoggedInUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> {
                    if (authentication != null) {
                        SupervisorDTO supervisorDTO = new SupervisorDTO();
                        Credentials credentials = (Credentials) authentication.getPrincipal();
                        supervisorDTO.setId(credentials.getSupervisorId());
                        return supervisorDTO;
                    }
                    return new SupervisorDTO();
                });
    }
}
