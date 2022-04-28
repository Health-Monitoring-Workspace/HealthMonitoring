drop materialized view  patients_details_view;

CREATE MATERIALIZED VIEW patients_details_view as
select p.id                                 as patient_id,
       p.name                               as patient_full_name,
       p.email                              as patient_email,
       p.phone_number                       as patient_phone_number,
       p.cnp                                as patient_cnp,
       p.home_address                       as patient_home_address,
       p.birth_date                         as patient_birth_date,
       date_part('year', age(p.birth_date)) as patient_age,
       dev.imei                             as device_imei,
       dev.brand                            as device_brand,
       dev.model                            as device_model,
       mr.diseases                          as diseases,
       mr.treatments                        as treatments,
       mr.details                           as other_details,
       ec.name                              as emergency_contact_full_name,
       ec.phone_number                      as emergency_contact_phone_number,
       ec.relationship                      as emergency_contact_relationship,
       pulse.data                           as pulse_rate,
       oxygen.data                          as oxygen_level,
       blood.data                           as blood_pressure,
       bodytemp.data                        as body_temperature,
       lastseen.created_at                  as last_seen,
       case
           when (lastseen.created_at > now() - interval '2 minutes') then TRUE
           else FALSE
           END                              as is_online,
       s.id                                 as supervisor
from (select distinct
    on (device_id) device_id,
                   data
      from events
      where vital_sign_type = 'PULSE'
      order by device_id, created_at desc) as pulse
         left join (select distinct
    on (device_id) device_id,
                   data
                    from events
                    where vital_sign_type = 'OXYGEN_IN_BLOOD'
                    order by device_id, created_at desc) as oxygen on pulse.device_id = oxygen.device_id
         left join (select distinct
    on (device_id) device_id,
                   data
                    from events
                    where vital_sign_type = 'BLOOD_PRESSURE'
                    order by device_id, created_at desc) as blood on blood.device_id = pulse.device_id
         left join (select distinct
    on (device_id) device_id,
                   data
                    from events
                    where vital_sign_type = 'BODY_TEMPERATURE'
                    order by device_id, created_at desc) as bodytemp on bodytemp.device_id = pulse.device_id
         left join (select distinct
    on (device_id) device_id,
                   created_at
                    from events
                    order by device_id, created_at desc) as lastseen on lastseen.device_id = pulse.device_id
         inner join devices dev on pulse.device_id = dev.id
         inner join patients p on dev.patient = p.id
         inner join medical_records mr on p.id = mr.patient
         inner join emergency_contacts ec on p.id = ec.patient
         inner join supervisors s on s.id = p.supervisor;


refresh
    materialized view patients_vital_signs_view;