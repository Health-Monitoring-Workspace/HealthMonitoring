package com.example.healthmonitoring.internal.vitalsigns;

import com.example.healthmonitoring.common.domain.entity.utility.PatientVitalSignsData;
import com.example.healthmonitoring.internal.supervisor.dto.ReportDTO;
import com.example.healthmonitoring.internal.supervisor.security.utils.AuthenticationUtils;
import com.example.healthmonitoring.internal.supervisor.service.SupervisorService;
import com.example.healthmonitoring.internal.vitalsigns.dto.VitalSignsDTO;
import com.example.healthmonitoring.internal.vitalsigns.service.VitalSignsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VitalSignsController {

    VitalSignsService vitalSignsService;
    SupervisorService supervisorService;

    @PostMapping("/vital-signs")
    public Mono<Void> ingestVitalSigns(@RequestBody VitalSignsDTO vitalSignsDTO) {
        log.info("Trying to ingest vitals {}", vitalSignsDTO);
        return vitalSignsService.persistVitalSigns(vitalSignsDTO);
    }

    @GetMapping("/patients")
    public Flux<PatientVitalSignsData> patientVitalSignsDataFlux() {
        log.info("Returining patients data for supervisor {}", AuthenticationUtils.getLoggedInUser().getId());
        return supervisorService.getPatientsData(AuthenticationUtils.getLoggedInUser());
    }

    @GetMapping("/reports")
    public Flux<ReportDTO> reportDTOFlux() {
        log.info("Returining patients data for supervisor {}", AuthenticationUtils.getLoggedInUser().getId());
        return supervisorService.getReportsForDate(LocalDate.of(2022, 3, 10), AuthenticationUtils.getLoggedInUser()).distinct();
    }

}
