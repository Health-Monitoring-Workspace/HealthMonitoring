package com.example.healthmonitoring.common.domain.entity.utility;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder(toBuilder = true)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Alert {

    String message;

    AlertType alertType;

    Gravity gravity;

}
