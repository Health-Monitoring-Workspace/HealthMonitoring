create table if not exists reports
(
    id              uuid    not null default uuid_generate_v4()
        constraint reports_pkey primary key,
    date            date,
    vital_sign_type varchar not null,
    mean            varchar not null,
    patient         uuid    not null
        constraint fk_reports_patients_id_patient references patients (id),
    constraint reports_unique unique (date, vital_sign_type, patient)
);

create or replace function generate_report(selected_date date)
    returns void
    language plpgsql
as
$$
BEGIN
    -- -----------------------------------------------------------------------------------------------
    insert into reports(date, vital_sign_type, mean, patient)
    select selected_date,
           'PULSE',
           avg(e.data::integer)::double precision as data,
           p.id                 as patient
    from events e
             inner join devices d on d.id = e.device_id
             inner join patients p on p.id = d.patient
    where e.created_at::date = selected_date
      and vital_sign_type = 'PULSE'
    group by p.id
    on conflict on constraint reports_unique
        do update set mean = EXCLUDED.mean;
    -- -----------------------------------------------------------------------------------------------
    insert into reports(date, vital_sign_type, mean, patient)
    select selected_date,
           'OXYGEN_IN_BLOOD',
           avg(e.data::integer)::double precision as data,
           p.id                 as patient
    from events e
             inner join devices d on d.id = e.device_id
             inner join patients p on p.id = d.patient
    where e.created_at::date = selected_date
      and vital_sign_type = 'OXYGEN_IN_BLOOD'
    group by p.id
    on conflict on constraint reports_unique
        do update set mean = EXCLUDED.mean;
    -- -----------------------------------------------------------------------------------------------
    insert into reports(date, vital_sign_type, mean, patient)
    select selected_date,
           'BODY_TEMPERATURE',
           avg(e.data::integer) as data,
           p.id                 as patient
    from events e
             inner join devices d on d.id = e.device_id
             inner join patients p on p.id = d.patient
    where e.created_at::date = selected_date
      and vital_sign_type = 'BODY_TEMPERATURE'
    group by p.id
    on conflict on constraint reports_unique
        do update set mean = EXCLUDED.mean;
    -- -----------------------------------------------------------------------------------------------
    insert into reports(date, vital_sign_type, mean, patient)
    select r.selected_date,
           r.vital_sign_type,
           concat(r.systolic, '/', r.diastolic) as mean,
           r.patient                            as patient
    from (
             select selected_date                                     as selected_date,
                    'BLOOD_PRESSURE'                                  as vital_sign_type,
                    avg(split_part(e.data, '/', 1)::integer)::integer as systolic,
                    avg(split_part(e.data, '/', 2)::integer)::integer as diastolic,
                    p.id                                              as patient
             from events e
                      inner join devices d on d.id = e.device_id
                      inner join patients p on p.id = d.patient
             where e.created_at::date = selected_date
               and vital_sign_type = 'BLOOD_PRESSURE'
             group by p.id) as r
    on conflict on constraint reports_unique
        do update set mean = EXCLUDED.mean;
end
$$