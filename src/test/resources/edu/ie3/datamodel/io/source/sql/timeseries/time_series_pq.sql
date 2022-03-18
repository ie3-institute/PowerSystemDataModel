CREATE TABLE public.time_series_pq
(
    uuid uuid PRIMARY KEY,
    time_series uuid NOT NULL,
    time timestamp with time zone NOT NULL,
    p double precision NOT NULL,
    q double precision NOT NULL
)
    WITHOUT OIDS
    TABLESPACE pg_default;

CREATE INDEX time_series_pq_series_id ON time_series_pq USING hash (time_series);

-- Order of columns is important when using btree: https://www.postgresql.org/docs/14/indexes-multicolumn.html
-- time_series at first since we at most use an equality constraint on time_series and a range query on time
CREATE UNIQUE INDEX time_series_pq_series_time ON time_series_pq USING btree (time_series, time);

INSERT INTO
    public.time_series_pq (uuid, time_series, time, p, q)
VALUES
('da288786-d3e3-40aa-a34a-f67955d45ac8', '3fbfaa97-cff4-46d4-95ba-a95665e87c26', '2020-01-01 00:00:00+0', 1000.0, 329.0),
('43dd0a7b-7a7e-4393-b516-a0ddbcbf073b', '3fbfaa97-cff4-46d4-95ba-a95665e87c26', '2020-01-01 00:15:00+0', 1250.0, 411.0);
