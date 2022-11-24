CREATE TABLE public.coordinates
(
    id integer,
    coordinate GEOMETRY
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

CREATE INDEX idx ON public.coordinates USING gist (coordinate);

INSERT INTO
    public.coordinates(id, coordinate)
VALUES
(67775, ST_POINT(7.438, 51.5, 4326)),
(531137, ST_POINT(7.375, 51.5, 4326)),
(551525, ST_POINT(7.438, 51.438, 4326)),
(278150, ST_POINT(7.375, 51.438, 4326))