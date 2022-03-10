package com.example.healthmonitoring.internal.vitalsigns.service;

import com.example.healthmonitoring.common.domain.repository.DeviceRepository;
import com.example.healthmonitoring.common.domain.repository.EventRepository;
import com.example.healthmonitoring.internal.vitalsigns.dto.VitalSignsDTO;
import com.example.healthmonitoring.internal.vitalsigns.mapper.VitalSignsMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VitalSignsService {

    DeviceRepository deviceRepository;

    EventRepository eventRepository;

    public Mono<Void> persistVitalSigns(@NotNull VitalSignsDTO vitalSignsDTO) {
        return deviceRepository.findByImei(vitalSignsDTO.getDeviceIMEI())
                .flatMap(device -> eventRepository.saveAll(VitalSignsMapper.mapVitalSignsDtoToEvents(vitalSignsDTO, device.getId())).then())
                .then();
    }

}
