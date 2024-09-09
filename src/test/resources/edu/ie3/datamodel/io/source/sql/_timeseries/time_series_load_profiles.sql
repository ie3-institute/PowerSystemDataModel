CREATE TABLE public.time_series_load_profiles
(
    load_profile VARCHAR(11) NOT NULL,
    time_series uuid NOT NULL,
    quarter_hour integer NOT NULL,
    SuSa double precision NOT NULL,
    SuSu double precision NOT NULL,
    SuWd double precision NOT NULL,
    TrSa double precision NOT NULL,
    TrSu double precision NOT NULL,
    TrWd double precision NOT NULL,
    WiSa double precision NOT NULL,
    WiSu double precision NOT NULL,
    WiWd double precision NOT NULL
)
    WITHOUT OIDS
    TABLESPACE pg_default;

CREATE INDEX time_series_load_profiles_series_id ON time_series_load_profiles USING hash (time_series);

-- Order of columns is important when using btree: https://www.postgresql.org/docs/14/indexes-multicolumn.html
-- Column time_series needs to placed as the first argument since we at most use an equality constraint on
-- time_series and a range query on time.
CREATE UNIQUE INDEX time_series_load_profiles_series_time ON time_series_load_profiles USING btree (time_series, quarter_hour);

INSERT INTO
    public.time_series_load_profiles (load_profile, time_series, quarter_hour, SuSa, SuSu, SuWd, TrSa, TrSu, TrWd, WiSa, WiSu, WiWd)
VALUES

('g2', 'b0ad5ba2-0d5e-4c9b-b818-4079cebf59cc', 0, 63.1, 50.6, 60.8, 73.1, 64.2, 70.5, 80.6, 73.7, 77.4),
('g2', 'b0ad5ba2-0d5e-4c9b-b818-4079cebf59cc', 1, 58.0, 47.4, 53.0, 67.6, 60.7, 61.9, 74.6, 68.7, 67.4),
('g2', 'b0ad5ba2-0d5e-4c9b-b818-4079cebf59cc', 2, 53.5, 44.3, 46.0, 62.8, 56.9, 54.4, 69.2, 63.6, 58.4),

('g3', '9b880468-309c-43c1-a3f4-26dd26266216', 0, 99.0, 94.6, 98.5, 92.5, 87.6, 91.2, 95.3, 87.8, 94.7),
('g3', '9b880468-309c-43c1-a3f4-26dd26266216', 1, 100.0, 95.3, 99.0, 93.0, 87.3, 90.2, 95.5, 88.3, 94.1),
('g3', '9b880468-309c-43c1-a3f4-26dd26266216', 2, 100.9, 96.0, 99.4, 93.7, 87.4, 89.4, 96.2, 89.0, 94.1);
