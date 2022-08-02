CREATE TABLE public.time_series_h
(
    uuid uuid PRIMARY KEY,
    time_series uuid NOT NULL,
    time timestamp with time zone NOT NULL,
    heat_demand double precision NOT NULL
)
    WITHOUT OIDS
    TABLESPACE pg_default;

CREATE INDEX time_series_h_series_id ON time_series_h USING hash (time_series);

-- Order of columns is important when using btree: https://www.postgresql.org/docs/14/indexes-multicolumn.html
-- Column time_series needs to placed as the first argument since we at most use an equality constraint on
-- time_series and a range query on time.
CREATE UNIQUE INDEX time_series_h_series_time ON time_series_h USING btree (time_series, time);

INSERT INTO
    public.time_series_h (uuid, time_series, time, heat_demand)
VALUES
('5ec4ddfe-addf-4f32-8fb5-fd4eaa5e5ced', 'c8fe6547-fd85-4fdf-a169-e4da6ce5c3d0', '2020-01-01 00:00:00+0', 8.0),
('e82dd54c-9f6f-4451-9dcd-f4f41b8c9ee0', 'c8fe6547-fd85-4fdf-a169-e4da6ce5c3d0', '2020-01-01 00:15:00+0', 12.0);
