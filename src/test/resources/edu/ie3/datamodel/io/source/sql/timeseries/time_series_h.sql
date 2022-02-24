CREATE TABLE public.time_series_h
(
    uuid uuid PRIMARY KEY,
    time_series uuid NOT NULL,
    time timestamp with time zone NOT NULL,
    heat_demand double precision NOT NULL,
    UNIQUE(time_series, time)
)
    WITHOUT OIDS
    TABLESPACE pg_default;

CREATE INDEX time_series_h_series_id ON time_series_h USING hash (time_series);

INSERT INTO
    public.time_series_h (uuid, time_series, time, heat_demand)
VALUES
('5ec4ddfe-addf-4f32-8fb5-fd4eaa5e5ced', 'c8fe6547-fd85-4fdf-a169-e4da6ce5c3d0', '2020-01-01 00:00:00+0', 8.0),
('e82dd54c-9f6f-4451-9dcd-f4f41b8c9ee0', 'c8fe6547-fd85-4fdf-a169-e4da6ce5c3d0', '2020-01-01 00:15:00+0', 12.0);
