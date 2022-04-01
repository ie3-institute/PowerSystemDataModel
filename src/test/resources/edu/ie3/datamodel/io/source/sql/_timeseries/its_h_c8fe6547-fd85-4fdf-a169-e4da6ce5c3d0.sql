CREATE TABLE public."its_h_c8fe6547-fd85-4fdf-a169-e4da6ce5c3d0"
(
    time timestamp with time zone,
    heat_demand double precision,
    uuid uuid,
    CONSTRAINT its_h_pkey PRIMARY KEY (uuid)
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

INSERT INTO
    public."its_h_c8fe6547-fd85-4fdf-a169-e4da6ce5c3d0" (uuid, time, heat_demand)
VALUES
('5ec4ddfe-addf-4f32-8fb5-fd4eaa5e5ced', '2020-01-01 00:00:00+0', 8.0),
('e82dd54c-9f6f-4451-9dcd-f4f41b8c9ee0', '2020-01-01 00:15:00+0', 12.0);
