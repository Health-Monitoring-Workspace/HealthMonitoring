package com.example.healthmonitoring.common.domain.entity.utility;

import com.example.healthmonitoring.common.domain.entity.Event;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@ToString
public class PatientDetailsDTO {

    @NotNull
    UUID patientId;

    @NotNull
    @NotBlank
    String patientFullName;

    @NotNull
    @NotBlank
    String patientEmail;

    @NotNull
    @NotBlank
    String patientPhoneNumber;

    @NotNull
    @NotBlank
    String patientCNP;

    @NotNull
    @NotBlank
    String patientHomeAddress;

    LocalDate patientBirthDate;

    @NotNull
    Integer patientAge;

    @NotNull
    @NotBlank
    String deviceImei;

    String deviceBrand;

    String deviceModel;

    String diseases;

    String treatments;

    String otherDetails;

    @NotNull
    @NotBlank
    String emergencyContactFullName;

    @NotNull
    @NotBlank
    String emergencyContactPhoneNumber;

    String emergencyContactRelationship;

    Boolean isOnline;

    LocalDateTime lastSeen;

    String pulseRate;

    String oxygenLevel;

    String bloodPressure;

    String bodyTemperature;

    List<Alert> alerts;

    List<Event> recentData;
}
