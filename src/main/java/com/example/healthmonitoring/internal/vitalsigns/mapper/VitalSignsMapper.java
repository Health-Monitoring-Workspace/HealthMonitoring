package com.example.healthmonitoring.internal.vitalsigns.mapper;

import com.example.healthmonitoring.common.domain.entity.Event;
import com.example.healthmonitoring.internal.vitalsigns.dto.VitalSignsDTO;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.example.healthmonitoring.common.domain.entity.utility.VitalSignType.*;

@Slf4j
public class VitalSignsMapper {

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public static Flux<Event> mapVitalSignsDtoToEvents(VitalSignsDTO vitalSignsDTO, UUID deviceId) {
        log.info("Mapping vitalSignsDTO {} for deviceId {}", vitalSignsDTO, deviceId);
        return Flux.fromStream(
                Stream.of(
                                pulseEvent(vitalSignsDTO, deviceId),
                                bloodPressureEvent(vitalSignsDTO, deviceId),
                                oxygenInBloodEvent(vitalSignsDTO, deviceId),
                                bodyTemperatureEvent(vitalSignsDTO, deviceId)
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
                .data(df.format(vitalSignsDTO.getBodyTemperature()))
                .build())
                : Stream.empty();
    }

}
