package com.example.healthmonitoring.common.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Table("emergency_contacts")
@Builder(toBuilder = true)
@Value
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmergencyContact {

    @NotNull
    @Id
    UUID id;

    String name;

    String phoneNumber;

    String relationship;

    UUID patient;

}
