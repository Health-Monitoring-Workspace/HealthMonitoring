package com.example.healthmonitoring.internal.vitalsigns;

import com.example.healthmonitoring.internal.supervisor.service.SupervisorService;
import com.example.healthmonitoring.internal.vitalsigns.dto.VitalSignsDTO;
import com.example.healthmonitoring.internal.vitalsigns.service.VitalSignsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VitalSignsController {

    VitalSignsService vitalSignsService;
//    SupervisorService supervisorService;

    @PostMapping(value = "/vital-signs", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Void> ingestVitalSigns(@RequestBody VitalSignsDTO vitalSignsDTO) {
        log.info("Trying to ingest vitals {}", vitalSignsDTO);
        return vitalSignsService.persistVitalSigns(vitalSignsDTO);
    }

//    @GetMapping("/patients")
//    public Flux<PatientVitalSignsData> patientVitalSignsDataFlux() {
//        log.info("Returning patients' data for supervisor {}", AuthenticationUtils.getLoggedInUser().getId());
//        return supervisorService.getPatientsData(AuthenticationUtils.getLoggedInUser());
//    }
//
//    @GetMapping("/reports")
//    public Flux<ReportDTO> reportDTOFlux() {
//        log.info("Returning patients' data for supervisor {}", AuthenticationUtils.getLoggedInUser().getId());
//        return supervisorService.getReportsForDate(LocalDate.of(2022, 3, 10), AuthenticationUtils.getLoggedInUser()).distinct();
//    }
//
//    @PostMapping(value = "/vs", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public Mono<Void> voidMono(@RequestBody Integer string) {
//        System.out.println(string);
//        return Mono.just(string).then();
//    }

}
