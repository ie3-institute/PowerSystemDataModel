CREATE TABLE public.coordinates
(
    id integer,
    latitude double precision,
    longitude double precision
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

create INDEX id_idx
    ON public.coordinates USING btree
        (id ASC NULLS LAST)
    TABLESPACE pg_default;

create INDEX coordinate_idx
    ON public.coordinates USING btree
        (latitude ASC NULLS LAST, longitude ASC NULLS LAST)
    TABLESPACE pg_default;

INSERT INTO
    public.coordinates(id, latitude, longitude)
VALUES
(67775,51.5,7.438),
(531137,51.5,7.375),
(551525,51.438,7.438),
(278150,51.438,7.375)