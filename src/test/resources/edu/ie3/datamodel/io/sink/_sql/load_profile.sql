CREATE TABLE public.load_profile_g2
(
    time_series uuid NOT NULL,
    day_of_week TEXT NOT NULL,
    quarter_hour_of_day TEXT NOT NULL,
    p double precision,
    grid_uuid uuid NOT NULL REFERENCES grids(uuid)
)
    WITHOUT OIDS
    TABLESPACE pg_default;

