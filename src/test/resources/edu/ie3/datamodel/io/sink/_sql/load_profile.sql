CREATE TABLE public.load_profile_g2
(
    uuid uuid PRIMARY KEY,
    time_series uuid NOT NULL,
    dayOfWeek TEXT NOT NULL,
    quarterHourOfDay TEXT NOT NULL,
    p double precision,
    grid_name TEXT NOT NULL,
    grid_uuid uuid NOT NULL
)
    WITHOUT OIDS
    TABLESPACE pg_default;

