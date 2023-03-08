CREATE TABLE public.coordinates
(
    id integer,
    latitude double precision,
    longitude double precision,
    coordinate_type varchar,
    PRIMARY KEY (id)
);


INSERT INTO
    public.coordinates (id, latitude, longitude, coordinate_type)
VALUES
    (67775,51.5,7.438,'ICON'),
    (531137,51.5,7.375,'ICON'),
    (551525,51.438,7.438,'ICON'),
    (278150,51.438,7.375,'ICON');