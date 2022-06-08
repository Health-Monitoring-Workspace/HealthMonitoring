package com.example.healthmonitoring.internal.dashboard;

import com.example.healthmonitoring.internal.supervisor.security.utils.AuthenticationUtils;
import com.example.healthmonitoring.internal.supervisor.service.SupervisorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardController {

    SupervisorService supervisorService;

    @GetMapping("")
    public Mono<String> getDefault(Model model) {
        return AuthenticationUtils.getLoggedInUser()
                .flatMapMany(supervisorService::getPatientsData)
                .collectList()
                .map(patients -> model.addAttribute("patients", patients))
                .then(Mono.just("dashboard/patientcard"));
    }


    @GetMapping("reports")
    public Mono<String> getReportsForDate(@RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, final Model model) {
        return AuthenticationUtils.getLoggedInUser()
                .flatMapMany(user ->
                        supervisorService.getReportsForDate(date == null ? LocalDate.now().minusDays(1) : date, user).distinct())
                .collectList()
                .map(reportDTOS -> {
                    model.addAttribute("reports", reportDTOS);
                    model.addAttribute("report_date", date == null ? LocalDate.now().minusDays(1) : date);
                    return reportDTOS;
                })
                .then(Mono.just("dashboard/reports"));
    }

    @GetMapping("/patients/{id}")
    public Mono<String> getPatientData(@PathVariable UUID id, final Model model) {
        return supervisorService.getPatientDetails(id)
                .map(details -> model.addAttribute("details", details))
                .then(Mono.just("dashboard/patientpage"));
    }


}
