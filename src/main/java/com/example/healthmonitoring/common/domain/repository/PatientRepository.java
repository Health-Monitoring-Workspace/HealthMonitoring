package com.example.healthmonitoring.common.domain.repository;

import com.example.healthmonitoring.common.domain.entity.Patient;
import com.example.healthmonitoring.common.domain.entity.utility.PatientDetailsDTO;
import com.example.healthmonitoring.common.domain.entity.utility.PatientVitalSignsData;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Lazy(false)
@Component
public interface PatientRepository extends ReactiveCrudRepository<Patient, UUID> {

    Flux<Patient> findAllBySupervisor(UUID supervisorId);

    @Query("select * from patients_vital_signs_view where supervisor = :supervisorId")
    Flux<PatientVitalSignsData> getDataFromMaterializedView(final @Param("supervisorId") UUID supervisorId);

    @Query("select * from patients_details_view where patient_id = :patientId")
    Mono<PatientDetailsDTO> getPatientDetails(final @Param("patientId") UUID patientId);

}
