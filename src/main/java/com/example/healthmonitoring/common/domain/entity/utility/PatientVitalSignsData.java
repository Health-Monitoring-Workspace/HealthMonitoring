package com.example.healthmonitoring.common.domain.entity.utility;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PatientVitalSignsData {

    String name;

    String age;

    Boolean isOnline;

    LocalDateTime lastSeen;

    String pulseRate;

    String oxygenLevel;

    String bloodPressure;

    String bodyTemperature;

    List<Alert> alerts;

}
