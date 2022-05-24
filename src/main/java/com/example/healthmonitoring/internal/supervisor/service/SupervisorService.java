package com.example.healthmonitoring.internal.supervisor.service;

import com.example.healthmonitoring.common.domain.entity.Device;
import com.example.healthmonitoring.common.domain.entity.EmergencyContact;
import com.example.healthmonitoring.common.domain.entity.MedicalRecord;
import com.example.healthmonitoring.common.domain.entity.Patient;
import com.example.healthmonitoring.common.domain.entity.utility.*;
import com.example.healthmonitoring.common.domain.repository.*;
import com.example.healthmonitoring.internal.supervisor.dto.EditPatientDTO;
import com.example.healthmonitoring.internal.supervisor.dto.PatientDTO;
import com.example.healthmonitoring.internal.supervisor.dto.ReportDTO;
import com.example.healthmonitoring.internal.supervisor.dto.SupervisorDTO;
import com.example.healthmonitoring.internal.supervisor.mapper.ReportMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SupervisorService {

    PatientRepository patientRepository;

    EmergencyContactRepository emergencyContactRepository;

    MedicalRecordRepository medicalRecordRepository;

    DeviceRepository deviceRepository;

    ReportsRepository reportsRepository;

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

    public Flux<PatientVitalSignsData> getPatientsData(@NotNull final SupervisorDTO principal) {
        return patientRepository
                .getDataFromMaterializedView(principal.getId())
                .flatMap(this::addAlerts)
                .sort(Comparator.comparing(PatientVitalSignsData::getName));
    }

    public Flux<ReportDTO> getReportsForDate(@NotNull LocalDate date, @NotNull SupervisorDTO principal) {
        Set<ReportDTO> reportDTOStream = new HashSet<>();
        return patientRepository.findAllBySupervisor(principal.getId())
                .flatMap(patient -> reportsRepository.findAllByPatientAndDate(patient.getId(), date)
                        .collectList()
                        .flatMap(reports -> ReportMapper.mapToDTO(reports, patient))
                        .map(reportDTOStream::addAll)
                        .thenReturn(Mono.just(Boolean.TRUE))
                )
                .flatMap(res -> Flux.fromStream(reportDTOStream.stream()))
                .sort(Comparator.comparing(ReportDTO::getFullName));
    }

    public Mono<PatientDetailsDTO> getPatientDetails(@NotNull UUID patientId) {
        return patientRepository.getPatientDetails(patientId)
                .flatMap(this::addAlerts)
                .flatMap(this::addRecentData);
    }

    public Mono<Void> editPatientDetails(@NotNull EditPatientDTO patientDetails) {
        return patientRepository.findById(patientDetails.getPatientId())
                .map(patient -> patient.toBuilder()
                        .email(patientDetails.getPatientEmail())
                        .phoneNumber(patientDetails.getPatientPhoneNumber())
                        .homeAddress(patientDetails.getPatientHomeAddress())
                        .build())
                .flatMap(patientRepository::save)
                .then(deviceRepository.findById(patientDetails.getDeviceId()))
                .map(device -> device.toBuilder()
                        .brand(patientDetails.getDeviceBrand())
                        .model(patientDetails.getDeviceModel())
                        .imei(patientDetails.getDeviceIMEI())
                        .build())
                .flatMap(deviceRepository::save)
                .then(medicalRecordRepository.findByPatient(patientDetails.getPatientId()))
                .map(medicalRecord -> medicalRecord.toBuilder()
                        .diseases(patientDetails.getDiseases())
                        .treatments(patientDetails.getTreatments())
                        .details(patientDetails.getOtherDetails())
                        .build())
                .flatMap(medicalRecordRepository::save)
                .then(emergencyContactRepository.findByPatient(patientDetails.getPatientId()))
                .map(emergencyContact -> emergencyContact.toBuilder()
                        .name(patientDetails.getEmergencyContactFullName())
                        .phoneNumber(patientDetails.getEmergencyContactPhoneNumber())
                        .relationship(patientDetails.getEmergencyContactRelationship())
                        .build())
                .flatMap(emergencyContactRepository::save)
                .then();
    }

    public Mono<Void> deletePatient(@NotNull UUID patientId) {
        return emergencyContactRepository.deleteByPatient(patientId)
                .then(medicalRecordRepository.deleteByPatient(patientId))
                .then(reportsRepository.deleteAllByPatient(patientId))
                .then(deviceRepository.findByPatient(patientId))
                .flatMap(device -> eventRepository.deleteAllByDeviceId(device.getId()))
                .then(deviceRepository.deleteByPatient(patientId))
                .then(patientRepository.deleteById(patientId))
                .then();
    }

    private Mono<PatientDetailsDTO> addRecentData(PatientDetailsDTO data) {
        return eventRepository.getRecentEventsForPatient(data.getPatientId())
                .sort((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .collectList()
                .map(events -> data.toBuilder().recentData(events).build());
    }

    private Mono<PatientDetailsDTO> addAlerts(PatientDetailsDTO data) {
        List<Alert> alerts = new ArrayList<>();
//        Target heart rate
//        moderate-intensity physical activity
//        For moderate-intensity physical activity, your target heart rate
//        should be between 64% and 76%1,2 of your maximum heart rate.
//        You can estimate your maximum heart rate based on your age.
//        To estimate your maximum age-related heart rate, subtract your age from 220.
//        For example, for a 50-year-old person, the estimated
//        maximum age-related heart rate would be calculated as 220 – 50 years = 170 beats per minute (bpm).
//        The 64% and 76% levels would be:
//        64% level: 170 x 0.64 = 109 bpm, and
//        76% level: 170 x 0.76 = 129 bpm
//        This shows that moderate-intensity physical activity for a 50-year-old person will
//        require that the heart rate remains between 109 and 129 bpm during physical activity


        if (data.getPulseRate() == null) {
            alerts.add(
                    Alert.builder()
                            .alertType(AlertType.DEVICE)
                            .gravity(Gravity.MEDIUM)
                            .message("Pulse is not available. Check for device errors.")
                            .build()
            );
        } else {
            double maximumAgeRelatedHeartRate = 220.0 - data.getPatientAge();
            double percent64 = (64.0 * maximumAgeRelatedHeartRate) / 100.0;
            double percent76 = (76.0 * maximumAgeRelatedHeartRate) / 100.0;
            if (Integer.parseInt(data.getPulseRate()) > percent76) {
                alerts.add(
                        Alert.builder()
                                .alertType(AlertType.PULSE)
                                .gravity(Gravity.HIGH)
                                .message("Pulse is too high. Recommended for moderate-intensity physical activity is between " + percent64 + " and " + percent76 + ".")
                                .build()
                );
            } else if (Integer.parseInt(data.getPulseRate()) < percent64) {
                alerts.add(
                        Alert.builder()
                                .alertType(AlertType.PULSE)
                                .gravity(Gravity.HIGH)
                                .message("Pulse is too low. Recommended for moderate-intensity physical activity is between " + percent64 + " and " + percent76 + ".")
                                .build()
                );
            }
        }
//        Normal body temperature can range from to 36.5 degrees C 37.2 degrees C for a healthy adult.

        if (data.getBodyTemperature() == null) {
            alerts.add(
                    Alert.builder()
                            .alertType(AlertType.DEVICE)
                            .gravity(Gravity.MEDIUM)
                            .message("Body temperature is not available. Check for device errors.")
                            .build()
            );
        } else {
            if (Double.parseDouble(data.getBodyTemperature()) > 37.2) {
                alerts.add(
                        Alert.builder()
                                .alertType(AlertType.BODY_TEMPERATURE)
                                .gravity(Gravity.HIGH)
                                .message("Body temperature is too high.")
                                .build()
                );
            } else if (Double.parseDouble(data.getBodyTemperature()) < 36.5) {
                alerts.add(
                        Alert.builder()
                                .alertType(AlertType.BODY_TEMPERATURE)
                                .gravity(Gravity.HIGH)
                                .message("Body temperature is too low.")
                                .build()
                );
            }
        }

//        Blood pressure is categorized as normal, elevated, or stage 1 or stage 2 high blood pressure:
//        Normal blood pressure is systolic of less than 120 and diastolic of less than 80 (120/80)
//        Elevated blood pressure is systolic of 120 to 129 and diastolic less than 80
//        Stage 1 high blood pressure is systolic is 130 to 139 or diastolic between 80 and 89
//        Stage 2 high blood pressure is when systolic is 140 or higher or the diastolic is 90 or higher

        if (data.getBloodPressure() == null) {
            alerts.add(
                    Alert.builder()
                            .alertType(AlertType.DEVICE)
                            .gravity(Gravity.MEDIUM)
                            .message("Blood pressure is not available. Check for device errors.")
                            .build()
            );
        } else {
            String[] tokens = data.getBloodPressure().split("/");
            int systolic = Integer.parseInt(tokens[0]);
            int diastolic = Integer.parseInt(tokens[1]);
            if (!(systolic <= 120 && diastolic < 80)) {
                if (systolic > 120 && systolic <= 129 && diastolic <= 80) {
                    alerts.add(
                            Alert.builder()
                                    .alertType(AlertType.BLOOD_PRESSURE)
                                    .gravity(Gravity.MEDIUM)
                                    .message("Blood pressure is elevated.")
                                    .build()
                    );
                } else if ((systolic >= 130 && systolic <= 139) && (diastolic >= 80 && diastolic <= 89)) {
                    alerts.add(
                            Alert.builder()
                                    .alertType(AlertType.BLOOD_PRESSURE)
                                    .gravity(Gravity.HIGH)
                                    .message("Blood pressure is in stage 1.")
                                    .build()
                    );
                } else if ((systolic >= 140) || (diastolic >= 90)) {
                    alerts.add(
                            Alert.builder()
                                    .alertType(AlertType.BLOOD_PRESSURE)
                                    .gravity(Gravity.HIGH)
                                    .message("Blood pressure is in stage 2.")
                                    .build()
                    );
                }
            }
        }

//        Normal pulse oximeter readings usually range from 95 to 100 percent.
//        Values under 90 percent are considered low.
        if (data.getOxygenLevel() == null) {
            alerts.add(
                    Alert.builder()
                            .alertType(AlertType.DEVICE)
                            .gravity(Gravity.MEDIUM)
                            .message("Oxygen level is not available. Check for device errors.")
                            .build()
            );
        } else {
            int oxygenLevel = Integer.parseInt(data.getOxygenLevel());
            if (oxygenLevel < 90) {
                alerts.add(
                        Alert.builder()
                                .alertType(AlertType.OXYGEN_IN_BLOOD)
                                .gravity(Gravity.HIGH)
                                .message("Oxygen level is low.")
                                .build()
                );
            }
        }

        return Mono.just(data.toBuilder().alerts(alerts).build());
    }

    private Mono<PatientVitalSignsData> addAlerts(PatientVitalSignsData data) {
        List<Alert> alerts = new ArrayList<>();
//        Target heart rate
//        moderate-intensity physical activity
//        For moderate-intensity physical activity, your target heart rate
//        should be between 64% and 76%1,2 of your maximum heart rate.
//        You can estimate your maximum heart rate based on your age.
//        To estimate your maximum age-related heart rate, subtract your age from 220.
//        For example, for a 50-year-old person, the estimated
//        maximum age-related heart rate would be calculated as 220 – 50 years = 170 beats per minute (bpm).
//        The 64% and 76% levels would be:
//        64% level: 170 x 0.64 = 109 bpm, and
//        76% level: 170 x 0.76 = 129 bpm
//        This shows that moderate-intensity physical activity for a 50-year-old person will
//        require that the heart rate remains between 109 and 129 bpm during physical activity


        if (data.getPulseRate() == null) {
            alerts.add(
                    Alert.builder()
                            .alertType(AlertType.DEVICE)
                            .gravity(Gravity.MEDIUM)
                            .message("Pulse is not available. Check for device errors.")
                            .build()
            );
        } else {
            double maximumAgeRelatedHeartRate = 220.0 - Double.parseDouble(data.getAge());
            double percent64 = (64.0 * maximumAgeRelatedHeartRate) / 100.0;
            double percent76 = (76.0 * maximumAgeRelatedHeartRate) / 100.0;
            if (Integer.parseInt(data.getPulseRate()) > percent76) {
                alerts.add(
                        Alert.builder()
                                .alertType(AlertType.PULSE)
                                .gravity(Gravity.HIGH)
                                .message("Pulse is too high. Recommended for moderate-intensity physical activity is between " + percent64 + " and " + percent76 + ".")
                                .build()
                );
            } else if (Integer.parseInt(data.getPulseRate()) < percent64) {
                alerts.add(
                        Alert.builder()
                                .alertType(AlertType.PULSE)
                                .gravity(Gravity.HIGH)
                                .message("Pulse is too low. Recommended for moderate-intensity physical activity is between " + percent64 + " and " + percent76 + ".")
                                .build()
                );
            }
        }
//        Normal body temperature can range from to 36.5 degrees C 37.2 degrees C for a healthy adult.

        if (data.getBodyTemperature() == null) {
            alerts.add(
                    Alert.builder()
                            .alertType(AlertType.DEVICE)
                            .gravity(Gravity.MEDIUM)
                            .message("Body temperature is not available. Check for device errors.")
                            .build()
            );
        } else {
            if (Double.parseDouble(data.getBodyTemperature()) > 37.2) {
                alerts.add(
                        Alert.builder()
                                .alertType(AlertType.BODY_TEMPERATURE)
                                .gravity(Gravity.HIGH)
                                .message("Body temperature is too high.")
                                .build()
                );
            } else if (Double.parseDouble(data.getBodyTemperature()) < 36.5) {
                alerts.add(
                        Alert.builder()
                                .alertType(AlertType.BODY_TEMPERATURE)
                                .gravity(Gravity.HIGH)
                                .message("Body temperature is too low.")
                                .build()
                );
            }
        }

//        Blood pressure is categorized as normal, elevated, or stage 1 or stage 2 high blood pressure:
//        Normal blood pressure is systolic of less than 120 and diastolic of less than 80 (120/80)
//        Elevated blood pressure is systolic of 120 to 129 and diastolic less than 80
//        Stage 1 high blood pressure is systolic is 130 to 139 or diastolic between 80 and 89
//        Stage 2 high blood pressure is when systolic is 140 or higher or the diastolic is 90 or higher

        if (data.getBloodPressure() == null) {
            alerts.add(
                    Alert.builder()
                            .alertType(AlertType.DEVICE)
                            .gravity(Gravity.MEDIUM)
                            .message("Blood pressure is not available. Check for device errors.")
                            .build()
            );
        } else {
            String[] tokens = data.getBloodPressure().split("/");
            int systolic = Integer.parseInt(tokens[0]);
            int diastolic = Integer.parseInt(tokens[1]);
            if (!(systolic <= 120 && diastolic < 80)) {
                if (systolic > 120 && systolic <= 129 && diastolic <= 80) {
                    alerts.add(
                            Alert.builder()
                                    .alertType(AlertType.BLOOD_PRESSURE)
                                    .gravity(Gravity.MEDIUM)
                                    .message("Blood pressure is elevated.")
                                    .build()
                    );
                } else if ((systolic >= 130 && systolic <= 139) && (diastolic >= 80 && diastolic <= 89)) {
                    alerts.add(
                            Alert.builder()
                                    .alertType(AlertType.BLOOD_PRESSURE)
                                    .gravity(Gravity.HIGH)
                                    .message("Blood pressure is in stage 1.")
                                    .build()
                    );
                } else if ((systolic >= 140) || (diastolic >= 90)) {
                    alerts.add(
                            Alert.builder()
                                    .alertType(AlertType.BLOOD_PRESSURE)
                                    .gravity(Gravity.HIGH)
                                    .message("Blood pressure is in stage 2.")
                                    .build()
                    );
                }
            }
        }

//        Normal pulse oximeter readings usually range from 95 to 100 percent.
//        Values under 90 percent are considered low.
        if (data.getOxygenLevel() == null) {
            alerts.add(
                    Alert.builder()
                            .alertType(AlertType.DEVICE)
                            .gravity(Gravity.MEDIUM)
                            .message("Oxygen level is not available. Check for device errors.")
                            .build()
            );
        } else {
            int oxygenLevel = Integer.parseInt(data.getOxygenLevel());
            if (oxygenLevel < 90) {
                alerts.add(
                        Alert.builder()
                                .alertType(AlertType.OXYGEN_IN_BLOOD)
                                .gravity(Gravity.HIGH)
                                .message("Oxygen level is low.")
                                .build()
                );
            }
        }

        return Mono.just(data.toBuilder().alerts(alerts).build());
    }

    private Mono<Patient> persistDetails(Patient patient, PatientDTO patientDTO) {
        return Mono.just(patient)
                .publishOn(Schedulers.boundedElastic())
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
