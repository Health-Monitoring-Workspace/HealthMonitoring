CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create table if not exists supervisors
(
    id        uuid    not null default uuid_generate_v4()
        constraint supervisors_pkey primary key,
    full_name varchar not null,
    email     varchar not null,
    password  varchar not null
);


