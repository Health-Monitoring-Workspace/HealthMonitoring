package com.example.healthmonitoring.internal.supervisor.security.utils;

import com.example.healthmonitoring.internal.supervisor.dto.SupervisorDTO;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class AuthenticationUtils {

    public static SupervisorDTO getLoggedInUser() {
        SupervisorDTO supervisorDTO = new SupervisorDTO();
        supervisorDTO.setId(UUID.fromString("1488ad1e-987f-11ec-b909-0242ac120002"));
        return supervisorDTO;
    }
}
