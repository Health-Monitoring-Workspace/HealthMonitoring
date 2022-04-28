create or replace function refresh_view()
    returns trigger
as
$$
begin
    refresh materialized view patients_vital_signs_view;
    refresh materialized view patients_details_view;
    return null;
end;
$$
    language plpgsql;

create trigger after_insert
    after insert or update or delete
    on events
    for each statement
execute procedure refresh_view();