package com.example.healthmonitoring.common.domain.entity;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Table("supervisors")
@Builder(toBuilder = true)
@Value
public class Supervisor {

    @NotNull
    @Id
    UUID id;

    @NotNull
    String fullName;

    @NotNull
    String email;
}
