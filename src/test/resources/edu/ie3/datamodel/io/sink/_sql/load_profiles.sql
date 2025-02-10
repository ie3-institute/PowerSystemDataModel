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
    wi_wd DOUBLE PRECISION NOT NULL,
    grid_uuid UUID NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
    TABLESPACE pg_default;

CREATE INDEX load_profiles_series_id ON load_profiles USING hash (load_profile);

-- Order of columns is important when using btree: https://www.postgresql.org/docs/14/indexes-multicolumn.html
-- Column time_series needs to placed as the first argument since we at most use an equality constraint on
-- time_series and a range query on time.
CREATE UNIQUE INDEX load_profiles_series_time ON load_profiles USING btree (load_profile, quarter_hour);