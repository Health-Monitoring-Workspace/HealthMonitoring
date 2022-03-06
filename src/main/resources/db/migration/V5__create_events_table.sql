create table if not exists events
(
    id              uuid    not null default uuid_generate_v4()
        constraint events_pkey primary key,
    device_id       uuid    not null
        constraint fk_events_device_id_devices references devices (id),
    vital_sign_type varchar not null,
    data            varchar not null,
    created_at      timestamp
);