package com.example.healthmonitoring.common.domain.repository;

import com.example.healthmonitoring.common.domain.entity.Patient;
import com.example.healthmonitoring.common.domain.entity.utility.PatientVitalSignsData;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface PatientRepository extends ReactiveCrudRepository<Patient, UUID> {

    Flux<Patient> findAllBySupervisor(UUID supervisorId);

    @Query("select * from patients_vital_signs_view where supervisor = :supervisorId")
    Flux<PatientVitalSignsData> getDataFromMaterializedView(final @Param("supervisorId") UUID supervisorId);

    @Query("refresh materialized view patients_vital_signs_view")
    @Scheduled(cron = "*/30 * * * * *")
    void refreshPatientDataView();

}
