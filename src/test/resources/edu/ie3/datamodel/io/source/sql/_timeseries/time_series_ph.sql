CREATE TABLE public.time_series_ph
(
    uuid uuid PRIMARY KEY,
    time_series uuid NOT NULL,
    time timestamp with time zone NOT NULL,
    p double precision NOT NULL,
    heat_demand double precision NOT NULL
)
    WITHOUT OIDS
    TABLESPACE pg_default;

CREATE INDEX time_series_ph_series_id ON time_series_ph USING hash (time_series);

CREATE UNIQUE INDEX time_series_ph_series_time ON time_series_ph USING  btree (time_series, time);

INSERT INTO
    public.time_series_ph (uuid, time_series, time, p, heat_demand)
VALUES
('5d1235b2-656c-43e8-9186-b4a703f6e467', '76c9d846-797c-4f07-b7ec-2245f679f5c7', '2020-01-01 00:00:00+0', 1000.0, 8.0),
('de6659b2-1545-4739-8d0a-e8ff79a6cb4b', '76c9d846-797c-4f07-b7ec-2245f679f5c7', '2020-01-01 00:15:00+0', 1250.0, 12.0);
