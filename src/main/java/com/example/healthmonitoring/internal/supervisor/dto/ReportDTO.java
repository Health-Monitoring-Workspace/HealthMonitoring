package com.example.healthmonitoring.internal.supervisor.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Builder(toBuilder = true)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@EqualsAndHashCode
public class ReportDTO {

    @NotBlank
    @NotNull
    String fullName;

    @NotNull
    Integer age;

    @NotNull
    LocalDate reportDate;

    String meanPulseRate;

    String meanOxygenLevel;

    String meanBloodPressure;

    String meanBodyTemperature;

}
