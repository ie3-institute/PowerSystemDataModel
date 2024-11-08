CREATE TABLE public.load_profiles
(
    load_profile VARCHAR(11) NOT NULL,
    quarter_hour integer NOT NULL,
    su_sa DOUBLE PRECISION NOT NULL,
    su_su DOUBLE PRECISION NOT NULL,
    su_wd DOUBLE PRECISION NOT NULL,
    tr_sa DOUBLE PRECISION NOT NULL,
    tr_su DOUBLE PRECISION NOT NULL,
    tr_wd DOUBLE PRECISION NOT NULL,
    wi_sa DOUBLE PRECISION NOT NULL,
    wi_su DOUBLE PRECISION NOT NULL,
    wi_wd DOUBLE PRECISION NOT NULL
)
    WITHOUT OIDS
    TABLESPACE pg_default;

CREATE INDEX load_profiles_series_id ON load_profiles USING hash (load_profile);

-- Order of columns is important when using btree: https://www.postgresql.org/docs/14/indexes-multicolumn.html
-- Column time_series needs to placed as the first argument since we at most use an equality constraint on
-- time_series and a range query on time.
CREATE UNIQUE INDEX load_profiles_series_time ON load_profiles USING btree (load_profile, quarter_hour);

INSERT INTO
    public.load_profiles (load_profile, quarter_hour, su_sa, su_su, su_wd, tr_sa, tr_su, tr_wd, wi_sa, wi_su, wi_wd)
VALUES

('g2', 0, 63.1, 50.6, 60.8, 73.1, 64.2, 70.5, 80.6, 73.7, 77.4),
('g2', 1, 58.0, 47.4, 53.0, 67.6, 60.7, 61.9, 74.6, 68.7, 67.4),
('g2', 2, 53.5, 44.3, 46.0, 62.8, 56.9, 54.4, 69.2, 63.6, 58.4),

('g3', 0, 99.0, 94.6, 98.5, 92.5, 87.6, 91.2, 95.3, 87.8, 94.7),
('g3', 1, 100.0, 95.3, 99.0, 93.0, 87.3, 90.2, 95.5, 88.3, 94.1),
('g3', 2, 100.9, 96.0, 99.4, 93.7, 87.4, 89.4, 96.2, 89.0, 94.1);
