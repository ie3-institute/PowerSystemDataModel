CREATE TABLE public.time_series_c
(
    uuid uuid PRIMARY KEY,
    time_series uuid NOT NULL,
    time timestamp with time zone NOT NULL,
    price double precision
)
    WITHOUT OIDS
    TABLESPACE pg_default;

CREATE INDEX time_series_c_series_id ON time_series_c USING hash (time_series);

-- Order of columns is important when using btree: https://www.postgresql.org/docs/14/indexes-multicolumn.html
-- time_series at first since we at most use an equality constraint on time_series and a range query on time
CREATE UNIQUE INDEX time_series_c_series_time ON time_series_c USING btree (time_series, time);

INSERT INTO
    public.time_series_c (uuid, time_series, time, price)
VALUES
('45bd936f-524a-4d59-8978-31ccf37fa230', '2fcb3e53-b94a-4b96-bea4-c469e499f1a1', '2020-01-01 00:00:00+0', 100.0),
('41b8dbf6-3e75-4073-8359-89d015777dd6', '2fcb3e53-b94a-4b96-bea4-c469e499f1a1', '2020-01-01 00:15:00+0', 125.0);

