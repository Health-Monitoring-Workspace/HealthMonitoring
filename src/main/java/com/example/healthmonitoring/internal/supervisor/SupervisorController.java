package com.example.healthmonitoring.internal.supervisor;

import com.example.healthmonitoring.internal.supervisor.dto.EditPatientDTO;
import com.example.healthmonitoring.internal.supervisor.dto.PatientDTO;
import com.example.healthmonitoring.internal.supervisor.security.utils.AuthenticationUtils;
import com.example.healthmonitoring.internal.supervisor.service.SupervisorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SupervisorController {

    SupervisorService supervisorService;

    @GetMapping("/")
    public Mono<String> login() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> {
                    if (authentication != null) {
                        return "redirect:/dashboard";
                    }
                    return "login";
                });
    }

    @GetMapping("/login")
    public Mono<String> redirectLogin(Model model) {
        return Mono.just("login");
    }

    @GetMapping("/add-patient")
    public Mono<String> addPatientForm(Model model) {
        model.addAttribute("patientForm", PatientDTO.builder().build());

        return Mono.just("addPatientPage/add-patient");
    }

    @PostMapping("/add-patient")
    public Mono<String> addPatientSubmission(@ModelAttribute("patientForm") PatientDTO patientDTO, Model model) {
        return AuthenticationUtils.getLoggedInUser()
                .flatMap(user ->
                        supervisorService.addPatient(patientDTO, user))
                .thenReturn("addPatientPage/add-patient");
    }

    @PostMapping("/edit-patient")
    public Mono<String> editPatientSubmission(@ModelAttribute("details") EditPatientDTO patientDTO, Model model) {
        return supervisorService.editPatientDetails(patientDTO)
                .thenReturn("redirect:/dashboard/patients/" + patientDTO.getPatientId());
    }

    @PostMapping("/delete-patient/{id}")
    public Mono<String> deletePatientSubmission(@PathVariable final UUID id, final Model model) {
        return supervisorService.deletePatient(id)
                .thenReturn("redirect:/dashboard");
    }

    @GetMapping("/profile")
    public Mono<String> supervisorProfile(final Model model) {
        return AuthenticationUtils.getLoggedInUser()
                .flatMap(user ->
                        supervisorService.supervisorProfile(user.getId()))
                .map(supervisor -> model.addAttribute("supervisor", supervisor))
                .thenReturn("supervisor/supervisorprofile");
    }

    @GetMapping("/about")
    public Mono<String> aboutPage(final Model model) {
        return Mono.just("general/about");
    }

    @GetMapping("/my-patients")
    public Mono<String> myPatients(final Model model) {
        return AuthenticationUtils.getLoggedInUser()
                .flatMapMany(user ->
                        supervisorService.findAllBySupervisor(user.getId()))
                .collectList()
                .map(patients -> model.addAttribute("patients", patients))
                .thenReturn("patients/my-patients");
    }

}
