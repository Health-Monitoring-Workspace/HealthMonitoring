package com.example.healthmonitoring.internal.supervisor.mapper;

import com.example.healthmonitoring.common.domain.entity.Event;
import com.example.healthmonitoring.internal.supervisor.dto.VitalSignsDTO;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
public class VitalSignsMapper {

    private static final String PULSE = "PULSE";
    private static final String BLOOD_PRESSURE = "BLOOD_PRESSURE";
    private static final String OXYGEN_IN_BLOOD = "OXYGEN_IN_BLOOD";
    private static final String BODY_TEMPERATURE = "BODY_TEMPERATURE";

    public static Flux<Event> mapVitalSignsDtoToEvents(VitalSignsDTO vitalSignsDTO, UUID deviceId) {
        log.info("Mapping vitalSignsDTO {} for deviceId {}", vitalSignsDTO, deviceId);
        return Flux.fromStream(
                Stream.of(
                        pulseEvent(vitalSignsDTO, deviceId),
                        bloodPressureEvent(vitalSignsDTO, deviceId),
                        oxygenInBloodEvent(vitalSignsDTO, deviceId)
                )
                        .filter(Objects::nonNull)
                        .flatMap(Function.identity())
        );
    }

    private static Stream<Event> pulseEvent(VitalSignsDTO vitalSignsDTO, UUID deviceId) {
        return vitalSignsDTO.getPulseRate() != null
                ? Stream.of(Event.builder()
                .deviceId(deviceId)
                .vitalSignType(PULSE)
                .data(vitalSignsDTO.getPulseRate().toString())
                .build())
                : Stream.empty();
    }

    private static Stream<Event> bloodPressureEvent(VitalSignsDTO vitalSignsDTO, UUID deviceId) {
        return vitalSignsDTO.getBloodPressure() != null
                ? Stream.of(Event.builder()
                .deviceId(deviceId)
                .vitalSignType(BLOOD_PRESSURE)
                .data(vitalSignsDTO.getBloodPressure())
                .build())
                : Stream.empty();
    }

    private static Stream<Event> oxygenInBloodEvent(VitalSignsDTO vitalSignsDTO, UUID deviceId) {
        return vitalSignsDTO.getOxygenLevel() != null
                ? Stream.of(Event.builder()
                .deviceId(deviceId)
                .vitalSignType(OXYGEN_IN_BLOOD)
                .data(vitalSignsDTO.getOxygenLevel().toString())
                .build())
                : Stream.empty();
    }

    private static Stream<Event> bodyTemperatureEvent(VitalSignsDTO vitalSignsDTO, UUID deviceId) {
        return vitalSignsDTO.getBodyTemperature() != null
                ? Stream.of(Event.builder()
                .deviceId(deviceId)
                .vitalSignType(BODY_TEMPERATURE)
                .data(vitalSignsDTO.getBodyTemperature().toString())
                .build())
                : Stream.empty();
    }

}
