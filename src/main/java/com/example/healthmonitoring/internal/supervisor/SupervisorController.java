package com.example.healthmonitoring.internal.supervisor;

import com.example.healthmonitoring.internal.supervisor.dto.PatientDTO;
import com.example.healthmonitoring.internal.supervisor.dto.VitalSignsDTO;
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
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SupervisorController {

    SupervisorService supervisorService;

    @GetMapping("/")
    public String login() {
        log.info("Enter login with {}", AuthenticationUtils.getLoggedInUser().getId());
        if (AuthenticationUtils.getLoggedInUser().getId() != null) {
            return "/dashboard";
        }
        return "login";
    }

    @GetMapping("/login")
    public String redirectLogin() {
        return "login";
    }

    @GetMapping("/addPatient")
    public String addPatientForm(Model model) {
        model.addAttribute("patientForm", PatientDTO.builder().build());

        return "addPatientPage/addPatient";
    }

    @PostMapping("/addPatient")
    public String addPatientSubmission(@ModelAttribute("patientForm") PatientDTO patientDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("patientForm", PatientDTO.builder().build());

            return "addPatientPage/addPatient";
        }
        supervisorService.addPatient(patientDTO, AuthenticationUtils.getLoggedInUser())
                .subscribe(
                        (n) -> {
                        },
                        (t) -> {
                            log.error(
                                    "Exception while persisting patient for user {}",
                                    patientDTO,
                                    t);
                            model.addAttribute("patientForm", PatientDTO.builder().build());
                        }

                );

        return "addPatientPage/addPatient";
    }

    @PostMapping("/vital-signs")
    public Mono<Boolean> ingestVitalSigns(@RequestBody VitalSignsDTO vitalSignsDTO) {
        log.info("Trying to ingest vitals {}", vitalSignsDTO);
        return supervisorService.persistVitalSigns(vitalSignsDTO);
    }

}
