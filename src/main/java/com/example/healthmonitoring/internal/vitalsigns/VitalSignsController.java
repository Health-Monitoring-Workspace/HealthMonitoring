package com.example.healthmonitoring.internal.vitalsigns;

import com.example.healthmonitoring.internal.vitalsigns.dto.VitalSignsDTO;
import com.example.healthmonitoring.internal.vitalsigns.service.VitalSignsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/vital-signs")
    public Mono<Void> ingestVitalSigns(@RequestBody VitalSignsDTO vitalSignsDTO) {
        log.info("Trying to ingest vitals {}", vitalSignsDTO);
        return vitalSignsService.persistVitalSigns(vitalSignsDTO);
    }
}
