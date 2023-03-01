CREATE TABLE public.coordinates
(
    lat_rot integer,
    long_rot double precision,
    lat_geo double precision,
    long_geo double precision,
    id integer,
    tid integer,
    PRIMARY KEY (id)
);

INSERT INTO
    public.coordinates (lat_rot, long_rot, lat_geo, long_geo, id, tid)
VALUES
    (-10,-6.8125,39.602772,1.279336,106580,1),
    (-10,-6.75,39.610001,1.358673,106581,2),
    (-10,-6.6875,39.617161,1.438028,106582,3),
    (-10,-6.625,39.624249,1.5174021,106583,4);
