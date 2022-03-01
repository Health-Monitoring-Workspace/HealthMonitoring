package com.example.healthmonitoring.common.domain.repository;

import com.example.healthmonitoring.common.domain.entity.Device;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface DeviceRepository extends ReactiveCrudRepository<Device, UUID> {
}
