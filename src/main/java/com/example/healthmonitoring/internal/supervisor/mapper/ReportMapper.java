package com.example.healthmonitoring.internal.supervisor.mapper;

import com.example.healthmonitoring.common.domain.entity.Patient;
import com.example.healthmonitoring.common.domain.entity.Report;
import com.example.healthmonitoring.common.domain.entity.utility.VitalSignType;
import com.example.healthmonitoring.internal.supervisor.dto.ReportDTO;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ReportMapper {

    public static Mono<List<ReportDTO>> mapToDTO(List<Report> reports, Patient patient) {
        Stream<ReportDTO> reportsStream;

        Optional<Report> oxygenReport = reports.stream().filter(report -> report.getVitalSignType().equals(VitalSignType.OXYGEN_IN_BLOOD)).findFirst();
        Optional<Report> pulseReport = reports.stream().filter(report -> report.getVitalSignType().equals(VitalSignType.PULSE)).findFirst();
        Optional<Report> bloodPressureReport = reports.stream().filter(report -> report.getVitalSignType().equals(VitalSignType.BLOOD_PRESSURE)).findFirst();
        Optional<Report> bodyTemperatureReport = reports.stream().filter(report -> report.getVitalSignType().equals(VitalSignType.BODY_TEMPERATURE)).findFirst();

        Optional<Report> anyReport = reports.stream().findAny();
        if (anyReport.isPresent()) {
            LocalDate reportDate = anyReport.get().getDate();
            reportsStream = Stream.of(ReportDTO.builder()
                    .fullName(patient.getName())
                    .age(Period.between(patient.getBirthDate(), LocalDate.now()).getYears())
                    .reportDate(reportDate)
                    .meanOxygenLevel(oxygenReport.orElse(Report.builder().mean("Not available").build()).getMean())
                    .meanBloodPressure(bloodPressureReport.orElse(Report.builder().mean("Not available").build()).getMean())
                    .meanPulseRate(pulseReport.orElse(Report.builder().mean("Not available").build()).getMean())
                    .meanBodyTemperature(bodyTemperatureReport.orElse(Report.builder().mean("Not available").build()).getMean())
                    .build());
        } else {
            reportsStream = Stream.empty();
        }

        return Mono.just(reportsStream.filter(Objects::nonNull).collect(Collectors.toList()));
    }

}
