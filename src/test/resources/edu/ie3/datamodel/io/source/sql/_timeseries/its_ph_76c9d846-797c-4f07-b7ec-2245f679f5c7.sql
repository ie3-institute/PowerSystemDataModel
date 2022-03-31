CREATE TABLE public."its_ph_76c9d846-797c-4f07-b7ec-2245f679f5c7"
(
    time timestamp with time zone,
    p double precision,
    heat_demand double precision,
    uuid uuid,
    CONSTRAINT its_ph_pkey PRIMARY KEY (uuid)
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

INSERT INTO
    public."its_ph_76c9d846-797c-4f07-b7ec-2245f679f5c7" (uuid, time, p, heat_demand)
VALUES
('5d1235b2-656c-43e8-9186-b4a703f6e467', '2020-01-01 00:00:00+0', 1000.0, 8.0),
('de6659b2-1545-4739-8d0a-e8ff79a6cb4b', '2020-01-01 00:15:00+0', 1250.0, 12.0);
