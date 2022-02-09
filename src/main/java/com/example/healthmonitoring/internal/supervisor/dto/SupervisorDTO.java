package com.example.healthmonitoring.internal.supervisor.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupervisorDTO {

    UUID id;

    String fullName;

    String email;

    String title;
}
