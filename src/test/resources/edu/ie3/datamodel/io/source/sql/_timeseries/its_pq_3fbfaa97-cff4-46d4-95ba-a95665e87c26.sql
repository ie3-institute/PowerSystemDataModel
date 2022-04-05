CREATE TABLE public."its_pq_3fbfaa97-cff4-46d4-95ba-a95665e87c26"
(
    time timestamp with time zone,
    p double precision,
    q double precision,
    uuid uuid,
    CONSTRAINT its_pq_pkey PRIMARY KEY (uuid)
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

INSERT INTO
    public."its_pq_3fbfaa97-cff4-46d4-95ba-a95665e87c26" (uuid, time, p, q)
VALUES
('da288786-d3e3-40aa-a34a-f67955d45ac8', '2020-01-01 00:00:00+0', 1000.0, 329.0),
('43dd0a7b-7a7e-4393-b516-a0ddbcbf073b', '2020-01-01 00:15:00+0', 1250.0, 411.0);
