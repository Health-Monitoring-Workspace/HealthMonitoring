insert into supervisors (id, full_name, email, title)
values ('1488ad1e-987f-11ec-b909-0242ac120002', 'Jane Doe', 'jane.doe@email.com', 'doctor'),
       ('1488b0a2-987f-11ec-b909-0242ac120002', 'John Doe', 'jdoe@email.com', 'doctor'),
       ('6175f4c4-987f-11ec-b909-0242ac120002', 'Will Smith', 'wsmith@email.com', 'doctor');

insert into credentials (id, username, password, supervisor_id)
values ('6805544c-987f-11ec-b909-0242ac120002', 'jane.doe',
        '$2a$12$IZlPY0OAj8YwceRGIamDx.PjvntgxSy/Viti2zZT8Bnkew9/DX0tW',
        '1488ad1e-987f-11ec-b909-0242ac120002'),
       ('68055672-987f-11ec-b909-0242ac120002', 'john.doe',
        '$2a$12$IZlPY0OAj8YwceRGIamDx.PjvntgxSy/Viti2zZT8Bnkew9/DX0tW',
        '1488b0a2-987f-11ec-b909-0242ac120002'),
       ('68055794-987f-11ec-b909-0242ac120002', 'wsmith',
        '$2a$12$IZlPY0OAj8YwceRGIamDx.PjvntgxSy/Viti2zZT8Bnkew9/DX0tW',
        '6175f4c4-987f-11ec-b909-0242ac120002');