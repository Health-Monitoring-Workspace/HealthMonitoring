create table if not exists patients
(
    id           uuid    not null default uuid_generate_v4()
        constraint patients_pkey primary key,
    name         varchar not null,
    email        varchar not null,
    phone_number varchar not null,
    cnp          varchar not null,
    home_address varchar not null,
    created_at   timestamp,
    supervisor   uuid    not null
        constraint fk_patients_supervisors_id_supervisor references supervisors (id)
);

create table if not exists medical_records
(
    id         uuid not null default uuid_generate_v4()
        constraint medical_records_pkey primary key,
    diseases   varchar,
    treatments varchar,
    details    varchar,
    patient    uuid not null
        constraint fk_medical_records_patients_id_patient references patients (id)
);

create table if not exists devices
(
    id      uuid not null default uuid_generate_v4()
        constraint devices_pkey primary key,
    imei    varchar,
    brand   varchar,
    model   varchar,
    patient uuid not null
        constraint fk_devices_patients_id_patient references patients (id)
);

create table if not exists emergency_contacts
(
    id           uuid    not null default uuid_generate_v4()
        constraint emergency_contacts_pkey primary key,
    name         varchar not null,
    phone_number varchar not null,
    relationship varchar not null,
    patient      uuid    not null
        constraint fk_emergency_contacts_patients_id_patient references patients (id)
);