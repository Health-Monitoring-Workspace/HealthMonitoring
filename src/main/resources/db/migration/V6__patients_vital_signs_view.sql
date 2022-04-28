drop materialized view patients_vital_signs_view;

CREATE MATERIALIZED VIEW patients_vital_signs_view as
select p.name                               as name,
       date_part('year', age(p.birth_date)) as age,
       pulse.data                           as pulse_rate,
       oxygen.data                          as oxygen_level,
       blood.data                           as blood_pressure,
       bodytemp.data                        as body_temperature,
       lastseen.created_at                  as last_seen,
       case
           when (lastseen.created_at > now() + interval '3 hours '- interval '2 minutes') then TRUE
           else FALSE
           END                              as is_online,
       s.id                                 as supervisor
from (select distinct on (device_id) device_id, data
      from events
      where vital_sign_type = 'PULSE'
      order by device_id, created_at desc) as pulse
         left join (select distinct on (device_id) device_id, data
                    from events
                    where vital_sign_type = 'OXYGEN_IN_BLOOD'
                    order by device_id, created_at desc) as oxygen on pulse.device_id = oxygen.device_id
         left join (select distinct on (device_id) device_id, data
                    from events
                    where vital_sign_type = 'BLOOD_PRESSURE'
                    order by device_id, created_at desc) as blood on blood.device_id = pulse.device_id
         left join (select distinct on (device_id) device_id, data
                    from events
                    where vital_sign_type = 'BODY_TEMPERATURE'
                    order by device_id, created_at desc) as bodytemp on bodytemp.device_id = pulse.device_id
         left join (select distinct on (device_id) device_id, created_at
                    from events
                    order by device_id, created_at desc) as lastseen on lastseen.device_id = pulse.device_id
         inner join devices dev on pulse.device_id = dev.id
         inner join patients p on dev.patient = p.id
         inner join supervisors s on s.id = p.supervisor;


refresh materialized view patients_vital_signs_view;