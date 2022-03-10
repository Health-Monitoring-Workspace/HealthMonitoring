package com.example.healthmonitoring.internal.vitalsigns.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VitalSignsDTO {

    String deviceIMEI;

    Integer pulseRate;

    Integer oxygenLevel;

    String bloodPressure;

    Double bodyTemperature;

}

