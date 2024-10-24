CREATE TABLE public.time_series_c
(
    time_series UUID NOT NULL,
    time TIMESTAMP WITH TIME ZONE NOT NULL,
    price DOUBLE PRECISION,
    grid_uuid UUID NOT NULL REFERENCES grids(uuid)
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
    time_series UUID NOT NULL,
    time TIMESTAMP WITH TIME ZONE NOT NULL,
    p DOUBLE PRECISION,
    grid_uuid UUID NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
    TABLESPACE pg_default;

CREATE INDEX time_series_p_series_id ON time_series_p USING hash (time_series);

CREATE UNIQUE INDEX time_series_p_series_time ON time_series_p USING btree (time_series, time);

CREATE TABLE public.time_series_pq
(
    time_series UUID NOT NULL,
    time TIMESTAMP WITH TIME ZONE NOT NULL,
    p DOUBLE PRECISION,
    q DOUBLE PRECISION,
    grid_uuid UUID NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
    TABLESPACE pg_default;

CREATE INDEX time_series_pq_series_id ON time_series_pq USING hash (time_series);

CREATE UNIQUE INDEX time_series_pq_series_time ON time_series_pq USING btree (time_series, time);

CREATE TABLE public.time_series_h
(
    time_series UUID NOT NULL,
    time TIMESTAMP WITH TIME ZONE NOT NULL,
    heat_demand DOUBLE PRECISION,
    grid_uuid UUID NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
    TABLESPACE pg_default;

CREATE INDEX time_series_h_series_id ON time_series_h USING hash (time_series);

CREATE UNIQUE INDEX time_series_h_series_time ON time_series_h USING btree (time_series, time);

CREATE TABLE public.time_series_ph
(
    time_series UUID NOT NULL,
    time TIMESTAMP WITH TIME ZONE NOT NULL,
    p DOUBLE PRECISION,
    heat_demand DOUBLE PRECISION,
    grid_uuid UUID NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
    TABLESPACE pg_default;

CREATE INDEX time_series_ph_series_id ON time_series_ph USING hash (time_series);

CREATE UNIQUE INDEX time_series_ph_series_time ON time_series_ph USING btree (time_series, time);

CREATE TABLE public.time_series_pqh
(
    time_series UUID NOT NULL,
    time TIMESTAMP WITH TIME ZONE NOT NULL,
    p DOUBLE PRECISION,
    q DOUBLE PRECISION,
    heat_demand DOUBLE PRECISION,
    grid_uuid UUID NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
    TABLESPACE pg_default;

CREATE INDEX time_series_pqh_series_id ON time_series_pqh USING hash (time_series);

CREATE UNIQUE INDEX time_series_pqh_series_time ON time_series_pqh USING btree (time_series, time);

CREATE TABLE public.time_series_weather
(
    time_series UUID NOT NULL,
    coordinate TEXT NOT NULL,
    time TIMESTAMP WITH TIME ZONE NOT NULL,
    diffuse_irradiance DOUBLE PRECISION,
    direct_irradiance DOUBLE PRECISION,
    direction DOUBLE PRECISION,
    temperature DOUBLE PRECISION,
    velocity DOUBLE PRECISION,
    grid_uuid UUID NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
    TABLESPACE pg_default;

CREATE INDEX time_series_weather_series_id ON time_series_weather USING hash (time_series);

CREATE UNIQUE INDEX time_series_weather_series_time ON time_series_weather USING btree (time_series, time);