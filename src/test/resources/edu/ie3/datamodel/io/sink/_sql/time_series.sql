CREATE TABLE public.time_series_c
(
    uuid uuid PRIMARY KEY,
    time_series uuid NOT NULL,
    time timestamp with time zone NOT NULL,
    price double precision,
    grid_name TEXT NOT NULL,
    grid_uuid uuid NOT NULL
)
    WITHOUT OIDS
    TABLESPACE pg_default;

CREATE INDEX time_series_c_series_id ON time_series_c USING hash (time_series);

-- Order of columns is important when using btree: https://www.postgresql.org/docs/14/indexes-multicolumn.html
-- Column time_series needs to placed as the first argument since we at most use an equality constraint on
-- time_series and a range query on time.
CREATE UNIQUE INDEX time_series_c_series_time ON time_series_c USING btree (time_series, time);

CREATE TABLE public.time_series_p
(
    uuid uuid PRIMARY KEY,
    time_series uuid NOT NULL,
    time timestamp with time zone NOT NULL,
    p double precision,
    grid_name TEXT NOT NULL,
    grid_uuid uuid NOT NULL
)
    WITHOUT OIDS
    TABLESPACE pg_default;

CREATE INDEX time_series_p_series_id ON time_series_p USING hash (time_series);

CREATE UNIQUE INDEX time_series_p_series_time ON time_series_p USING btree (time_series, time);

CREATE TABLE public.time_series_pq
(
    uuid uuid PRIMARY KEY,
    time_series uuid NOT NULL,
    time timestamp with time zone NOT NULL,
    p double precision,
    q double precision,
    grid_name TEXT NOT NULL,
    grid_uuid uuid NOT NULL
)
    WITHOUT OIDS
    TABLESPACE pg_default;

CREATE INDEX time_series_pq_series_id ON time_series_pq USING hash (time_series);

CREATE UNIQUE INDEX time_series_pq_series_time ON time_series_pq USING btree (time_series, time);

CREATE TABLE public.time_series_h
(
    uuid uuid PRIMARY KEY,
    time_series uuid NOT NULL,
    time timestamp with time zone NOT NULL,
    heatDemand double precision,
    grid_name TEXT NOT NULL,
    grid_uuid uuid NOT NULL
)
    WITHOUT OIDS
    TABLESPACE pg_default;

CREATE INDEX time_series_h_series_id ON time_series_h USING hash (time_series);

CREATE UNIQUE INDEX time_series_h_series_time ON time_series_h USING btree (time_series, time);

CREATE TABLE public.time_series_ph
(
    uuid uuid PRIMARY KEY,
    time_series uuid NOT NULL,
    time timestamp with time zone NOT NULL,
    p double precision,
    heatDemand double precision,
    grid_name TEXT NOT NULL,
    grid_uuid uuid NOT NULL
)
    WITHOUT OIDS
    TABLESPACE pg_default;

CREATE INDEX time_series_ph_series_id ON time_series_ph USING hash (time_series);

CREATE UNIQUE INDEX time_series_ph_series_time ON time_series_ph USING btree (time_series, time);

CREATE TABLE public.time_series_pqh
(
    uuid uuid PRIMARY KEY,
    time_series uuid NOT NULL,
    time timestamp with time zone NOT NULL,
    p double precision,
    q double precision,
    heatDemand double precision,
    grid_name TEXT NOT NULL,
    grid_uuid uuid NOT NULL
)
    WITHOUT OIDS
    TABLESPACE pg_default;

CREATE INDEX time_series_pqh_series_id ON time_series_pqh USING hash (time_series);

CREATE UNIQUE INDEX time_series_pqh_series_time ON time_series_pqh USING btree (time_series, time);

CREATE TABLE public.time_series_weather
(
    uuid uuid PRIMARY KEY,
    time_series uuid NOT NULL,
    coordinate TEXT NOT NULL,
    time timestamp with time zone NOT NULL,
    diffuseIrradiance double precision,
    directIrradiance double precision,
    direction double precision,
    temperature double precision,
    velocity double precision,
    grid_name TEXT NOT NULL,
    grid_uuid uuid NOT NULL
)
    WITHOUT OIDS
    TABLESPACE pg_default;

CREATE INDEX time_series_weather_series_id ON time_series_weather USING hash (time_series);

CREATE UNIQUE INDEX time_series_weather_series_time ON time_series_weather USING btree (time_series, time);