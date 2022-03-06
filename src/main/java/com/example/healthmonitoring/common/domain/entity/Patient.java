package com.example.healthmonitoring.common.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("patients")
@Builder(toBuilder = true)
@Value
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Patient {

    @NotNull
    @Id
    UUID id;

    String name;

    String email;

    String phoneNumber;

    String CNP;

    String homeAddress;

    LocalDate birthDate;

    UUID supervisor;

    @CreatedDate
    LocalDateTime createdAt;

}
