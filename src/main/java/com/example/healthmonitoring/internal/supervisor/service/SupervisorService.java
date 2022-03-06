package com.example.healthmonitoring.internal.supervisor.service;

import com.example.healthmonitoring.common.domain.entity.Device;
import com.example.healthmonitoring.common.domain.entity.EmergencyContact;
import com.example.healthmonitoring.common.domain.entity.MedicalRecord;
import com.example.healthmonitoring.common.domain.entity.Patient;
import com.example.healthmonitoring.common.domain.repository.*;
import com.example.healthmonitoring.internal.supervisor.dto.PatientDTO;
import com.example.healthmonitoring.internal.supervisor.dto.SupervisorDTO;
import com.example.healthmonitoring.internal.supervisor.dto.VitalSignsDTO;
import com.example.healthmonitoring.internal.supervisor.mapper.VitalSignsMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SupervisorService {

    PatientRepository patientRepository;

    EmergencyContactRepository emergencyContactRepository;

    MedicalRecordRepository medicalRecordRepository;

    DeviceRepository deviceRepository;

    EventRepository eventRepository;

    public Mono<Boolean> addPatient(@NotNull PatientDTO patientDTO, @NotNull SupervisorDTO principal) {
        return
                patientRepository.save(
                        Patient.builder()
                                .name(patientDTO.getPatientFullName())
                                .CNP(patientDTO.getPatientCNP())
                                .email(patientDTO.getPatientEmail())
                                .homeAddress(patientDTO.getPatientHomeAddress())
                                .phoneNumber(patientDTO.getPatientPhoneNumber())
                                .birthDate(LocalDate.parse(patientDTO.getPatientBirthDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                                .supervisor(principal.getId())
                                .build()
                )
                        .flatMap(patient -> persistDetails(patient, patientDTO))
                        .then(Mono.just(Boolean.TRUE));
    }

    public Mono<Boolean> persistVitalSigns(@NotNull VitalSignsDTO vitalSignsDTO) {
        return deviceRepository.findByImei(vitalSignsDTO.getDeviceIMEI())
                .flatMap(device -> eventRepository.saveAll(VitalSignsMapper.mapVitalSignsDtoToEvents(vitalSignsDTO, device.getId())).then())
                .then(Mono.just(Boolean.TRUE));
    }

    private Mono<Patient> persistDetails(Patient patient, PatientDTO patientDTO) {
        return Mono.just(patient)
                .doOnNext(p -> {
                            log.info("Saving medical records for {}", patient);
                            medicalRecordRepository.save(
                                    MedicalRecord.builder()
                                            .patient(patient.getId())
                                            .details(patientDTO.getOtherDetails())
                                            .treatments(patientDTO.getTreatments())
                                            .diseases(patientDTO.getDiseases())
                                            .build()
                            ).subscribe(
                                    (n) -> {
                                    },
                                    (t) ->
                                            log.error(
                                                    "Exception while persisting medical records for user {}",
                                                    patient,
                                                    t));
                        }
                )
                .doOnNext(p -> {
                    log.info("Saving emergency contact for {}", patient);
                    emergencyContactRepository.save(
                            EmergencyContact.builder()
                                    .name(patientDTO.getEmergencyContactFullName())
                                    .phoneNumber(patientDTO.getEmergencyContactPhoneNumber())
                                    .relationship(patientDTO.getEmergencyContactRelationship())
                                    .patient(patient.getId())
                                    .build()
                    ).subscribe(
                            (n) -> {
                            },
                            (t) ->
                                    log.error(
                                            "Exception while persisting emergency contact for user {}",
                                            patient,
                                            t));
                })
                .doOnNext(p -> {
                    log.info("Saving device for {}", patient);
                    deviceRepository.save(
                            Device.builder()
                                    .imei(patientDTO.getDeviceIMEI())
                                    .brand(patientDTO.getDeviceBrand())
                                    .model(patientDTO.getDeviceModel())
                                    .patient(patient.getId())
                                    .build()
                    ).subscribe(
                            (n) -> {
                            },
                            (t) ->
                                    log.error(
                                            "Exception while persisting device for user {}",
                                            patient,
                                            t));
                });
    }


}
