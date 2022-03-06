package com.example.healthmonitoring.common.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("events")
@Builder(toBuilder = true)
@Value
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {

    @NotNull
    @Id
    UUID id;

    UUID deviceId;

    String vitalSignType;

    String data;

    @CreatedDate
    LocalDateTime createdAt;

}
