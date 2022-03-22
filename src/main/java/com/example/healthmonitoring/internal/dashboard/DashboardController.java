package com.example.healthmonitoring.internal.dashboard;

import com.example.healthmonitoring.common.domain.entity.utility.PatientVitalSignsData;
import com.example.healthmonitoring.internal.supervisor.security.utils.AuthenticationUtils;
import com.example.healthmonitoring.internal.supervisor.service.SupervisorService;
import com.example.healthmonitoring.internal.vitalsigns.service.VitalSignsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

@Slf4j
@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardController {

    VitalSignsService vitalSignsService;

    SupervisorService supervisorService;

    @GetMapping("")
    public String getDefault(Model model) {
        Flux<PatientVitalSignsData> patients = supervisorService.getPatientsData(AuthenticationUtils.getLoggedInUser());
        model.addAttribute("patients", patients);
        return "dashboard/patientcard";
    }

}
