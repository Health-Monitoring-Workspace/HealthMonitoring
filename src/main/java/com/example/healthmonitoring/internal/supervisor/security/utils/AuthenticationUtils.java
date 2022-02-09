package com.example.healthmonitoring.internal.supervisor.security.utils;

import com.example.healthmonitoring.common.domain.entity.Credentials;
import com.example.healthmonitoring.internal.supervisor.dto.SupervisorDTO;
import lombok.experimental.UtilityClass;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static java.util.Optional.ofNullable;

@UtilityClass
public class AuthenticationUtils {
    public static SupervisorDTO getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return new SupervisorDTO();
        }
        return ofNullable(authentication)
                .map(Authentication::getPrincipal)
                .map(credentials->{
                    Credentials c = (Credentials) credentials;
                    SupervisorDTO toReturn = new SupervisorDTO();
                    toReturn.setId(c.getSupervisorId());
                    return toReturn;
                })
                .orElse(new SupervisorDTO());
    }

}
