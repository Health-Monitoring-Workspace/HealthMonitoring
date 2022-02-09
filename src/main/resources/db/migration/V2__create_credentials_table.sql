create table if not exists credentials
(
    id uuid not null default uuid_generate_v4()
        constraint credentials_pkey primary key,
    username varchar not null,
    password varchar not null,
    supervisor_id uuid not null
        constraint fk_credentials_supervisor_id_supervisors references supervisors (id)
);

alter table supervisors drop column if exists password;
alter table supervisors add column if not exists title varchar;