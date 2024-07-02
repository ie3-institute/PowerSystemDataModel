CREATE TABLE public.test_data_sql
(
    uuid UUID PRIMARY KEY,
    time_series UUID NOT NULL,
    time TIMESTAMP WITH TIME ZONE NOT NULL,
    p DOUBLE PRECISION NOT NULL
)
    WITHOUT OIDS
    TABLESPACE pg_default;

CREATE INDEX test_data_sql_series_id ON test_data_sql USING hash (time_series);

-- Order of columns is important when using btree: https://www.postgresql.org/docs/14/indexes-multicolumn.html
-- Column time_series needs to placed as the first argument since we at most use an equality constraint on
-- time_series and a range query on time.
CREATE UNIQUE INDEX test_data_sql_series_time ON test_data_sql USING btree (time_series, time);