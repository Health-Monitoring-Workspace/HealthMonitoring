package com.example.healthmonitoring.internal.dashboard;

import com.example.healthmonitoring.internal.supervisor.security.utils.AuthenticationUtils;
import com.example.healthmonitoring.internal.supervisor.service.SupervisorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardController {

    SupervisorService supervisorService;

    @GetMapping("")
    public Mono<String> getDefault(Model model) {
        return supervisorService.getPatientsData(AuthenticationUtils.getLoggedInUser())
                .collectList()
                .map(patients -> model.addAttribute("patients", patients))
                .then(Mono.just("dashboard/patientcard"));
    }

}
