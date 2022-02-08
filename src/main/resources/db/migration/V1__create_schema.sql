CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create table if not exists supervisors
(
    id        uuid    not null default uuid_generate_v4()
        constraint supervisors_pkey primary key,
    full_name varchar not null,
    email     varchar not null,
    password  varchar not null
);

create table if not exists patients
(
    id            uuid      not null default uuid_generate_v4()
        constraint patients_pkey primary key,
    full_name     varchar   not null,
    email         varchar,
    cnp           varchar   not null,
    home_address  varchar   not null,
    created       timestamp not null,
    supervisor_id uuid      not null
        constraint fk_patients_supervisor_id_supervisors references supervisors (id)
);

create table if not exists devices
(
    id         uuid    not null default uuid_generate_v4()
        constraint devices_pkey primary key,
    imei       varchar not null,
    brand      varchar not null,
    model      varchar not null,
    patient_id uuid    not null
        constraint fk_devices_patient_id_patients references patients (id)
);

create table if not exists vital_signs
(
    id        uuid      not null default uuid_generate_v4()
        constraint vs_pkey primary key,
    device_id uuid      not null
        constraint fk_vs_device_id_devices references devices (id),
    created   timestamp not null,
    vs_type   varchar   not null,
    data      jsonb     not null
);



