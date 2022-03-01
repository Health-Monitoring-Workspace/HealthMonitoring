package com.example.healthmonitoring.common.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Table("devices")
@Builder(toBuilder = true)
@Value
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Device {

    @NotNull
    @Id
    UUID id;

    String imei;

    String brand;

    String model;

    UUID patient;

}
