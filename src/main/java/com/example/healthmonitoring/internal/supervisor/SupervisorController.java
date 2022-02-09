package com.example.healthmonitoring.internal.supervisor;

import com.example.healthmonitoring.internal.supervisor.security.utils.AuthenticationUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SupervisorController {

    @GetMapping("/")
    public String login() {
        log.info("Enter login with {}", AuthenticationUtils.getLoggedInUser().getId());
        if (AuthenticationUtils.getLoggedInUser().getId() != null) {
            return "/dashboard";
        }
        return "login";
    }
}
