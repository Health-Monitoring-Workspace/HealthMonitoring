package com.example.healthmonitoring.internal.supervisor.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VitalSignsDTO {

    String deviceIMEI;

    Integer pulseRate;

    Integer oxygenLevel;

    String bloodPressure;

    Double bodyTemperature;

}

