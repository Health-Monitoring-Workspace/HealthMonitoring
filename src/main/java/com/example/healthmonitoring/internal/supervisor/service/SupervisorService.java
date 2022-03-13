package com.example.healthmonitoring.internal.supervisor.service;

import com.example.healthmonitoring.common.domain.entity.Device;
import com.example.healthmonitoring.common.domain.entity.EmergencyContact;
import com.example.healthmonitoring.common.domain.entity.MedicalRecord;
import com.example.healthmonitoring.common.domain.entity.Patient;
import com.example.healthmonitoring.common.domain.entity.utility.PatientVitalSignsData;
import com.example.healthmonitoring.common.domain.repository.*;
import com.example.healthmonitoring.internal.supervisor.dto.PatientDTO;
import com.example.healthmonitoring.internal.supervisor.dto.SupervisorDTO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.example.healthmonitoring.common.domain.entity.utility.VitalSignType.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SupervisorService {

    PatientRepository patientRepository;

    EventRepository eventRepository;

    EmergencyContactRepository emergencyContactRepository;

    MedicalRecordRepository medicalRecordRepository;

    DeviceRepository deviceRepository;

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

    public Flux<PatientVitalSignsData> getPatientsData(@NotNull final SupervisorDTO principal) {
        return patientRepository.findAllBySupervisor(principal.getId())
                .collectList()
                .filter(CollectionUtils::isNotEmpty)
                .flatMapMany(patients -> Flux.fromStream(patients.stream().map(this::mapToPatientVitalsSignsData)));
    }

    private PatientVitalSignsData mapToPatientVitalsSignsData(Patient patient) {
        //TODO test if this works :)
        PatientVitalSignsData.PatientVitalSignsDataBuilder dataBuilder = PatientVitalSignsData.builder();
        deviceRepository.findByPatient(patient.getId())
                .doOnNext(device -> {
                    eventRepository.findFirstByDeviceIdAndVitalSignTypeOrderByCreatedAtDesc(device.getId(), PULSE)
                            .flatMap(pulse -> {
                                dataBuilder.pulseRate(Integer.valueOf(pulse.getData()));
                                return Mono.just(device);
                            })
                            .flatMap(d -> eventRepository.findFirstByDeviceIdAndVitalSignTypeOrderByCreatedAtDesc(device.getId(), BLOOD_PRESSURE)
                                    .flatMap(blood_pressure -> {
                                        dataBuilder.bloodPressure(blood_pressure.getData());
                                        return Mono.just(device);
                                    }))
                            .flatMap(d -> eventRepository.findFirstByDeviceIdAndVitalSignTypeOrderByCreatedAtDesc(device.getId(), OXYGEN_IN_BLOOD)
                                    .flatMap(oxygen -> {
                                        dataBuilder.oxygenLevel(Integer.valueOf(oxygen.getData()));
                                        return Mono.just(device);
                                    }))
                            .flatMap(d -> eventRepository.findFirstByDeviceIdAndVitalSignTypeOrderByCreatedAtDesc(device.getId(), BODY_TEMPERATURE)
                                    .flatMap(temp -> {
                                        dataBuilder.bodyTemperature(Double.valueOf(temp.getData()));
                                        return Mono.just(dataBuilder);
                                    }))
                            .then();
                }).subscribe();
        return dataBuilder.build();
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
