package com.example.healthmonitoring.internal.supervisor;

import com.example.healthmonitoring.internal.supervisor.dto.PatientDTO;
import com.example.healthmonitoring.internal.supervisor.security.utils.AuthenticationUtils;
import com.example.healthmonitoring.internal.supervisor.service.SupervisorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SupervisorController {

    SupervisorService supervisorService;

    @GetMapping("/")
    public Mono<String> login() {
        log.info("Enter login with {}", AuthenticationUtils.getLoggedInUser().getId());
        if (AuthenticationUtils.getLoggedInUser().getId() != null) {
            return Mono.just("redirect:/dashboard");
        }
        return Mono.just("general/login");
    }

    @GetMapping("/login")
    public Mono<String> redirectLogin(Model model) {
        return Mono.just("general/login");
    }

    @GetMapping("/add-patient")
    public Mono<String> addPatientForm(Model model) {
        model.addAttribute("patientForm", PatientDTO.builder().build());

        return Mono.just("addPatientPage/add-patient");
    }

    @PostMapping("/add-patient")
    public Mono<String> addPatientSubmission(@ModelAttribute("patientForm") PatientDTO patientDTO, Model model) {
//        if (result.hasErrors()) {
//            model.addAttribute("patientForm", PatientDTO.builder().build());
//
//            return Mono.just("addPatientPage/add-patient");
//        }
        return supervisorService.addPatient(patientDTO, AuthenticationUtils.getLoggedInUser())
                .thenReturn("addPatientPage/add-patient");
    }
}
