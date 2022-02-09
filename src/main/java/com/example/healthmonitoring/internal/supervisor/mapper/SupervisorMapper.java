package com.example.healthmonitoring.internal.supervisor.mapper;

import com.example.healthmonitoring.common.domain.entity.Supervisor;
import com.example.healthmonitoring.internal.supervisor.dto.SupervisorDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import javax.validation.constraints.NotNull;

@Mapper
public interface SupervisorMapper {
    SupervisorMapper INSTANCE = Mappers.getMapper(SupervisorMapper.class);

    SupervisorDTO dtoFromSupervisor(@NotNull Supervisor supervisor);

    Supervisor supervisorFromDto(@NotNull SupervisorDTO dto);

}
