package com.example.healthmonitoring.internal.supervisor.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Builder(toBuilder = true)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EditPatientDTO {

    @NotNull
    UUID patientId;

    @NotNull
    @NotBlank
    String patientEmail;

    @NotNull
    @NotBlank
    String patientPhoneNumber;

    @NotNull
    @NotBlank
    String patientHomeAddress;

    @NotNull
    UUID deviceId;

    @NotNull
    @NotBlank
    String deviceIMEI;

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

}
